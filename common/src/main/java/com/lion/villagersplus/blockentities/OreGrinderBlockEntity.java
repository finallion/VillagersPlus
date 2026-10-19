package com.lion.villagersplus.blockentities;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.blocks.OreGrinderBlock;
import com.lion.villagersplus.client.screen.OreGrinderScreenHandler;
import com.lion.villagersplus.init.VPBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class OreGrinderBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int INPUT_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    public static final int PICKAXE_SLOT = 3;
    public static final int INVENTORY_SIZE = 4;

    private static final int[] TOP_SLOTS = new int[]{INPUT_SLOT};
    private static final int[] BOTTOM_SLOTS = new int[]{OUTPUT_SLOT, FUEL_SLOT};
    private static final int[] SIDE_SLOTS = new int[]{FUEL_SLOT};

    private static final int GRIND_TIME_NO_PICKAXE = 800;

    private static Map<Item, ItemStack> GRINDING_RECIPES;

    private NonNullList<ItemStack> inventory;
    private int burnTime;
    private int fuelTime;
    private int grindProgress;
    private int grindTimeTotal;

    protected final ContainerData propertyDelegate;

    public OreGrinderBlockEntity(BlockPos pos, BlockState state) {
        super(VPBlockEntities.ORE_GRINDER_BLOCK_ENTITY.get(), pos, state);
        this.inventory = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
        this.grindTimeTotal = GRIND_TIME_NO_PICKAXE;
        this.propertyDelegate = new ContainerData() {
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

            public int getCount() {
                return 4;
            }
        };
    }

    private static Map<Item, ItemStack> getGrindingRecipes() {
        if (GRINDING_RECIPES == null) {
            Map<Item, ItemStack> recipes = new HashMap<>();
            addRecipe(recipes, Items.IRON_ORE, Items.RAW_IRON, 2);
            addRecipe(recipes, Items.DEEPSLATE_IRON_ORE, Items.RAW_IRON, 2);
            addRecipe(recipes, Items.GOLD_ORE, Items.RAW_GOLD, 2);
            addRecipe(recipes, Items.DEEPSLATE_GOLD_ORE, Items.RAW_GOLD, 2);
            addRecipe(recipes, Items.NETHER_GOLD_ORE, Items.RAW_GOLD, 2);
            addRecipe(recipes, Items.COPPER_ORE, Items.RAW_COPPER, 3);
            addRecipe(recipes, Items.DEEPSLATE_COPPER_ORE, Items.RAW_COPPER, 3);
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
        return stack.is(ItemTags.PICKAXES);
    }

    public static boolean isGrindable(ItemStack stack) {
        return getGrindResult(stack) != null;
    }

    private static int getGrindTime(@Nullable Level world, ItemStack pickaxe) {
        if (!isPickaxe(pickaxe)) {
            return GRIND_TIME_NO_PICKAXE;
        }

        Tool tool = pickaxe.get(DataComponents.TOOL);

        if (tool == null) {
            return GRIND_TIME_NO_PICKAXE;
        }

        float speed = tool.defaultMiningSpeed();
        int efficiency = enchantmentLevel(world, Enchantments.EFFICIENCY, pickaxe);

        if (efficiency > 0) {
            speed += (float) (efficiency * efficiency + 1);
        }

        return Mth.clamp((int) (1200.0F / speed), 40, 600);
    }

    /// Returns 0 without a world, because enchantments need the registry access.
    private static int enchantmentLevel(@Nullable Level world, ResourceKey<Enchantment> enchantment, ItemStack stack) {
        if (world == null || stack.isEmpty()) {
            return 0;
        }

        return world.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .get(enchantment)
                .map(entry -> EnchantmentHelper.getItemEnchantmentLevel(entry, stack))
                .orElse(0);
    }

    protected Component getDefaultName() {
        return Component.translatable("container.villagersplus.ore_grinder");
    }

    public int getContainerSize() {
        return this.inventory.size();
    }

    public boolean isEmpty() {
        java.util.Iterator<ItemStack> iterator = this.inventory.iterator();

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

    public static void tick(Level world, BlockPos pos, BlockState state, OreGrinderBlockEntity blockEntity) {
        boolean dirty = false;
        boolean disabled = world.hasNeighborSignal(pos);

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
                blockEntity.burnTime = getFuelTime(world, fuelStack);
                blockEntity.fuelTime = blockEntity.burnTime;

                if (blockEntity.isBurning()) {
                    dirty = true;
                    Item fuelItem = fuelStack.getItem();
                    fuelStack.shrink(1);

                    if (fuelStack.isEmpty()) {
                        ItemStackTemplate remainder = fuelItem.getCraftingRemainder();
                        blockEntity.inventory.set(FUEL_SLOT, remainder == null ? ItemStack.EMPTY : remainder.create());
                    }
                }
            }

            if (blockEntity.isBurning() && canAccept) {
                ++blockEntity.grindProgress;

                if (blockEntity.grindProgress >= blockEntity.grindTimeTotal) {
                    blockEntity.grindProgress = 0;
                    blockEntity.grind(result);
                    world.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 0.6F, 1.0F);
                    dirty = true;
                }
            } else {
                blockEntity.grindProgress = 0;
            }
        } else if (blockEntity.grindProgress > 0) {
            blockEntity.grindProgress = Mth.clamp(blockEntity.grindProgress - 2, 0, blockEntity.grindTimeTotal);
        }

        boolean lit = blockEntity.isBurning() && !disabled;

        if (state.getValue(OreGrinderBlock.LIT) != lit) {
            dirty = true;
            state = state.setValue(OreGrinderBlock.LIT, lit);
            world.setBlock(pos, state, 3);
        }

        if (dirty) {
            setChanged(world, pos, state);
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

        if (!outputStack.is(result.getItem())) {
            return false;
        }

        int worstCaseYield = scaledYield(result) * (fortuneLevel() + 1);
        int combined = outputStack.getCount() + worstCaseYield;
        return combined <= this.getMaxStackSize() && combined <= outputStack.getMaxStackSize();
    }

    private int fortuneLevel() {
        ItemStack pickaxeStack = this.inventory.get(PICKAXE_SLOT);

        return VillagersPlus.CONFIG.ore_grinder_fortune_enabled
                ? enchantmentLevel(this.level, Enchantments.FORTUNE, pickaxeStack) : 0;
    }

    private static int scaledYield(ItemStack baseResult) {
        float multiplier = Math.max(0.0F, VillagersPlus.CONFIG.ore_grinder_output_multiplier);

        return Math.max(1, Math.round(baseResult.getCount() * multiplier));
    }

    private void grind(@Nullable ItemStack baseResult) {
        if (baseResult == null || !this.canAcceptOutput(baseResult)) {
            return;
        }

        ItemStack inputStack = this.inventory.get(INPUT_SLOT);
        ItemStack pickaxeStack = this.inventory.get(PICKAXE_SLOT);
        ItemStack outputStack = this.inventory.get(OUTPUT_SLOT);

        int fortune = fortuneLevel();
        int count = scaledYield(baseResult);

        if (fortune > 0) {
            int bonus = this.level.getRandom().nextInt(fortune + 2) - 1;

            if (bonus < 0) {
                bonus = 0;
            }

            count *= (bonus + 1);
        }

        int max = Math.min(baseResult.getMaxStackSize(), this.getMaxStackSize());

        if (outputStack.isEmpty()) {
            ItemStack newStack = baseResult.copy();
            newStack.setCount(Math.min(count, max));
            this.inventory.set(OUTPUT_SLOT, newStack);
        } else if (outputStack.is(baseResult.getItem())) {
            int space = max - outputStack.getCount();
            outputStack.grow(Math.min(count, space));
        }

        inputStack.shrink(1);
        this.damagePickaxe(pickaxeStack);
    }

    private void damagePickaxe(ItemStack pickaxe) {
        if (!isPickaxe(pickaxe) || !pickaxe.isDamageableItem()) {
            return;
        }

        int unbreaking = enchantmentLevel(this.level, Enchantments.UNBREAKING, pickaxe);
        int damage = Math.max(0, VillagersPlus.CONFIG.ore_grinder_pickaxe_damage);
        for (int i = 0; i < damage; i++) {
            if (unbreaking > 0 && this.level.getRandom().nextInt(unbreaking + 1) != 0) {
                continue;
            }

            pickaxe.setDamageValue(pickaxe.getDamageValue() + 1);

            if (pickaxe.getDamageValue() >= pickaxe.getMaxDamage()) {
                this.inventory.set(PICKAXE_SLOT, ItemStack.EMPTY);
                this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_BREAK.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
                return;
            }
        }
    }

    // Fuel burn times come from the level registry.
    private static int getFuelTime(@Nullable Level world, ItemStack fuel) {
        if (world == null || fuel.isEmpty()) {
            return 0;
        }

        return world.fuelValues().burnDuration(fuel);
    }

    public static boolean canUseAsFuel(@Nullable Level world, ItemStack stack) {
        return world != null && world.fuelValues().isFuel(stack);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(view, this.inventory);
        this.burnTime = view.getShortOr("BurnTime", (short) 0);
        this.grindProgress = view.getShortOr("GrindTime", (short) 0);
        this.grindTimeTotal = getGrindTime(this.level, this.inventory.get(PICKAXE_SLOT));
        int storedFuelTime = view.getShortOr("FuelTime", (short) 0);
        this.fuelTime = storedFuelTime > 0 ? storedFuelTime : getFuelTime(this.level, this.inventory.get(FUEL_SLOT));
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        view.putShort("BurnTime", (short) this.burnTime);
        view.putShort("GrindTime", (short) this.grindProgress);
        view.putShort("FuelTime", (short) this.fuelTime);
        ContainerHelper.saveAllItems(view, this.inventory);
    }

    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < this.inventory.size() ? this.inventory.get(slot) : ItemStack.EMPTY;
    }

    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.inventory, slot, amount);
    }

    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.inventory, slot);
    }

    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.inventory.size()) {
            ItemStack existing = this.inventory.get(slot);
            boolean sameItem = !stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, existing);
            this.inventory.set(slot, stack);

            if (stack.getCount() > this.getMaxStackSize()) {
                stack.setCount(this.getMaxStackSize());
            }

            if (slot == INPUT_SLOT && !sameItem) {
                this.grindProgress = 0;
                this.setChanged();
            }
        }
    }

    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == OUTPUT_SLOT) {
            return false;
        } else if (slot == FUEL_SLOT) {
            return canUseAsFuel(this.level, stack);
        } else if (slot == PICKAXE_SLOT) {
            return isPickaxe(stack);
        } else {
            return isGrindable(stack);
        }
    }

    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        } else {
            return side == Direction.UP ? TOP_SLOTS : SIDE_SLOTS;
        }
    }

    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.canPlaceItem(slot, stack);
    }

    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == OUTPUT_SLOT || slot == FUEL_SLOT && !canUseAsFuel(this.level, stack);
    }

    public void clearContent() {
        this.inventory.clear();
    }

    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new OreGrinderScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}
