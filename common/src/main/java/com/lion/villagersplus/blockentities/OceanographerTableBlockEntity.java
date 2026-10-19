package com.lion.villagersplus.blockentities;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.blocks.OceanographerTableBlock;
import com.lion.villagersplus.init.VPBlockEntities;
import com.lion.villagersplus.mixin.EntityAccessorMixin;
import com.lion.villagersplus.util.DuckBucketable;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.fish.Pufferfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class OceanographerTableBlockEntity extends BlockEntity implements Container, WorldlyContainer {
    public static final int FISH_SLOT = 4;

    private static final float BASE_SWIM_SPEED = 0.04F;
    private static final int TUMBLE_DURATION = 20;

    public static final float MIN_FISH_SCALE = 0.25F;
    public static final float MAX_FISH_SCALE = 1.75F;
    public static final float FISH_SCALE_STEP = 0.25F;

    // Corals stand close to the glass, so their growth cap is lower.
    public static final float MIN_CORAL_SCALE = 0.5F;
    public static final float MAX_CORAL_SCALE = 1.5F;
    public static final float CORAL_SCALE_STEP = 0.25F;

    private NonNullList<ItemStack> inventory;
    private float fishScale = 1.0F;
    private float coralScale = 1.0F;
    private boolean stationary = false;
    private static final int[] SLOTS = new int[]{0, 1, 2, 3, 4};

    private Entity displayFish;
    private Item displayFishItem;
    private Item failedFishItem;
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

    /// The limit applies when blocks connect, not only in the scan.
    public static final int MAX_TANK_BLOCKS = 48;
    private static final int TANK_RESCAN_INTERVAL = 40;
    private static final float FLOOR_HEIGHT = 0.125F;

    private final LongOpenHashSet tankBlocks = new LongOpenHashSet();
    private final LongArrayList tankBlockList = new LongArrayList();
    private int tankRescanTimer;
    private boolean multiTank;
    private double fishX = 0.5D, fishY = 0.5D, fishZ = 0.5D;
    private double prevFishX = 0.5D, prevFishY = 0.5D, prevFishZ = 0.5D;
    private double targetX, targetY, targetZ;
    private boolean hasTarget;
    private int repathTimer;
    private float wanderYaw;
    private float wanderPitch;
    private float prevWanderPitch;
    private net.minecraft.util.RandomSource wanderRandom;

    private final List<OceanographerTableBlockEntity> guestFish = new ArrayList<>();
    /// Set while another aquarium of the tank draws this block's fish.
    private boolean fishHandedOff;

    public OceanographerTableBlockEntity(BlockPos pos, BlockState state) {
        super(VPBlockEntities.OCEANOGRAPHER_TABLE_BLOCK_ENTITY.get(), pos, state);
        this.inventory = NonNullList.withSize(5, ItemStack.EMPTY);
    }

    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }

    public float getFishScale() {
        return this.fishScale;
    }

    public boolean isStationary() {
        return this.stationary;
    }

    public void toggleStationary() {
        this.stationary = !this.stationary;
        this.updateListeners();
    }

    public boolean adjustFishScale(float delta) {
        float next = Mth.clamp(this.fishScale + delta, MIN_FISH_SCALE, MAX_FISH_SCALE);

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

    public boolean adjustCoralScale(float delta) {
        float next = Mth.clamp(this.coralScale + delta, MIN_CORAL_SCALE, MAX_CORAL_SCALE);

        if (next == this.coralScale) {
            return false;
        }

        this.coralScale = next;
        this.updateListeners();
        return true;
    }

    public ItemStack extractFish() {
        ItemStack fishBucket = this.inventory.get(FISH_SLOT);
        this.inventory.set(FISH_SLOT, ItemStack.EMPTY);
        this.fishScale = 1.0F;
        this.stationary = false;
        this.updateListeners();
        return fishBucket;
    }

    public void ejectCorals() {
        boolean changed = false;
        for (int slot = 0; slot < 4; slot++) {
            ItemStack coral = this.inventory.get(slot);

            if (!coral.isEmpty()) {
                net.minecraft.world.Containers.dropItemStack(this.level, this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 1.1D, this.worldPosition.getZ() + 0.5D, coral);
                this.inventory.set(slot, ItemStack.EMPTY);
                changed = true;
            }
        }

        if (changed) {
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

    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    public boolean isEmpty() {
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
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(view, this.inventory);
        this.fishScale = view.getFloatOr("FishScale", 1.0F);
        this.coralScale = view.getFloatOr("CoralScale", 1.0F);
        this.stationary = view.getBooleanOr("Stationary", false);
        this.displayFishItem = null;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        ContainerHelper.saveAllItems(view, this.inventory);
        view.putFloat("FishScale", this.fishScale);
        view.putFloat("CoralScale", this.coralScale);
        view.putBoolean("Stationary", this.stationary);
    }

    public static void clientTick(Level world, BlockPos pos, BlockState state, OceanographerTableBlockEntity be) {
        // Drop guests before they re-register, so a fish that moved on is not drawn twice.
        be.guestFish.removeIf(guest -> guest.isRemoved() || !guest.isFishInBlock(be.worldPosition));
        be.animateFish(world);
    }

    private void animateFish(Level world) {
        ItemStack stack = this.inventory.get(FISH_SLOT);

        if (!(stack.getItem() instanceof MobBucketItem)) {
            this.displayFish = null;
            this.displayFishItem = null;
            this.failedFishItem = null;
            this.fishHandedOff = false;
            return;
        }

        if (stack.getItem() == this.failedFishItem) {
            this.displayFish = null;
            return;
        }

        if (stack.getItem() != this.displayFishItem || this.displayFish == null) {
            this.displayFishItem = stack.getItem();
            this.displayFish = createFish(world, stack);

            if (this.displayFish == null) {
                this.failedFishItem = stack.getItem();
            }
        }

        if (this.displayFish == null) {
            return;
        }

        if (!this.varianceInit) {
            initVariance();
        }

        Entity fish = this.displayFish;
        ((EntityAccessorMixin) fish).setWasTouchingWater(true);

        this.animAge++;
        fish.tickCount = this.animAge;

        // Rescan the connected tank so neighbour changes update the swimmable area.
        if (--this.tankRescanTimer <= 0) {
            this.rescanTank(world);
            this.tankRescanTimer = TANK_RESCAN_INTERVAL;
        }

        if (fish instanceof LivingEntity living) {
            living.walkAnimation.update(1.0F, 0.4F, 1.0F);
        }

        // The axolotl model needs its flip-flops driven here, because no entity tick runs.
        if (fish instanceof Axolotl axolotl) {
            axolotl.playingDeadAnimator.tick(false);
            axolotl.inWaterAnimator.tick(true);
            axolotl.onGroundAnimator.tick(false);
            axolotl.movingAnimator.tick(axolotl.walkAnimation.isMoving());
        }

        this.prevFishX = this.fishX;
        this.prevFishY = this.fishY;
        this.prevFishZ = this.fishZ;
        this.prevWanderPitch = this.wanderPitch;

        if (this.stationary) {
            this.prevSwimAngle = this.swimAngle;
            this.fishX = 0.5D;
            this.fishY = 0.5D;
            this.fishZ = 0.5D;
            this.wanderPitch = 0.0F;
            setFishYaw(fish, 0.0F, true);
        } else if (!this.multiTank) {
            this.prevSwimAngle = this.swimAngle;
            this.swimAngle += BASE_SWIM_SPEED * this.speedMul * this.dirSign;
            float radius = Mth.clamp(this.swimRadius / this.fishScale, 0.06F, 0.28F);
            this.fishX = 0.5D + radius * Mth.cos(this.swimAngle);
            this.fishY = 0.5D;
            this.fishZ = 0.5D + radius * Mth.sin(this.swimAngle);
            this.wanderPitch = approachDegrees(this.wanderPitch, 0.0F, 3.0F);
            float yawDeg = (float) Math.toDegrees(this.swimAngle) + (this.dirSign < 0 ? 180.0F : 0.0F);
            this.wanderYaw = yawDeg;
            setFishYaw(fish, yawDeg, false);
        } else {
            this.wanderTank();
            setFishYaw(fish, this.wanderYaw, false);
        }

        if (fish instanceof Pufferfish puffer) {
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

        this.prevRoll = this.roll;

        if (fish instanceof Axolotl) {
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

    /// Hands the fish to the aquarium block it swims in, so section culling uses the right block.
    private void handOffFishToHost(Level world) {
        this.fishHandedOff = false;
        int bx = Mth.floor(this.fishX);
        int by = Mth.floor(this.fishY);
        int bz = Mth.floor(this.fishZ);

        if (bx == 0 && by == 0 && bz == 0) {
            return;
        }

        if (world.getBlockEntity(this.worldPosition.offset(bx, by, bz)) instanceof OceanographerTableBlockEntity host
                && host != this) {
            if (!host.guestFish.contains(this)) {
                host.guestFish.add(this);
            }

            this.fishHandedOff = true;
        }
    }

    public boolean isFishInBlock(BlockPos blockPos) {
        return this.displayFish != null
                && this.worldPosition.getX() + Mth.floor(this.fishX) == blockPos.getX()
                && this.worldPosition.getY() + Mth.floor(this.fishY) == blockPos.getY()
                && this.worldPosition.getZ() + Mth.floor(this.fishZ) == blockPos.getZ();
    }

    public boolean drawsOwnFish() {
        return this.displayFish != null && !this.fishHandedOff;
    }

    public List<OceanographerTableBlockEntity> getGuestFish() {
        return this.guestFish;
    }

    private static void setFishYaw(Entity fish, float yawDeg, boolean snap) {
        if (fish instanceof LivingEntity living) {
            living.yBodyRotO = snap ? yawDeg : living.yBodyRot;
            living.yBodyRot = yawDeg;
            living.yHeadRotO = snap ? yawDeg : living.yHeadRot;
            living.yHeadRot = yawDeg;
            living.yRotO = snap ? yawDeg : living.getYRot();
            living.setYRot(yawDeg);
        }
    }

    /// Flood-fills the connected aquariums around this one as tank-local offsets.
    private void rescanTank(Level world) {
        this.tankBlocks.clear();
        this.tankBlockList.clear();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(this.worldPosition);
        this.tankBlocks.add(BlockPos.asLong(0, 0, 0));
        while (!queue.isEmpty() && this.tankBlocks.size() < MAX_TANK_BLOCKS) {
            BlockPos current = queue.poll();
            BlockState currentState = world.getBlockState(current);

            if (!(currentState.getBlock() instanceof OceanographerTableBlock)) {
                continue;
            }

            for (Direction direction : Direction.values()) {
                if (!currentState.getValue(OceanographerTableBlock.connectionProperty(direction))) {
                    continue;
                }

                BlockPos next = current.relative(direction);
                long key = BlockPos.asLong(next.getX() - this.worldPosition.getX(), next.getY() - this.worldPosition.getY(), next.getZ() - this.worldPosition.getZ());

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
            this.fishX = this.prevFishX = 0.5D;
            this.fishY = this.prevFishY = 0.5D;
            this.fishZ = this.prevFishZ = 0.5D;
            this.hasTarget = false;
        } else if (!this.isInsideTank(this.fishX, this.fishY, this.fishZ, inset, insetY)) {
            // Swim clear of the glass first, because every wander step would fail from inside the margin.
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

        float maxTurn = 4.0F * this.speedMul;

        if (dx * dx + dz * dz > 0.0025D) {
            float desiredYaw = (float) Math.toDegrees(Mth.atan2(-dx, dz));
            float turn = Mth.clamp(Mth.degreesDifference(this.wanderYaw, desiredYaw), -maxTurn, maxTurn);
            this.wanderYaw = Mth.wrapDegrees(this.wanderYaw + turn);
        }

        double speed = 0.016D * this.speedMul;
        double rad = Math.toRadians(this.wanderYaw);
        double vy = Mth.clamp(this.targetY - this.fishY, -speed * 0.5D, speed * 0.5D);
        double nx = this.fishX - Math.sin(rad) * speed;
        double ny = this.fishY + vy;
        double nz = this.fishZ + Math.cos(rad) * speed;

        if (this.isInsideTank(nx, ny, nz, inset, insetY)) {
            this.fishX = nx;
            this.fishY = ny;
            this.fishZ = nz;
            float desiredPitch = (float) -Math.toDegrees(Mth.atan2(vy, speed));
            this.wanderPitch = approachDegrees(this.wanderPitch, desiredPitch, 3.0F);
        } else if (this.isInsideTank(nx, this.fishY, nz, inset, insetY)) {
            this.fishX = nx;
            this.fishZ = nz;
            this.wanderPitch = approachDegrees(this.wanderPitch, 0.0F, 3.0F);
        } else {
            this.hasTarget = false;
            this.wanderPitch = approachDegrees(this.wanderPitch, 0.0F, 3.0F);
        }
    }

    /// Swims the fish back out of the clearance margin, which wander steps cannot do.
    private void swimClearOfWalls(float inset, float insetY) {
        int bx = Mth.floor(this.fishX);
        int by = Mth.floor(this.fishY);
        int bz = Mth.floor(this.fishZ);
        float insetFloor = FLOOR_HEIGHT + insetY;
        double safeX = clampBetweenWalls(
                this.fishX,
                bx,
                inset,
                this.tankBlocks.contains(BlockPos.asLong(bx - 1, by, bz)),
                this.tankBlocks.contains(BlockPos.asLong(bx + 1, by, bz))
        );
        double safeY = clampBetweenWalls(
                this.fishY,
                by,
                insetFloor,
                this.tankBlocks.contains(BlockPos.asLong(bx, by - 1, bz)),
                this.tankBlocks.contains(BlockPos.asLong(bx, by + 1, bz))
        );
        double safeZ = clampBetweenWalls(
                this.fishZ,
                bz,
                inset,
                this.tankBlocks.contains(BlockPos.asLong(bx, by, bz - 1)),
                this.tankBlocks.contains(BlockPos.asLong(bx, by, bz + 1))
        );

        double dx = safeX - this.fishX;
        double dy = safeY - this.fishY;
        double dz = safeZ - this.fishZ;
        double speed = 0.016D * this.speedMul;
        this.fishX += Mth.clamp(dx, -speed, speed);
        this.fishY += Mth.clamp(dy, -speed, speed);
        this.fishZ += Mth.clamp(dz, -speed, speed);

        if (dx * dx + dz * dz > 1.0E-6D) {
            float maxTurn = 4.0F * this.speedMul;
            float desiredYaw = (float) Math.toDegrees(Mth.atan2(-dx, dz));
            float turn = Mth.clamp(Mth.degreesDifference(this.wanderYaw, desiredYaw), -maxTurn, maxTurn);
            this.wanderYaw = Mth.wrapDegrees(this.wanderYaw + turn);
        }

        this.wanderPitch = approachDegrees(this.wanderPitch, 0.0F, 3.0F);
        this.hasTarget = false;
    }

    /// Clamps one coordinate into the cell, `margin` away from walls. Sides with a neighbour have no wall.
    private static double clampBetweenWalls(double value, int cell, float margin, boolean openLow, boolean openHigh) {
        double min = openLow ? cell : cell + margin + 1.0E-3D;
        double max = openHigh ? cell + 1.0D : cell + 1.0D - margin - 1.0E-3D;

        if (min > max) {
            return cell + 0.5D;
        }

        return Mth.clamp(value, min, max);
    }

    private void pickWanderTarget(float inset, float insetY) {
        var r = this.wanderRandom;
        for (int attempt = 0; attempt < 10; attempt++) {
            long cell = this.tankBlockList.getLong(r.nextInt(this.tankBlockList.size()));
            BlockPos rel = BlockPos.of(cell);
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
        this.targetX = Math.floor(this.fishX) + 0.5D;
        this.targetY = Math.floor(this.fishY) + 0.5D;
        this.targetZ = Math.floor(this.fishZ) + 0.5D;
        this.hasTarget = true;
        this.repathTimer = 100;
    }

    private float wallInset() {
        return Mth.clamp(0.18F * this.fishScale + 0.12F, 0.2F, 0.45F);
    }

    private float verticalInset() {
        return Mth.clamp(0.1F * this.fishScale + 0.08F, 0.1F, 0.3F);
    }

    private boolean isInsideTank(double x, double y, double z, float inset, float insetY) {
        int bx = Mth.floor(x);
        int by = Mth.floor(y);
        int bz = Mth.floor(z);

        if (!this.tankBlocks.contains(BlockPos.asLong(bx, by, bz))) {
            return false;
        }

        if (!this.tankBlocks.contains(BlockPos.asLong(bx - 1, by, bz)) && x - bx < inset) {
            return false;
        }

        if (!this.tankBlocks.contains(BlockPos.asLong(bx + 1, by, bz)) && bx + 1 - x < inset) {
            return false;
        }

        if (!this.tankBlocks.contains(BlockPos.asLong(bx, by, bz - 1)) && z - bz < inset) {
            return false;
        }

        if (!this.tankBlocks.contains(BlockPos.asLong(bx, by, bz + 1)) && bz + 1 - z < inset) {
            return false;
        }

        if (!this.tankBlocks.contains(BlockPos.asLong(bx, by - 1, bz)) && y - by < FLOOR_HEIGHT + insetY) {
            return false;
        }

        if (!this.tankBlocks.contains(BlockPos.asLong(bx, by + 1, bz)) && by + 1 - y < FLOOR_HEIGHT + insetY) {
            return false;
        }

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
        float diff = Mth.clamp(Mth.degreesDifference(current, target), -step, step);
        return current + diff;
    }

    private void initVariance() {
        net.minecraft.util.RandomSource r = net.minecraft.util.RandomSource.create(this.worldPosition.asLong());
        this.dirSign = r.nextBoolean() ? 1.0F : -1.0F;
        this.speedMul = 0.7F + r.nextFloat() * 0.8F;
        this.swimRadius = 0.14F + r.nextFloat() * 0.10F;
        this.swimAngle = r.nextFloat() * Mth.TWO_PI;
        this.prevSwimAngle = this.swimAngle;
        this.rollCooldown = 120 + r.nextInt(200);
        this.puffCooldown = 100 + r.nextInt(160);
        this.wanderRandom = net.minecraft.util.RandomSource.create(this.worldPosition.asLong() * 31L + 17L);
        this.wanderYaw = r.nextFloat() * 360.0F;
        this.varianceInit = true;
    }

    @Nullable
    private Entity createFish(Level world, ItemStack stack) {
        try {
            return buildFish(world, stack);
        } catch (RuntimeException | LinkageError e) {
            VillagersPlus.LOGGER.error("Cannot create the aquarium fish for {}.", BuiltInRegistries.ITEM.getKey(stack.getItem()), e);
            return null;
        }
    }

    @Nullable
    private Entity buildFish(Level world, ItemStack stack) {
        if (!(stack.getItem() instanceof MobBucketItem bucketItem)) {
            return null;
        }

        EntityType<?> type = ((DuckBucketable) bucketItem).getEntityType();

        if (type == null) {
            return null;
        }

        Entity fish = type.create(world, EntitySpawnReason.LOAD);

        if (fish == null) {
            return null;
        }

        CustomData bucketData = stack.get(DataComponents.BUCKET_ENTITY_DATA);

        if (bucketData != null) {
            CompoundTag entityNbt = bucketData.copyTag();
            try (ProblemReporter.ScopedCollector reporter =
                         new ProblemReporter.ScopedCollector(fish.problemPath(), VillagersPlus.LOGGER)) {
                fish.load(TagValueInput.create(reporter, world.registryAccess(), entityNbt));
            }
        }

        fish.applyComponentsFromItemStack(stack);
        ((EntityAccessorMixin) fish).setWasTouchingWater(true);
        return fish;
    }

    public void requestTankRescan() {
        this.tankRescanTimer = 0;
    }

    @Nullable
    public Entity getDisplayFish() {
        return this.displayFish;
    }

    public boolean isMultiTank() {
        return this.multiTank;
    }

    public float getFishX(float tickDelta) {
        return (float) Mth.lerp((double) tickDelta, this.prevFishX, this.fishX);
    }

    public float getFishY(float tickDelta) {
        return (float) Mth.lerp((double) tickDelta, this.prevFishY, this.fishY);
    }

    public float getFishZ(float tickDelta) {
        return (float) Mth.lerp((double) tickDelta, this.prevFishZ, this.fishZ);
    }

    public float getFishPitch(float tickDelta) {
        return Mth.rotLerp(tickDelta, this.prevWanderPitch, this.wanderPitch);
    }

    public float getRoll(float tickDelta) {
        return Mth.lerp(tickDelta, this.prevRoll, this.roll);
    }

    public float getAnimAge(float tickDelta) {
        return this.animAge + tickDelta;
    }

    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < this.inventory.size() ? (ItemStack)this.inventory.get(slot) : ItemStack.EMPTY;
    }

    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.inventory, slot, amount);
    }

    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.inventory, slot);
    }

    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.inventory.size()) {
            this.inventory.set(slot, stack);
        }
    }

    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    public void clearContent() {
        this.inventory.clear();
    }

    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        // Only right-click may fill the display slots, not hoppers.
        return false;
    }

    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.canPlaceItem(slot, stack);
    }

    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return false;
    }

}
