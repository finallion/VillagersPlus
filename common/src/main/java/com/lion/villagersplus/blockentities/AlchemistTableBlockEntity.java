package com.lion.villagersplus.blockentities;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.blocks.AlchemistTableBlock;
import com.lion.villagersplus.client.screen.AlchemistTableScreenHandler;
import com.lion.villagersplus.init.VPBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class AlchemistTableBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    private static final int[] TOP_SLOTS = new int[]{3};
    private static final int[] BOTTOM_SLOTS = new int[]{0, 1, 2, 3};
    private static final int[] SIDE_SLOTS = new int[]{0, 1, 2, 4};
    private NonNullList<ItemStack> inventory;
    private boolean[] slotsEmptyLastTick;
    private Item itemBrewing;
    int brewTime;
    int fuel;
    protected final ContainerData propertyDelegate;

    public AlchemistTableBlockEntity(BlockPos pos, BlockState state) {
        super(VPBlockEntities.ALCHEMIST_TABLE_BLOCK_ENTITY.get(), pos, state);
        this.inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        this.propertyDelegate = new ContainerData() {
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

            public int getCount() {
                return 2;
            }
        };
    }

    protected Component getDefaultName() {
        return Component.translatable("container.alchemist_table");
    }

    public int getContainerSize() {
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

    public static void tick(Level world, BlockPos pos, BlockState state, AlchemistTableBlockEntity blockEntity) {
        ItemStack itemStack = blockEntity.inventory.get(4);
        if (blockEntity.fuel <= 0 && itemStack.is(Items.GUNPOWDER)) {
            blockEntity.fuel = 20;
            itemStack.shrink(1);
            setChanged(world, pos, state);
        }

        if (blockEntity.fuel <= 0) {
            state = state.setValue(AlchemistTableBlock.HAS_FUEL, false);
        } else {
            state = state.setValue(AlchemistTableBlock.HAS_FUEL, true);
        }

        boolean bl = canCraft(blockEntity.inventory);
        boolean bl2 = blockEntity.brewTime > 0;
        ItemStack itemStack2 = blockEntity.inventory.get(3);

        if (!itemStack2.isEmpty()) {
            state = state.setValue(AlchemistTableBlock.BOTTLE_PROPERTIES[3], true);
        } else {
            state = state.setValue(AlchemistTableBlock.BOTTLE_PROPERTIES[3], false);
        }

        if (bl2) {
            --blockEntity.brewTime;
            boolean bl3 = blockEntity.brewTime == 0;
            state = (BlockState) state.setValue(AlchemistTableBlock.IS_BREWING, true);
            if (world.getRandom().nextInt(8) == 0) {
                world.playSound(null, pos, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 0.5F, 0.5F);
            }

            if (bl3 && bl) {
                craft(world, pos, blockEntity.inventory);
                setChanged(world, pos, state);
            } else if (!bl || !itemStack2.is(blockEntity.itemBrewing)) {
                blockEntity.brewTime = 0;
                setChanged(world, pos, state);
            }
        } else if (bl && blockEntity.fuel > 0) {
            blockEntity.fuel -= 20;
            blockEntity.brewTime = 400;
            blockEntity.itemBrewing = itemStack2.getItem();
            setChanged(world, pos, state);
        }

        if (blockEntity.brewTime <= 0) {
            state = (BlockState) state.setValue(AlchemistTableBlock.IS_BREWING, false);
        }

        world.setBlock(pos, state, 2);

        boolean[] bls = blockEntity.getSlotsEmpty();
        if (!Arrays.equals(bls, blockEntity.slotsEmptyLastTick)) {
            blockEntity.slotsEmptyLastTick = bls;
            BlockState blockState = state;
            if (!(state.getBlock() instanceof AlchemistTableBlock)) {
                return;
            }

            for (int i = 0; i < AlchemistTableBlock.BOTTLE_PROPERTIES.length - 1; ++i) {
                blockState = (BlockState) blockState.setValue(AlchemistTableBlock.BOTTLE_PROPERTIES[i], bls[i]);
            }

            world.setBlock(pos, blockState, 2);
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

    private static boolean canCraft(NonNullList<ItemStack> slots) {
        ItemStack itemStack = (ItemStack) slots.get(3);
        if (itemStack.isEmpty()) {
            return false;
        } else if (!(itemStack.is(Items.LINGERING_POTION) || itemStack.is(Items.SPLASH_POTION) || itemStack.is(Items.POTION))) {
            return false;
        } else {
            for (int i = 0; i < 3; ++i) {
                ItemStack itemStack2 = (ItemStack) slots.get(i);
                if (!itemStack2.isEmpty() ) {
                    return true;
                }
            }

            return false;
        }
    }

    private static void craft(Level world, BlockPos pos, NonNullList<ItemStack> slots) {
        ItemStack itemStack = slots.get(3);
        List<Holder<Potion>> potions = BuiltInRegistries.POTION.listElements().collect(Collectors.toList());

        int explosionChance = 0;
        if (VillagersPlus.CONFIG.can_explode) {
            explosionChance = world.getRandom().nextInt(VillagersPlus.CONFIG.explosion_chance);
        }

        if (explosionChance == 0) {
            for (int i = 0; i < 3; ++i) {
                if (!slots.get(i).isEmpty()) {
                    ItemStack brewed = new ItemStack(world.getRandom().nextBoolean() ? Items.SPLASH_POTION : Items.POTION);
                    brewed.set(DataComponents.POTION_CONTENTS,
                            new PotionContents(potions.get(world.getRandom().nextInt(potions.size()))));
                    slots.set(i, brewed);
                }
            }
        } else {
            world.explode(null, pos.getX(), pos.getY(), pos.getZ(), 3.0F, Level.ExplosionInteraction.NONE);
            slots.set(0, ItemStack.EMPTY);
            slots.set(1, ItemStack.EMPTY);
            slots.set(2, ItemStack.EMPTY);
            slots.set(4, ItemStack.EMPTY);
        }

        itemStack.shrink(1);

        slots.set(3, itemStack);
        world.levelEvent(1035, pos, 0);
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
        this.brewTime = view.getShortOr("BrewTime", (short) 0);
        if (this.brewTime > 0) {
            this.itemBrewing = this.inventory.get(3).getItem();
        }
        this.fuel = view.getByteOr("Fuel", (byte) 0);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        view.putShort("BrewTime", (short) this.brewTime);
        ContainerHelper.saveAllItems(view, this.inventory);
        view.putByte("Fuel", (byte) this.fuel);
    }

    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < this.inventory.size() ? (ItemStack) this.inventory.get(slot) : ItemStack.EMPTY;
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
            return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 3) {
            return stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION) || stack.is(Items.GLASS_BOTTLE);
        } else if (slot == 4) {
            return stack.is(Items.GUNPOWDER);
        } else {
            return stack.is(Items.GLASS_BOTTLE) && this.getItem(slot).isEmpty();
        }
    }

    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.UP) {
            return TOP_SLOTS;
        } else {
            return side == Direction.DOWN ? BOTTOM_SLOTS : SIDE_SLOTS;
        }
    }

    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.canPlaceItem(slot, stack);
    }

    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot != 3 || stack.is(Items.GLASS_BOTTLE);
    }

    public void clearContent() {
        this.inventory.clear();
    }

    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new AlchemistTableScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}
