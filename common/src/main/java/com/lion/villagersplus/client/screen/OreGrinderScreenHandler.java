package com.lion.villagersplus.client.screen;

import com.lion.villagersplus.blockentities.OreGrinderBlockEntity;
import com.lion.villagersplus.init.VPScreens;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class OreGrinderScreenHandler extends AbstractContainerMenu {
    private static final int MACHINE_SLOTS = OreGrinderBlockEntity.INVENTORY_SIZE;
    private static final int INV_START = MACHINE_SLOTS;
    private static final int HOTBAR_START = INV_START + 27;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final Container inventory;
    private final ContainerData propertyDelegate;

    public OreGrinderScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(OreGrinderBlockEntity.INVENTORY_SIZE), new SimpleContainerData(4));
    }

    public OreGrinderScreenHandler(
            int syncId,
            Inventory playerInventory,
            Container inventory,
            ContainerData propertyDelegate
    ) {
        super(VPScreens.ORE_GRINDER_SCREEN_HANDLER, syncId);
        checkContainerSize(inventory, OreGrinderBlockEntity.INVENTORY_SIZE);
        checkContainerDataCount(propertyDelegate, 4);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;

        this.addSlot(new InputSlot(inventory, OreGrinderBlockEntity.INPUT_SLOT, 56, 17));
        this.addSlot(new FuelSlot(inventory, OreGrinderBlockEntity.FUEL_SLOT, 56, 53));
        this.addSlot(new OutputSlot(inventory, OreGrinderBlockEntity.OUTPUT_SLOT, 116, 35));
        this.addSlot(new PickaxeSlot(inventory, OreGrinderBlockEntity.PICKAXE_SLOT, 22, 36));
        this.addDataSlots(propertyDelegate);

        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();

            if (index == OreGrinderBlockEntity.OUTPUT_SLOT) {
                if (!this.moveItemStackTo(itemStack2, INV_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemStack2, itemStack);
            } else if (index >= INV_START) {
                if (OreGrinderBlockEntity.canUseAsFuel(itemStack2)) {
                    if (!this.moveItemStackTo(itemStack2, OreGrinderBlockEntity.FUEL_SLOT, OreGrinderBlockEntity.FUEL_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (OreGrinderBlockEntity.isPickaxe(itemStack2)) {
                    if (!this.moveItemStackTo(itemStack2, OreGrinderBlockEntity.PICKAXE_SLOT, OreGrinderBlockEntity.PICKAXE_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.inventory.canPlaceItem(OreGrinderBlockEntity.INPUT_SLOT, itemStack2)) {
                    if (!this.moveItemStackTo(itemStack2, OreGrinderBlockEntity.INPUT_SLOT, OreGrinderBlockEntity.INPUT_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= INV_START && index < HOTBAR_START) {
                    if (!this.moveItemStackTo(itemStack2, HOTBAR_START, HOTBAR_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= HOTBAR_START && index < HOTBAR_END && !this.moveItemStackTo(itemStack2, INV_START, HOTBAR_START, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack2, INV_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemStack2);
        }

        return itemStack;
    }

    public boolean isGrinding() {
        return this.propertyDelegate.get(0) > 0;
    }

    public int getGrindProgress() {
        int progress = this.propertyDelegate.get(2);
        int total = this.propertyDelegate.get(3);
        return total != 0 && progress != 0 ? progress * 24 / total : 0;
    }

    public int getFuelProgress() {
        int fuelTime = this.propertyDelegate.get(1);

        if (fuelTime == 0) {
            fuelTime = 200;
        }

        return this.propertyDelegate.get(0) * 13 / fuelTime;
    }

    private static class InputSlot extends Slot {
        public InputSlot(Container inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        public boolean mayPlace(ItemStack stack) {
            return OreGrinderBlockEntity.isGrindable(stack);
        }
    }

    private static class FuelSlot extends Slot {
        public FuelSlot(Container inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        public boolean mayPlace(ItemStack stack) {
            return OreGrinderBlockEntity.canUseAsFuel(stack);
        }
    }

    private static class PickaxeSlot extends Slot {
        public PickaxeSlot(Container inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        public boolean mayPlace(ItemStack stack) {
            return OreGrinderBlockEntity.isPickaxe(stack);
        }

        public int getMaxStackSize() {
            return 1;
        }
    }

    private static class OutputSlot extends Slot {
        public OutputSlot(Container inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}
