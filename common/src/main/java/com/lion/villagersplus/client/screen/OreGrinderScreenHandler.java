package com.lion.villagersplus.client.screen;

import com.lion.villagersplus.blockentities.OreGrinderBlockEntity;
import com.lion.villagersplus.init.VPScreens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class OreGrinderScreenHandler extends ScreenHandler {
    /** Number of machine slots before the player inventory begins. */
    private static final int MACHINE_SLOTS = OreGrinderBlockEntity.INVENTORY_SIZE;
    private static final int INV_START = MACHINE_SLOTS;
    private static final int HOTBAR_START = INV_START + 27;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    /** Fuel is a per-world FuelRegistry since 1.21.2, so the handler has to remember its world. */
    private final net.minecraft.world.World world;

    public OreGrinderScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(OreGrinderBlockEntity.INVENTORY_SIZE), new ArrayPropertyDelegate(4));
    }

    public OreGrinderScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(VPScreens.ORE_GRINDER_SCREEN_HANDLER, syncId);
        checkSize(inventory, OreGrinderBlockEntity.INVENTORY_SIZE);
        checkDataCount(propertyDelegate, 4);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.world = playerInventory.player.getWorld();

        this.addSlot(new InputSlot(inventory, OreGrinderBlockEntity.INPUT_SLOT, 56, 17));
        this.addSlot(new FuelSlot(inventory, OreGrinderBlockEntity.FUEL_SLOT, 56, 53, this.world));
        this.addSlot(new OutputSlot(inventory, OreGrinderBlockEntity.OUTPUT_SLOT, 116, 35));
        this.addSlot(new PickaxeSlot(inventory, OreGrinderBlockEntity.PICKAXE_SLOT, 22, 36));
        this.addProperties(propertyDelegate);

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

    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index == OreGrinderBlockEntity.OUTPUT_SLOT) {
                // From output into player inventory.
                if (!this.insertItem(itemStack2, INV_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(itemStack2, itemStack);
            } else if (index >= INV_START) {
                // From player inventory into the machine.
                if (OreGrinderBlockEntity.canUseAsFuel(this.world, itemStack2)) {
                    if (!this.insertItem(itemStack2, OreGrinderBlockEntity.FUEL_SLOT, OreGrinderBlockEntity.FUEL_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (OreGrinderBlockEntity.isPickaxe(itemStack2)) {
                    if (!this.insertItem(itemStack2, OreGrinderBlockEntity.PICKAXE_SLOT, OreGrinderBlockEntity.PICKAXE_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.inventory.isValid(OreGrinderBlockEntity.INPUT_SLOT, itemStack2)) {
                    if (!this.insertItem(itemStack2, OreGrinderBlockEntity.INPUT_SLOT, OreGrinderBlockEntity.INPUT_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= INV_START && index < HOTBAR_START) {
                    // Main inventory -> hotbar.
                    if (!this.insertItem(itemStack2, HOTBAR_START, HOTBAR_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= HOTBAR_START && index < HOTBAR_END && !this.insertItem(itemStack2, INV_START, HOTBAR_START, false)) {
                    // Hotbar -> main inventory.
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, INV_START, HOTBAR_END, false)) {
                // From an input/fuel/pickaxe slot back into the player inventory.
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);
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
        public InputSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        public boolean canInsert(ItemStack stack) {
            // Only ores that have a grinding recipe are accepted.
            return OreGrinderBlockEntity.isGrindable(stack);
        }
    }

    private static class FuelSlot extends Slot {
        private final net.minecraft.world.World world;

        public FuelSlot(Inventory inventory, int index, int x, int y, net.minecraft.world.World world) {
            super(inventory, index, x, y);
            this.world = world;
        }

        public boolean canInsert(ItemStack stack) {
            return OreGrinderBlockEntity.canUseAsFuel(this.world, stack);
        }
    }

    private static class PickaxeSlot extends Slot {
        public PickaxeSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        public boolean canInsert(ItemStack stack) {
            return OreGrinderBlockEntity.isPickaxe(stack);
        }

        public int getMaxItemCount() {
            return 1;
        }
    }

    private static class OutputSlot extends Slot {
        public OutputSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        public boolean canInsert(ItemStack stack) {
            return false;
        }
    }
}
