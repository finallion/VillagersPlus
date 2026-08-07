package com.lion.villagersplus.blockentities;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.blocks.OreGrinderBlock;
import com.lion.villagersplus.client.screen.OreGrinderScreenHandler;
import com.lion.villagersplus.init.VPBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OreGrinderBlockEntity extends LockableContainerBlockEntity implements SidedInventory {
    public static final int INPUT_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    public static final int PICKAXE_SLOT = 3;
    public static final int INVENTORY_SIZE = 4;

    private static final int[] TOP_SLOTS = new int[]{INPUT_SLOT};
    private static final int[] BOTTOM_SLOTS = new int[]{OUTPUT_SLOT, FUEL_SLOT};
    private static final int[] SIDE_SLOTS = new int[]{FUEL_SLOT};

    /** Grind time (ticks) when no pickaxe is inserted. A pickaxe is always faster. */
    private static final int GRIND_TIME_NO_PICKAXE = 800;

    /** Ore-doubling recipes: input item -> ground result. Populated lazily to avoid classload-order issues. */
    private static Map<Item, ItemStack> GRINDING_RECIPES;

    private DefaultedList<ItemStack> inventory;
    private int burnTime;
    private int fuelTime;
    private int grindProgress;
    private int grindTimeTotal;

    protected final PropertyDelegate propertyDelegate;

    public OreGrinderBlockEntity(BlockPos pos, BlockState state) {
        super(VPBlockEntities.ORE_GRINDER_BLOCK_ENTITY.get(), pos, state);
        this.inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
        this.grindTimeTotal = GRIND_TIME_NO_PICKAXE;
        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                return switch (index) {
                    case 0 -> OreGrinderBlockEntity.this.burnTime;
                    case 1 -> OreGrinderBlockEntity.this.fuelTime;
                    case 2 -> OreGrinderBlockEntity.this.grindProgress;
                    case 3 -> OreGrinderBlockEntity.this.grindTimeTotal;
                    default -> 0;
                };
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0 -> OreGrinderBlockEntity.this.burnTime = value;
                    case 1 -> OreGrinderBlockEntity.this.fuelTime = value;
                    case 2 -> OreGrinderBlockEntity.this.grindProgress = value;
                    case 3 -> OreGrinderBlockEntity.this.grindTimeTotal = value;
                }
            }

            public int size() {
                return 4;
            }
        };
    }

    private static Map<Item, ItemStack> getGrindingRecipes() {
        if (GRINDING_RECIPES == null) {
            Map<Item, ItemStack> recipes = new HashMap<>();
            // Raw-metal ores double their raw drop.
            addRecipe(recipes, Items.IRON_ORE, Items.RAW_IRON, 2);
            addRecipe(recipes, Items.DEEPSLATE_IRON_ORE, Items.RAW_IRON, 2);
            addRecipe(recipes, Items.GOLD_ORE, Items.RAW_GOLD, 2);
            addRecipe(recipes, Items.DEEPSLATE_GOLD_ORE, Items.RAW_GOLD, 2);
            addRecipe(recipes, Items.NETHER_GOLD_ORE, Items.RAW_GOLD, 2);
            addRecipe(recipes, Items.COPPER_ORE, Items.RAW_COPPER, 3);
            addRecipe(recipes, Items.DEEPSLATE_COPPER_ORE, Items.RAW_COPPER, 3);
            // Gem/mineral ores double their drop.
            addRecipe(recipes, Items.COAL_ORE, Items.COAL, 2);
            addRecipe(recipes, Items.DEEPSLATE_COAL_ORE, Items.COAL, 2);
            addRecipe(recipes, Items.DIAMOND_ORE, Items.DIAMOND, 2);
            addRecipe(recipes, Items.DEEPSLATE_DIAMOND_ORE, Items.DIAMOND, 2);
            addRecipe(recipes, Items.EMERALD_ORE, Items.EMERALD, 2);
            addRecipe(recipes, Items.DEEPSLATE_EMERALD_ORE, Items.EMERALD, 2);
            addRecipe(recipes, Items.LAPIS_ORE, Items.LAPIS_LAZULI, 8);
            addRecipe(recipes, Items.DEEPSLATE_LAPIS_ORE, Items.LAPIS_LAZULI, 8);
            addRecipe(recipes, Items.REDSTONE_ORE, Items.REDSTONE, 8);
            addRecipe(recipes, Items.DEEPSLATE_REDSTONE_ORE, Items.REDSTONE, 8);
            addRecipe(recipes, Items.NETHER_QUARTZ_ORE, Items.QUARTZ, 2);
            GRINDING_RECIPES = recipes;
        }
        return GRINDING_RECIPES;
    }

    private static void addRecipe(Map<Item, ItemStack> recipes, Item input, Item output, int count) {
        recipes.put(input, new ItemStack(output, count));
    }

    @Nullable
    private static ItemStack getGrindResult(ItemStack input) {
        if (input.isEmpty()) {
            return null;
        }
        ItemStack result = getGrindingRecipes().get(input.getItem());
        return result == null ? null : result.copy();
    }

    public static boolean isPickaxe(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem;
    }

    /** True only for ores that have a grinding recipe. Used to restrict the input slot. */
    public static boolean isGrindable(ItemStack stack) {
        return getGrindResult(stack) != null;
    }

    /**
     * Pickaxe material and Efficiency together determine grinding speed (fewer ticks = faster).
     * No pickaxe is the slowest. Efficiency adds to the effective speed using the vanilla
     * mining bonus (level^2 + 1), so a higher Efficiency level noticeably speeds things up.
     */
    private static int getGrindTime(@Nullable World world, ItemStack pickaxe) {
        if (!isPickaxe(pickaxe)) {
            return GRIND_TIME_NO_PICKAXE;
        }
        float speed = ((ToolItem) pickaxe.getItem()).getMaterial().getMiningSpeedMultiplier();
        int efficiency = enchantmentLevel(world, Enchantments.EFFICIENCY, pickaxe);
        if (efficiency > 0) {
            speed += (float) (efficiency * efficiency + 1);
        }
        // Base material: wood ~2 -> 600, stone ~4 -> 300, iron ~6 -> 200, diamond ~8 -> 150, gold ~12 -> 100.
        // Efficiency V on diamond ~34 -> clamps to the 40-tick floor.
        return MathHelper.clamp((int) (1200.0F / speed), 40, 600);
    }

    /**
     * Enchantments moved into a dynamic registry in 1.21, so {@link Enchantments} only holds keys and
     * a level lookup has to go through the world's registries. A missing world (during
     * {@code readNbt}, before the block entity is placed) simply counts as no enchantment; the tick
     * recomputes the grind time every tick anyway, so it corrects itself immediately.
     */
    private static int enchantmentLevel(@Nullable World world, RegistryKey<Enchantment> enchantment, ItemStack stack) {
        if (world == null || stack.isEmpty()) {
            return 0;
        }
        return world.getRegistryManager()
                .get(RegistryKeys.ENCHANTMENT)
                .getEntry(enchantment)
                .map(entry -> EnchantmentHelper.getLevel(entry, stack))
                .orElse(0);
    }

    protected Text getContainerName() {
        return Text.translatable("container.villagersplus.ore_grinder");
    }

    public int size() {
        return this.inventory.size();
    }

    public boolean isEmpty() {
        Iterator<ItemStack> iterator = this.inventory.iterator();

        ItemStack itemStack;
        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemStack = iterator.next();
        } while (itemStack.isEmpty());

        return false;
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    public static void tick(World world, BlockPos pos, BlockState state, OreGrinderBlockEntity blockEntity) {
        boolean dirty = false;
        boolean disabled = world.isReceivingRedstonePower(pos);

        if (blockEntity.isBurning() && !disabled) {
            --blockEntity.burnTime;
        }

        ItemStack fuelStack = blockEntity.inventory.get(FUEL_SLOT);
        ItemStack inputStack = blockEntity.inventory.get(INPUT_SLOT);
        ItemStack pickaxeStack = blockEntity.inventory.get(PICKAXE_SLOT);
        boolean hasInput = !inputStack.isEmpty();
        boolean hasFuel = !fuelStack.isEmpty();

        blockEntity.grindTimeTotal = getGrindTime(world, pickaxeStack);

        if (!disabled && (blockEntity.isBurning() || hasFuel && hasInput)) {
            ItemStack result = getGrindResult(inputStack);
            boolean canAccept = blockEntity.canAcceptOutput(result);

            if (!blockEntity.isBurning() && canAccept && hasFuel) {
                blockEntity.burnTime = getFuelTime(fuelStack);
                blockEntity.fuelTime = blockEntity.burnTime;
                if (blockEntity.isBurning()) {
                    dirty = true;
                    Item fuelItem = fuelStack.getItem();
                    fuelStack.decrement(1);
                    if (fuelStack.isEmpty()) {
                        Item remainder = fuelItem.getRecipeRemainder();
                        blockEntity.inventory.set(FUEL_SLOT, remainder == null ? ItemStack.EMPTY : new ItemStack(remainder));
                    }
                }
            }

            if (blockEntity.isBurning() && canAccept) {
                ++blockEntity.grindProgress;
                if (blockEntity.grindProgress >= blockEntity.grindTimeTotal) {
                    blockEntity.grindProgress = 0;
                    blockEntity.grind(result);
                    world.playSound(null, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 0.6F, 1.0F);
                    dirty = true;
                }
            } else {
                blockEntity.grindProgress = 0;
            }
        } else if (blockEntity.grindProgress > 0) {
            blockEntity.grindProgress = MathHelper.clamp(blockEntity.grindProgress - 2, 0, blockEntity.grindTimeTotal);
        }

        boolean lit = blockEntity.isBurning() && !disabled;
        if (state.get(OreGrinderBlock.LIT) != lit) {
            dirty = true;
            state = state.with(OreGrinderBlock.LIT, lit);
            world.setBlockState(pos, state, 3);
        }

        if (dirty) {
            markDirty(world, pos, state);
        }
    }

    private boolean canAcceptOutput(@Nullable ItemStack result) {
        if (result == null) {
            return false;
        }
        ItemStack outputStack = this.inventory.get(OUTPUT_SLOT);
        if (outputStack.isEmpty()) {
            return true;
        }
        if (!outputStack.isOf(result.getItem())) {
            return false;
        }
        int combined = outputStack.getCount() + result.getCount();
        return combined <= this.getMaxCountPerStack() && combined <= outputStack.getMaxCount();
    }

    private void grind(@Nullable ItemStack baseResult) {
        if (baseResult == null || !this.canAcceptOutput(baseResult)) {
            return;
        }
        ItemStack inputStack = this.inventory.get(INPUT_SLOT);
        ItemStack pickaxeStack = this.inventory.get(PICKAXE_SLOT);
        ItemStack outputStack = this.inventory.get(OUTPUT_SLOT);

        // Fortune boosts the yield (vanilla ore-drop formula) on top of the base doubling;
        // Efficiency (handled in getGrindTime) only affects grinding speed. Both the base
        // output and the Fortune effect are configurable for balancing.
        float multiplier = Math.max(0.0F, VillagersPlus.CONFIG.ore_grinder_output_multiplier);
        int count = Math.max(1, Math.round(baseResult.getCount() * multiplier));
        int fortune = VillagersPlus.CONFIG.ore_grinder_fortune_enabled
                ? enchantmentLevel(this.world, Enchantments.FORTUNE, pickaxeStack) : 0;
        if (fortune > 0) {
            int bonus = this.world.random.nextInt(fortune + 2) - 1;
            if (bonus < 0) {
                bonus = 0;
            }
            count *= (bonus + 1);
        }

        int max = Math.min(baseResult.getMaxCount(), this.getMaxCountPerStack());
        if (outputStack.isEmpty()) {
            ItemStack newStack = baseResult.copy();
            newStack.setCount(Math.min(count, max));
            this.inventory.set(OUTPUT_SLOT, newStack);
        } else if (outputStack.isOf(baseResult.getItem())) {
            int space = max - outputStack.getCount();
            outputStack.increment(Math.min(count, space));
        }

        inputStack.decrement(1);
        this.damagePickaxe(pickaxeStack);
    }

    private void damagePickaxe(ItemStack pickaxe) {
        if (!isPickaxe(pickaxe) || !pickaxe.isDamageable()) {
            return;
        }
        // Configurable wear per ground ore; honour Unbreaking per durability point with the
        // vanilla probability (like ItemStack#damage does).
        int unbreaking = enchantmentLevel(this.world, Enchantments.UNBREAKING, pickaxe);
        int damage = Math.max(0, VillagersPlus.CONFIG.ore_grinder_pickaxe_damage);
        for (int i = 0; i < damage; i++) {
            if (unbreaking > 0 && this.world.random.nextInt(unbreaking + 1) != 0) {
                continue;
            }
            pickaxe.setDamage(pickaxe.getDamage() + 1);
            if (pickaxe.getDamage() >= pickaxe.getMaxDamage()) {
                this.inventory.set(PICKAXE_SLOT, ItemStack.EMPTY);
                this.world.playSound(null, this.pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return;
            }
        }
    }

    private static int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        }
        Item item = fuel.getItem();
        return AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(item, 0);
    }

    public static boolean canUseAsFuel(ItemStack stack) {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().containsKey(stack.getItem());
    }

    /**
     * LockableContainerBlockEntity declares these abstract as of 1.20.5 so it can move the whole
     * inventory in and out of the {@code container} item component.
     */
    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory, registryLookup);
        this.burnTime = nbt.getShort("BurnTime");
        this.grindProgress = nbt.getShort("GrindTime");
        this.grindTimeTotal = getGrindTime(this.world, this.inventory.get(PICKAXE_SLOT));
        this.fuelTime = nbt.contains("FuelTime")
                ? nbt.getShort("FuelTime")
                : getFuelTime(this.inventory.get(FUEL_SLOT));
    }

    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putShort("BurnTime", (short) this.burnTime);
        nbt.putShort("GrindTime", (short) this.grindProgress);
        nbt.putShort("FuelTime", (short) this.fuelTime);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
    }

    public ItemStack getStack(int slot) {
        return slot >= 0 && slot < this.inventory.size() ? this.inventory.get(slot) : ItemStack.EMPTY;
    }

    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    public void setStack(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.inventory.size()) {
            ItemStack existing = this.inventory.get(slot);
            // canCombine is gone; the equivalent check is item plus components.
            boolean sameItem = !stack.isEmpty() && ItemStack.areItemsAndComponentsEqual(stack, existing);
            this.inventory.set(slot, stack);
            if (stack.getCount() > this.getMaxCountPerStack()) {
                stack.setCount(this.getMaxCountPerStack());
            }
            if (slot == INPUT_SLOT && !sameItem) {
                this.grindProgress = 0;
                this.markDirty();
            }
        }
    }

    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return !(player.squaredDistanceTo((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) > 64.0D);
        }
    }

    public boolean isValid(int slot, ItemStack stack) {
        if (slot == OUTPUT_SLOT) {
            return false;
        } else if (slot == FUEL_SLOT) {
            return canUseAsFuel(stack);
        } else if (slot == PICKAXE_SLOT) {
            return isPickaxe(stack);
        } else {
            return isGrindable(stack);
        }
    }

    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        } else {
            return side == Direction.UP ? TOP_SLOTS : SIDE_SLOTS;
        }
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == OUTPUT_SLOT || slot == FUEL_SLOT && !canUseAsFuel(stack);
    }

    public void clear() {
        this.inventory.clear();
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new OreGrinderScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}
