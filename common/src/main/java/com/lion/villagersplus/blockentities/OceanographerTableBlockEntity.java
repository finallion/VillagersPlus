package com.lion.villagersplus.blockentities;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.blocks.OceanographerTableBlock;
import com.lion.villagersplus.init.VPBlockEntities;
import com.lion.villagersplus.mixin.EntityAccessorMixin;
import com.lion.villagersplus.util.DuckBucketable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.EntityBucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OceanographerTableBlockEntity extends BlockEntity implements Inventory, SidedInventory {
    public static final int FISH_SLOT = 4;

    private static final float BASE_SWIM_SPEED = 0.04F; // radians/tick
    private static final int TUMBLE_DURATION = 20;      // ticks for one axolotl barrel roll

    public static final float MIN_FISH_SCALE = 0.25F;
    public static final float MAX_FISH_SCALE = 1.75F;
    public static final float FISH_SCALE_STEP = 0.25F;

    // Corals stand close to the glass, so cap growth lower than the fish.
    public static final float MIN_CORAL_SCALE = 0.5F;
    public static final float MAX_CORAL_SCALE = 1.5F;
    public static final float CORAL_SCALE_STEP = 0.25F;

    private DefaultedList<ItemStack> inventory;
    private float fishScale = 1.0F;
    private float coralScale = 1.0F;
    private boolean stationary = false;
    private static final int[] SLOTS = new int[]{0, 1, 2, 3, 4};

    // --- Client-only display/animation state (never serialized) ---
    private Entity displayFish;
    private Item displayFishItem;
    private boolean varianceInit;
    private float dirSign = 1.0F;
    private float speedMul = 1.0F;
    private float swimRadius = 0.18F;
    private int animAge;
    private float swimAngle;
    private float prevSwimAngle;
    private float roll;
    private float prevRoll;
    private int rollTimer;
    private int rollCooldown = 200;
    private int puffTimer;
    private int puffCooldown = 160;

    // --- Multi-block tank (connected aquariums) ---
    /**
     * How many aquariums one connected tank may contain.
     *
     * <p>Enforced when blocks connect (see {@code OceanographerTableBlock.getPlacementState}), not
     * only here. It used to be only a cap on this scan, which meant a bigger structure still formed:
     * the flood fill then stopped early and everything past it was physically connected but never
     * scanned - so the fish's swimmable area ended in the middle of open water.
     */
    public static final int MAX_TANK_BLOCKS = 48;
    private static final int TANK_RESCAN_INTERVAL = 40;
    // Water spans y 2/16 .. 14/16 inside a block (floor slab / lid slab).
    private static final float FLOOR_HEIGHT = 0.125F;

    /** Offsets (relative to this block) of all blocks forming the connected tank, packed via BlockPos.asLong. */
    private final LongOpenHashSet tankBlocks = new LongOpenHashSet();
    private final LongArrayList tankBlockList = new LongArrayList();
    private int tankRescanTimer;
    private boolean multiTank;
    // Fish position in tank-local coordinates: (0.5, 0.5, 0.5) is the centre of this (home) block.
    private double fishX = 0.5D, fishY = 0.5D, fishZ = 0.5D;
    private double prevFishX = 0.5D, prevFishY = 0.5D, prevFishZ = 0.5D;
    private double targetX, targetY, targetZ;
    private boolean hasTarget;
    private int repathTimer;
    private float wanderYaw;
    private float wanderPitch;
    private float prevWanderPitch;
    private net.minecraft.util.math.random.Random wanderRandom;

    /** Block entities whose fish currently swims inside this block; see {@link #handOffFishToHost}. */
    private final List<OceanographerTableBlockEntity> guestFish = new ArrayList<>();
    /** Set while another aquarium of the tank has taken over drawing this block's fish. */
    private boolean fishHandedOff;

    public OceanographerTableBlockEntity(BlockPos pos, BlockState state) {
        super(VPBlockEntities.OCEANOGRAPHER_TABLE_BLOCK_ENTITY.get(), pos, state);
        this.inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    }

    public int size() {
        return this.inventory.size();
    }


    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        // createNbt runs writeData, which already serialises the whole inventory.
        return createNbt(registryLookup);
    }

    public float getFishScale() {
        return this.fishScale;
    }

    public boolean isStationary() {
        return this.stationary;
    }

    /** Toggles between free swimming and hovering animated in the centre. */
    public void toggleStationary() {
        this.stationary = !this.stationary;
        this.updateListeners();
    }

    /**
     * Adjusts the aquarium animal's display size by {@code delta}, clamped to [MIN, MAX].
     * @return true if the size actually changed (i.e. it was not already at the limit).
     */
    public boolean adjustFishScale(float delta) {
        float next = MathHelper.clamp(this.fishScale + delta, MIN_FISH_SCALE, MAX_FISH_SCALE);
        if (next == this.fishScale) {
            return false;
        }
        this.fishScale = next;
        this.updateListeners();
        return true;
    }

    public float getCoralScale() {
        return this.coralScale;
    }

    /**
     * Adjusts the corals' display size by {@code delta}, clamped to [MIN, MAX].
     * @return true if the size actually changed (i.e. it was not already at the limit).
     */
    public boolean adjustCoralScale(float delta) {
        float next = MathHelper.clamp(this.coralScale + delta, MIN_CORAL_SCALE, MAX_CORAL_SCALE);
        if (next == this.coralScale) {
            return false;
        }
        this.coralScale = next;
        this.updateListeners();
        return true;
    }

    /**
     * Removes the fish bucket (slot {@link #FISH_SLOT}) and resets the feeding state,
     * syncing the change to watching clients.
     * @return the stored fish bucket stack
     */
    public ItemStack extractFish() {
        ItemStack fishBucket = this.inventory.get(FISH_SLOT);
        this.inventory.set(FISH_SLOT, ItemStack.EMPTY);
        this.fishScale = 1.0F;
        this.stationary = false;
        this.updateListeners();
        return fishBucket;
    }

    /** Pops all planted corals out of the tank (used when this block loses its floor). */
    public void ejectCorals() {
        boolean changed = false;
        for (int slot = 0; slot < 4; slot++) {
            ItemStack coral = this.inventory.get(slot);
            if (!coral.isEmpty()) {
                net.minecraft.util.ItemScatterer.spawn(this.world, this.pos.getX() + 0.5D, this.pos.getY() + 1.1D, this.pos.getZ() + 0.5D, coral);
                this.inventory.set(slot, ItemStack.EMPTY);
                changed = true;
            }
        }
        if (changed) {
            // Freshly planted corals always start at normal size again.
            this.coralScale = 1.0F;
            this.updateListeners();
        }
    }

    public boolean insertCoral(ItemStack coral, int slot) {
        ItemStack itemStack = (ItemStack)this.inventory.get(slot);
        if (itemStack.isEmpty()) {
            this.inventory.set(slot, coral.split(1));
            this.updateListeners();
            return true;
        }
        return false;
    }


    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    public boolean isEmpty() {
        // Spelled out: Inventory has a nested Iterator type, so the bare name resolves to that one.
        java.util.Iterator<ItemStack> var1 = this.inventory.iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = (ItemStack)var1.next();
        } while(itemStack.isEmpty());

        return false;
    }

    private void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readData(view, this.inventory);
        this.fishScale = view.getFloat("FishScale", 1.0F);
        this.coralScale = view.getFloat("CoralScale", 1.0F);
        this.stationary = view.getBoolean("Stationary", false);
        // The fish stack may have changed (e.g. structure placement / initial sync); rebuild lazily.
        this.displayFishItem = null;
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        Inventories.writeData(view, this.inventory);
        view.putFloat("FishScale", this.fishScale);
        view.putFloat("CoralScale", this.coralScale);
        view.putBoolean("Stationary", this.stationary);
    }

    // ------------------------------------------------------------------
    // Client-side fish animation
    // ------------------------------------------------------------------

    public static void clientTick(World world, BlockPos pos, BlockState state, OceanographerTableBlockEntity be) {
        // Guests are dropped before they get a chance to re-register this tick, so an owner whose
        // fish has swum on does not keep being drawn by the block it left.
        be.guestFish.removeIf(guest -> guest.isRemoved() || !guest.isFishInBlock(be.pos));
        be.animateFish(world);
    }

    private void animateFish(World world) {
        ItemStack stack = this.inventory.get(FISH_SLOT);
        if (!(stack.getItem() instanceof EntityBucketItem)) {
            this.displayFish = null;
            this.displayFishItem = null;
            this.fishHandedOff = false;
            return;
        }

        if (stack.getItem() != this.displayFishItem || this.displayFish == null) {
            this.displayFishItem = stack.getItem();
            this.displayFish = createFish(world, stack);
        }
        if (this.displayFish == null) {
            return;
        }
        if (!this.varianceInit) {
            initVariance();
        }

        Entity fish = this.displayFish;
        ((EntityAccessorMixin) fish).setTouchingWater(true);

        this.animAge++;
        fish.age = this.animAge;

        // Connected aquariums form one large tank; rescan it periodically so breaking or
        // placing neighbouring aquariums updates the swimmable area.
        if (--this.tankRescanTimer <= 0) {
            this.rescanTank(world);
            this.tankRescanTimer = TANK_RESCAN_INTERVAL;
        }

        // Fin/limb animation always runs so the fish stays "alive" even when hovering in place.
        if (fish instanceof LivingEntity living) {
            living.limbAnimator.updateLimbs(1.0F, 0.4F, 1.0F);
        }

        // The axolotl model blends its poses from four flip-flops rather than from age and the limb
        // animator the way the fish models do, and nothing here runs the entity tick that advances
        // them. Drive them for a tank animal: always swimming, never beached or playing dead.
        if (fish instanceof AxolotlEntity axolotl) {
            axolotl.playingDeadFf.tick(false);
            axolotl.inWaterFf.tick(true);
            axolotl.onGroundFf.tick(false);
            axolotl.isMovingFf.tick(axolotl.limbAnimator.isLimbMoving());
        }

        this.prevFishX = this.fishX;
        this.prevFishY = this.fishY;
        this.prevFishZ = this.fishZ;
        this.prevWanderPitch = this.wanderPitch;

        if (this.stationary) {
            // Calming food: hover animated in the centre, facing a fixed direction (no travel, no turning).
            this.prevSwimAngle = this.swimAngle;
            this.fishX = 0.5D;
            this.fishY = 0.5D;
            this.fishZ = 0.5D;
            this.wanderPitch = 0.0F;
            setFishYaw(fish, 0.0F, true);
        } else if (!this.multiTank) {
            // Single tub: circular path; heading = tangent (reversed when swimming the other way round).
            this.prevSwimAngle = this.swimAngle;
            this.swimAngle += BASE_SWIM_SPEED * this.speedMul * this.dirSign;
            float radius = MathHelper.clamp(this.swimRadius / this.fishScale, 0.06F, 0.28F);
            this.fishX = 0.5D + radius * MathHelper.cos(this.swimAngle);
            this.fishY = 0.5D;
            this.fishZ = 0.5D + radius * MathHelper.sin(this.swimAngle);
            this.wanderPitch = approachDegrees(this.wanderPitch, 0.0F, 3.0F);
            float yawDeg = (float) Math.toDegrees(this.swimAngle) + (this.dirSign < 0 ? 180.0F : 0.0F);
            this.wanderYaw = yawDeg;
            setFishYaw(fish, yawDeg, false);
        } else {
            // Multi-block tank: wander freely through the whole connected water volume.
            this.wanderTank();
            setFishYaw(fish, this.wanderYaw, false);
        }

        // Pufferfish: inflate every so often, hold, then deflate.
        if (fish instanceof PufferfishEntity puffer) {
            if (this.puffTimer > 0) {
                this.puffTimer--;
                if (this.puffTimer == 0) {
                    puffer.setPuffState(0);
                }
            } else if (--this.puffCooldown <= 0) {
                puffer.setPuffState(2);
                this.puffTimer = 60;
                this.puffCooldown = 180 + (int) (this.speedMul * 60.0F);
            }
        }

        // Axolotl: occasionally do a playful barrel roll around its own axis.
        this.prevRoll = this.roll;
        if (fish instanceof AxolotlEntity) {
            if (this.rollTimer > 0) {
                this.rollTimer--;
                this.roll += 360.0F / TUMBLE_DURATION;
            } else {
                this.roll = 0.0F;
                this.prevRoll = 0.0F;
                if (--this.rollCooldown <= 0) {
                    this.rollTimer = TUMBLE_DURATION;
                    this.rollCooldown = 160 + (int) (this.speedMul * 80.0F);
                }
            }
        }

        this.handOffFishToHost(world);
    }

    /**
     * Hands the fish to the aquarium block it is currently swimming in.
     *
     * <p>The fish is animated in tank-local coordinates, so in a connected tank it travels several
     * blocks - and with them across chunk section borders - away from the block entity that owns it.
     * Drawing it from the owner made its visibility depend on whether the *owner's* section survived
     * culling rather than the one the fish is actually in, so it blinked out while the glass around
     * it stayed on screen. Handing it over ties it to the section the player is looking at.
     *
     * <p>If the target block is not an aquarium after all (a stale scan, a half-loaded chunk), the
     * hand-over is skipped and the owner keeps drawing it - a fish in the wrong section beats no
     * fish at all.
     */
    private void handOffFishToHost(World world) {
        this.fishHandedOff = false;
        int bx = MathHelper.floor(this.fishX);
        int by = MathHelper.floor(this.fishY);
        int bz = MathHelper.floor(this.fishZ);
        if (bx == 0 && by == 0 && bz == 0) {
            return; // still in its own block
        }
        if (world.getBlockEntity(this.pos.add(bx, by, bz)) instanceof OceanographerTableBlockEntity host
                && host != this) {
            if (!host.guestFish.contains(this)) {
                host.guestFish.add(this);
            }
            this.fishHandedOff = true;
        }
    }

    /** {@return whether this block's fish currently sits in the aquarium at {@code blockPos}} */
    public boolean isFishInBlock(BlockPos blockPos) {
        return this.displayFish != null
                && this.pos.getX() + MathHelper.floor(this.fishX) == blockPos.getX()
                && this.pos.getY() + MathHelper.floor(this.fishY) == blockPos.getY()
                && this.pos.getZ() + MathHelper.floor(this.fishZ) == blockPos.getZ();
    }

    /** {@return whether this block entity still draws its own fish} */
    public boolean drawsOwnFish() {
        return this.displayFish != null && !this.fishHandedOff;
    }

    /** {@return the owners whose fish this block currently draws on their behalf} */
    public List<OceanographerTableBlockEntity> getGuestFish() {
        return this.guestFish;
    }

    private static void setFishYaw(Entity fish, float yawDeg, boolean snap) {
        if (fish instanceof LivingEntity living) {
            living.lastBodyYaw = snap ? yawDeg : living.bodyYaw;
            living.bodyYaw = yawDeg;
            living.lastHeadYaw = snap ? yawDeg : living.headYaw;
            living.headYaw = yawDeg;
            living.lastYaw = snap ? yawDeg : living.getYaw();
            living.setYaw(yawDeg);
        }
    }

    /**
     * Flood-fills the connected aquarium blocks around this one (tank-local offsets, all six
     * directions). Follows the blocks' connection properties, so sneak-placed standalone
     * aquariums are never counted into the tank.
     */
    private void rescanTank(World world) {
        this.tankBlocks.clear();
        this.tankBlockList.clear();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(this.pos);
        this.tankBlocks.add(BlockPos.asLong(0, 0, 0));
        while (!queue.isEmpty() && this.tankBlocks.size() < MAX_TANK_BLOCKS) {
            BlockPos current = queue.poll();
            BlockState currentState = world.getBlockState(current);
            if (!(currentState.getBlock() instanceof OceanographerTableBlock)) {
                continue;
            }
            for (Direction direction : Direction.values()) {
                if (!currentState.get(OceanographerTableBlock.connectionProperty(direction))) {
                    continue;
                }
                BlockPos next = current.offset(direction);
                long key = BlockPos.asLong(next.getX() - this.pos.getX(), next.getY() - this.pos.getY(), next.getZ() - this.pos.getZ());
                if (!this.tankBlocks.contains(key) && world.getBlockState(next).getBlock() instanceof OceanographerTableBlock) {
                    this.tankBlocks.add(key);
                    queue.add(next);
                }
            }
        }
        this.tankBlockList.addAll(this.tankBlocks);
        boolean wasMulti = this.multiTank;
        this.multiTank = this.tankBlocks.size() > 1;
        if (this.multiTank != wasMulti) {
            this.hasTarget = false;
        }
    }

    private void wanderTank() {
        float inset = this.wallInset();
        float insetY = this.verticalInset();
        if (!this.isInsideTank(this.fishX, this.fishY, this.fishZ, 0.0F, 0.0F)) {
            // The tank shrank under the fish (a block was broken): snap back home.
            this.fishX = this.prevFishX = 0.5D;
            this.fishY = this.prevFishY = 0.5D;
            this.fishZ = this.prevFishZ = 0.5D;
            this.hasTarget = false;
        } else if (!this.isInsideTank(this.fishX, this.fishY, this.fishZ, inset, insetY)) {
            // The clearance grew around the fish (fish food made it bigger, or a neighbouring
            // aquarium was broken and that side gained glass). Every wander step below would be
            // rejected, so swim clear of the wall first instead of stalling inside the glass.
            this.swimClearOfWalls(inset, insetY);
            return;
        }
        double dx = this.targetX - this.fishX;
        double dy = this.targetY - this.fishY;
        double dz = this.targetZ - this.fishZ;
        if (!this.hasTarget || dx * dx + dy * dy + dz * dz < 0.04D || --this.repathTimer <= 0) {
            this.pickWanderTarget(inset, insetY);
            dx = this.targetX - this.fishX;
            dz = this.targetZ - this.fishZ;
        }
        // Steer smoothly toward the target, then swim along the current heading; when the target
        // is (almost) straight above/below, keep the current heading and just rise/sink.
        float maxTurn = 4.0F * this.speedMul;
        if (dx * dx + dz * dz > 0.0025D) {
            float desiredYaw = (float) Math.toDegrees(MathHelper.atan2(-dx, dz));
            float turn = MathHelper.clamp(MathHelper.subtractAngles(this.wanderYaw, desiredYaw), -maxTurn, maxTurn);
            this.wanderYaw = MathHelper.wrapDegrees(this.wanderYaw + turn);
        }
        double speed = 0.016D * this.speedMul;
        double rad = Math.toRadians(this.wanderYaw);
        double vy = MathHelper.clamp(this.targetY - this.fishY, -speed * 0.5D, speed * 0.5D);
        double nx = this.fishX - Math.sin(rad) * speed;
        double ny = this.fishY + vy;
        double nz = this.fishZ + Math.cos(rad) * speed;
        if (this.isInsideTank(nx, ny, nz, inset, insetY)) {
            this.fishX = nx;
            this.fishY = ny;
            this.fishZ = nz;
            // Nose up/down with the vertical motion so the fish doesn't ride an elevator.
            float desiredPitch = (float) -Math.toDegrees(MathHelper.atan2(vy, speed));
            this.wanderPitch = approachDegrees(this.wanderPitch, desiredPitch, 3.0F);
        } else if (this.isInsideTank(nx, this.fishY, nz, inset, insetY)) {
            // Vertically blocked (floor/lid): keep swimming level.
            this.fishX = nx;
            this.fishZ = nz;
            this.wanderPitch = approachDegrees(this.wanderPitch, 0.0F, 3.0F);
        } else {
            this.hasTarget = false; // blocked by a wall: turn in place toward a new target
            this.wanderPitch = approachDegrees(this.wanderPitch, 0.0F, 3.0F);
        }
    }

    /**
     * Swims the fish back into the part of its block that keeps the required clearance from the
     * glass, floor and lid. Used when that region shrank around the fish rather than the fish
     * swimming into it, which the regular wander steps cannot resolve: from inside the margin
     * every candidate step is still inside it, so the fish would sit motionless in the glass.
     */
    private void swimClearOfWalls(float inset, float insetY) {
        int bx = MathHelper.floor(this.fishX);
        int by = MathHelper.floor(this.fishY);
        int bz = MathHelper.floor(this.fishZ);
        float insetFloor = FLOOR_HEIGHT + insetY;
        double safeX = clampBetweenWalls(this.fishX, bx, inset,
                this.tankBlocks.contains(BlockPos.asLong(bx - 1, by, bz)),
                this.tankBlocks.contains(BlockPos.asLong(bx + 1, by, bz)));
        double safeY = clampBetweenWalls(this.fishY, by, insetFloor,
                this.tankBlocks.contains(BlockPos.asLong(bx, by - 1, bz)),
                this.tankBlocks.contains(BlockPos.asLong(bx, by + 1, bz)));
        double safeZ = clampBetweenWalls(this.fishZ, bz, inset,
                this.tankBlocks.contains(BlockPos.asLong(bx, by, bz - 1)),
                this.tankBlocks.contains(BlockPos.asLong(bx, by, bz + 1)));

        double dx = safeX - this.fishX;
        double dy = safeY - this.fishY;
        double dz = safeZ - this.fishZ;
        double speed = 0.016D * this.speedMul;
        this.fishX += MathHelper.clamp(dx, -speed, speed);
        this.fishY += MathHelper.clamp(dy, -speed, speed);
        this.fishZ += MathHelper.clamp(dz, -speed, speed);

        // Face the way it is backing out, and repath once it is clear again.
        if (dx * dx + dz * dz > 1.0E-6D) {
            float maxTurn = 4.0F * this.speedMul;
            float desiredYaw = (float) Math.toDegrees(MathHelper.atan2(-dx, dz));
            float turn = MathHelper.clamp(MathHelper.subtractAngles(this.wanderYaw, desiredYaw), -maxTurn, maxTurn);
            this.wanderYaw = MathHelper.wrapDegrees(this.wanderYaw + turn);
        }
        this.wanderPitch = approachDegrees(this.wanderPitch, 0.0F, 3.0F);
        this.hasTarget = false;
    }

    /**
     * Clamps one coordinate into the part of cell {@code cell} that stays {@code margin} away from
     * the walls on either side; a side with a connected neighbour has no wall to clear. The extra
     * epsilon keeps the result strictly inside {@link #isInsideTank}'s bounds, so the recovery
     * always terminates instead of hovering on the boundary.
     */
    private static double clampBetweenWalls(double value, int cell, float margin, boolean openLow, boolean openHigh) {
        double min = openLow ? cell : cell + margin + 1.0E-3D;
        double max = openHigh ? cell + 1.0D : cell + 1.0D - margin - 1.0E-3D;
        if (min > max) {
            return cell + 0.5D; // clearances overlap (fish too big for the cell): aim for the centre
        }
        return MathHelper.clamp(value, min, max);
    }

    private void pickWanderTarget(float inset, float insetY) {
        var r = this.wanderRandom;
        for (int attempt = 0; attempt < 10; attempt++) {
            long cell = this.tankBlockList.getLong(r.nextInt(this.tankBlockList.size()));
            BlockPos rel = BlockPos.fromLong(cell);
            double tx = rel.getX() + inset + r.nextDouble() * (1.0D - 2.0D * inset);
            double ty = rel.getY() + FLOOR_HEIGHT + insetY + r.nextDouble() * Math.max(0.0D, 1.0D - 2.0D * (FLOOR_HEIGHT + insetY));
            double tz = rel.getZ() + inset + r.nextDouble() * (1.0D - 2.0D * inset);
            if (this.isPathClear(tx, ty, tz, inset, insetY)) {
                this.targetX = tx;
                this.targetY = ty;
                this.targetZ = tz;
                this.hasTarget = true;
                this.repathTimer = 200 + r.nextInt(200);
                return;
            }
        }
        // Fallback: drift to the centre of the block the fish is currently in.
        this.targetX = Math.floor(this.fishX) + 0.5D;
        this.targetY = Math.floor(this.fishY) + 0.5D;
        this.targetZ = Math.floor(this.fishZ) + 0.5D;
        this.hasTarget = true;
        this.repathTimer = 100;
    }

    /** Keeps larger (fed) fish further away from the glass so they don't clip through it. */
    private float wallInset() {
        return MathHelper.clamp(0.18F * this.fishScale + 0.12F, 0.2F, 0.45F);
    }

    /** Extra distance to floor and lid on top of the slab height. */
    private float verticalInset() {
        return MathHelper.clamp(0.1F * this.fishScale + 0.08F, 0.1F, 0.3F);
    }

    private boolean isInsideTank(double x, double y, double z, float inset, float insetY) {
        int bx = MathHelper.floor(x);
        int by = MathHelper.floor(y);
        int bz = MathHelper.floor(z);
        if (!this.tankBlocks.contains(BlockPos.asLong(bx, by, bz))) {
            return false;
        }
        // Only sides without a connected neighbour have glass / floor / lid; keep the inset there.
        if (!this.tankBlocks.contains(BlockPos.asLong(bx - 1, by, bz)) && x - bx < inset) return false;
        if (!this.tankBlocks.contains(BlockPos.asLong(bx + 1, by, bz)) && bx + 1 - x < inset) return false;
        if (!this.tankBlocks.contains(BlockPos.asLong(bx, by, bz - 1)) && z - bz < inset) return false;
        if (!this.tankBlocks.contains(BlockPos.asLong(bx, by, bz + 1)) && bz + 1 - z < inset) return false;
        if (!this.tankBlocks.contains(BlockPos.asLong(bx, by - 1, bz)) && y - by < FLOOR_HEIGHT + insetY) return false;
        if (!this.tankBlocks.contains(BlockPos.asLong(bx, by + 1, bz)) && by + 1 - y < FLOOR_HEIGHT + insetY) return false;
        return true;
    }

    private boolean isPathClear(double x1, double y1, double z1, float inset, float insetY) {
        double x0 = this.fishX, y0 = this.fishY, z0 = this.fishZ;
        double dist = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0) + (z1 - z0) * (z1 - z0));
        int steps = Math.max(1, (int) Math.ceil(dist / 0.2D));
        for (int i = 1; i <= steps; i++) {
            double t = (double) i / steps;
            if (!this.isInsideTank(x0 + (x1 - x0) * t, y0 + (y1 - y0) * t, z0 + (z1 - z0) * t, inset, insetY)) {
                return false;
            }
        }
        return true;
    }

    private static float approachDegrees(float current, float target, float step) {
        float diff = MathHelper.clamp(MathHelper.subtractAngles(current, target), -step, step);
        return current + diff;
    }

    /** Deterministic per-tub variance so neighbouring aquariums don't all swim identically. */
    private void initVariance() {
        net.minecraft.util.math.random.Random r = net.minecraft.util.math.random.Random.create(this.pos.asLong());
        this.dirSign = r.nextBoolean() ? 1.0F : -1.0F;
        this.speedMul = 0.7F + r.nextFloat() * 0.8F;      // 0.7 .. 1.5
        this.swimRadius = 0.14F + r.nextFloat() * 0.10F;  // 0.14 .. 0.24
        this.swimAngle = r.nextFloat() * MathHelper.TAU;
        this.prevSwimAngle = this.swimAngle;
        this.rollCooldown = 120 + r.nextInt(200);
        this.puffCooldown = 100 + r.nextInt(160);
        this.wanderRandom = net.minecraft.util.math.random.Random.create(this.pos.asLong() * 31L + 17L);
        this.wanderYaw = r.nextFloat() * 360.0F;
        this.varianceInit = true;
    }

    @Nullable
    private Entity createFish(World world, ItemStack stack) {
        if (!(stack.getItem() instanceof EntityBucketItem bucketItem)) {
            return null;
        }
        EntityType<?> type = ((DuckBucketable) bucketItem).getEntityType();
        if (type == null) {
            return null;
        }
        Entity fish = type.create(world, SpawnReason.LOAD);
        if (fish == null) {
            return null;
        }

        NbtComponent bucketData = stack.get(DataComponentTypes.BUCKET_ENTITY_DATA);
        if (bucketData != null) {
            NbtCompound entityNbt = bucketData.copyNbt();
            try (ErrorReporter.Logging reporter =
                         new ErrorReporter.Logging(fish.getErrorReporterContext(), VillagersPlus.LOGGER)) {
                fish.readData(NbtReadView.create(reporter, world.getRegistryManager(), entityNbt));
            }
        }

        fish.copyComponentsFrom(stack);
        ((EntityAccessorMixin) fish).setTouchingWater(true);
        return fish;
    }

    @Nullable
    public Entity getDisplayFish() {
        return this.displayFish;
    }

    public boolean isMultiTank() {
        return this.multiTank;
    }

    /** Interpolated fish position in tank-local coordinates (0.5 = centre of this block). */
    public float getFishX(float tickDelta) {
        return (float) MathHelper.lerp((double) tickDelta, this.prevFishX, this.fishX);
    }

    public float getFishY(float tickDelta) {
        return (float) MathHelper.lerp((double) tickDelta, this.prevFishY, this.fishY);
    }

    public float getFishZ(float tickDelta) {
        return (float) MathHelper.lerp((double) tickDelta, this.prevFishZ, this.fishZ);
    }

    /** Interpolated nose-up/-down angle while wandering vertically. */
    public float getFishPitch(float tickDelta) {
        return MathHelper.lerpAngleDegrees(tickDelta, this.prevWanderPitch, this.wanderPitch);
    }

    public float getRoll(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevRoll, this.roll);
    }

    public float getAnimAge(float tickDelta) {
        return this.animAge + tickDelta;
    }

    // ------------------------------------------------------------------

    public ItemStack getStack(int slot) {
        return slot >= 0 && slot < this.inventory.size() ? (ItemStack)this.inventory.get(slot) : ItemStack.EMPTY;
    }

    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    public void setStack(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.inventory.size()) {
            this.inventory.set(slot, stack);
        }
    }

    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return !(player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) > 64.0D);
        }
    }

    public void clear() {
        this.inventory.clear();
    }

    public int[] getAvailableSlots(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        // Corals and the fish only go in via right-click. Without this, hoppers could push
        // arbitrary items into the display slots.
        return false;
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

}
