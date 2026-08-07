package com.lion.villagersplus.blockentities;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.blocks.AlchemistTableBlock;
import com.lion.villagersplus.client.screen.AlchemistTableScreenHandler;
import com.lion.villagersplus.init.VPBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class AlchemistTableBlockEntity extends LockableContainerBlockEntity implements SidedInventory {
    private static final int[] TOP_SLOTS = new int[]{3};
    private static final int[] BOTTOM_SLOTS = new int[]{0, 1, 2, 3};
    private static final int[] SIDE_SLOTS = new int[]{0, 1, 2, 4};
    private DefaultedList<ItemStack> inventory;
    private boolean[] slotsEmptyLastTick;
    private Item itemBrewing;
    int brewTime;
    int fuel;
    protected final PropertyDelegate propertyDelegate;

    public AlchemistTableBlockEntity(BlockPos pos, BlockState state) {
        super(VPBlockEntities.ALCHEMIST_TABLE_BLOCK_ENTITY.get(), pos, state);
        this.inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                return switch (index) {
                    case 0 -> AlchemistTableBlockEntity.this.brewTime;
                    case 1 -> AlchemistTableBlockEntity.this.fuel;
                    default -> 0;
                };
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        AlchemistTableBlockEntity.this.brewTime = value;
                        break;
                    case 1:
                        AlchemistTableBlockEntity.this.fuel = value;
                }

            }

            public int size() {
                return 2;
            }
        };
    }

    protected Text getContainerName() {
        return Text.translatable("container.alchemist_table");
    }

    public int size() {
        return this.inventory.size();
    }

    public boolean isEmpty() {
        java.util.Iterator<ItemStack> var1 = this.inventory.iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = var1.next();
        } while (itemStack.isEmpty());

        return false;
    }


    public static void tick(World world, BlockPos pos, BlockState state, AlchemistTableBlockEntity blockEntity) {
        ItemStack itemStack = blockEntity.inventory.get(4);
        if (blockEntity.fuel <= 0 && itemStack.isOf(Items.GUNPOWDER)) {
            blockEntity.fuel = 20;
            itemStack.decrement(1);
            markDirty(world, pos, state);
        }

        if (blockEntity.fuel <= 0) {
            state = state.with(AlchemistTableBlock.HAS_FUEL, false);
        } else {
            state = state.with(AlchemistTableBlock.HAS_FUEL, true);
        }

        boolean bl = canCraft(blockEntity.inventory);
        boolean bl2 = blockEntity.brewTime > 0;
        ItemStack itemStack2 = blockEntity.inventory.get(3);

        if (!itemStack2.isEmpty()) {
            state = state.with(AlchemistTableBlock.BOTTLE_PROPERTIES[3], true);
        } else {
            state = state.with(AlchemistTableBlock.BOTTLE_PROPERTIES[3], false);
        }


        if (bl2) {
            --blockEntity.brewTime;
            boolean bl3 = blockEntity.brewTime == 0;
            state = (BlockState) state.with(AlchemistTableBlock.IS_BREWING, true);
            if (world.random.nextInt(8) == 0) {
                world.playSound(null, pos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5F, 0.5F);
            }

            if (bl3 && bl) {
                craft(world, pos, blockEntity.inventory);
                markDirty(world, pos, state);
            } else if (!bl || !itemStack2.isOf(blockEntity.itemBrewing)) {
                blockEntity.brewTime = 0;
                markDirty(world, pos, state);
            }
        } else if (bl && blockEntity.fuel > 0) {
            blockEntity.fuel -= 20;
            blockEntity.brewTime = 400;
            blockEntity.itemBrewing = itemStack2.getItem();
            markDirty(world, pos, state);
        }

        if (blockEntity.brewTime <= 0) {
            state = (BlockState) state.with(AlchemistTableBlock.IS_BREWING, false);
        }

        world.setBlockState(pos, state, 2);

        boolean[] bls = blockEntity.getSlotsEmpty();
        if (!Arrays.equals(bls, blockEntity.slotsEmptyLastTick)) {
            blockEntity.slotsEmptyLastTick = bls;
            BlockState blockState = state;
            if (!(state.getBlock() instanceof AlchemistTableBlock)) {
                return;
            }

            for (int i = 0; i < AlchemistTableBlock.BOTTLE_PROPERTIES.length - 1; ++i) {
                blockState = (BlockState) blockState.with(AlchemistTableBlock.BOTTLE_PROPERTIES[i], bls[i]);
            }

            world.setBlockState(pos, blockState, 2);
        }

    }

    private boolean[] getSlotsEmpty() {
        boolean[] bls = new boolean[3];

        for (int i = 0; i < 3; ++i) {
            if (!((ItemStack) this.inventory.get(i)).isEmpty()) {
                bls[i] = true;
            }
        }

        return bls;
    }

    private static boolean canCraft(DefaultedList<ItemStack> slots) {
        ItemStack itemStack = (ItemStack) slots.get(3);
        if (itemStack.isEmpty()) {
            return false;
        } else if (!(itemStack.isOf(Items.LINGERING_POTION) || itemStack.isOf(Items.SPLASH_POTION) || itemStack.isOf(Items.POTION))) {
            return false;
        } else {
            for (int i = 0; i < 3; ++i) {
                ItemStack itemStack2 = (ItemStack) slots.get(i);
                if (!itemStack2.isEmpty() /*&& BrewingRecipeRegistry.hasRecipe(itemStack2, itemStack)*/) {
                    return true;
                }
            }

            return false;
        }
    }

    private static void craft(World world, BlockPos pos, DefaultedList<ItemStack> slots) {
        // ingredient slot
        ItemStack itemStack = slots.get(3);
        // A potion is a registry entry now, not a bare value: PotionContentsComponent holds the entry.
        List<RegistryEntry<Potion>> potions = Registries.POTION.streamEntries().collect(Collectors.toList());

        int explosionChance = 0;
        if (VillagersPlus.CONFIG.can_explode) {
            explosionChance = world.getRandom().nextInt(VillagersPlus.CONFIG.explosion_chance);
        }

        if (explosionChance == 0) {
            for (int i = 0; i < 3; ++i) {
                if (!slots.get(i).isEmpty()) {
                    ItemStack brewed = new ItemStack(world.random.nextBoolean() ? Items.SPLASH_POTION : Items.POTION);
                    brewed.set(DataComponentTypes.POTION_CONTENTS,
                            new PotionContentsComponent(potions.get(world.random.nextInt(potions.size()))));
                    slots.set(i, brewed);
                }
            }
        } else {
            world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 3.0F, World.ExplosionSourceType.NONE);
            slots.set(0, ItemStack.EMPTY);
            slots.set(1, ItemStack.EMPTY);
            slots.set(2, ItemStack.EMPTY);
            slots.set(4, ItemStack.EMPTY);
        }

        itemStack.decrement(1);

        slots.set(3, itemStack);
        world.syncWorldEvent(1035, pos, 0);
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

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readData(view, this.inventory);
        this.brewTime = view.getShort("BrewTime", (short) 0);
        if (this.brewTime > 0) {
            this.itemBrewing = this.inventory.get(3).getItem();
        }
        this.fuel = view.getByte("Fuel", (byte) 0);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putShort("BrewTime", (short) this.brewTime);
        Inventories.writeData(view, this.inventory);
        view.putByte("Fuel", (byte) this.fuel);
    }

    public ItemStack getStack(int slot) {
        return slot >= 0 && slot < this.inventory.size() ? (ItemStack) this.inventory.get(slot) : ItemStack.EMPTY;
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
            return !(player.squaredDistanceTo((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) > 64.0D);
        }
    }

    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 3) {
            return stack.isOf(Items.POTION) || stack.isOf(Items.SPLASH_POTION) || stack.isOf(Items.LINGERING_POTION) || stack.isOf(Items.GLASS_BOTTLE);
        } else if (slot == 4) { // fuel
            return stack.isOf(Items.GUNPOWDER);
        } else {
            return stack.isOf(Items.GLASS_BOTTLE) && this.getStack(slot).isEmpty();
        }
    }

    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP) {
            return TOP_SLOTS;
        } else {
            return side == Direction.DOWN ? BOTTOM_SLOTS : SIDE_SLOTS;
        }
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot != 3 || stack.isOf(Items.GLASS_BOTTLE);
    }

    public void clear() {
        this.inventory.clear();
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new AlchemistTableScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}
