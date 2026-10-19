package com.lion.villagersplus.client.screen;

import com.lion.villagersplus.init.VPScreens;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

public class AlchemistTableScreenHandler extends AbstractContainerMenu {
    private final Container inventory;
    private final ContainerData propertyDelegate;
    private final Slot ingredientSlot;

    public AlchemistTableScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(5), new SimpleContainerData(2));
    }

    public AlchemistTableScreenHandler(
            int syncId,
            Inventory playerInventory,
            Container inventory,
            ContainerData propertyDelegate
    ) {
        super(VPScreens.ALCHEMIST_TABLE_SCREEN_HANDLER, syncId);
        checkContainerSize(inventory, 5);
        checkContainerDataCount(propertyDelegate, 2);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.addSlot(new AlchemistTableScreenHandler.PotionSlot(inventory, 0, 56, 51));
        this.addSlot(new AlchemistTableScreenHandler.PotionSlot(inventory, 1, 79, 58));
        this.addSlot(new AlchemistTableScreenHandler.PotionSlot(inventory, 2, 102, 51));
        this.ingredientSlot = this.addSlot(new AlchemistTableScreenHandler.IngredientSlot(inventory, 3, 79, 17));
        this.addSlot(new AlchemistTableScreenHandler.FuelSlot(inventory, 4, 17, 17));
        this.addDataSlots(propertyDelegate);

        int i;
        for(i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

    }

    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();

            if ((index < 0 || index > 2) && index != 3 && index != 4) {
                if (AlchemistTableScreenHandler.FuelSlot.matches(itemStack)) {
                    if (this.moveItemStackTo(itemStack2, 4, 5, false) || this.ingredientSlot.mayPlace(itemStack2) && !this.moveItemStackTo(itemStack2, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.ingredientSlot.mayPlace(itemStack2)) {
                    if (!this.moveItemStackTo(itemStack2, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (AlchemistTableScreenHandler.PotionSlot.matches(itemStack) && itemStack.getCount() == 1) {
                    if (!this.moveItemStackTo(itemStack2, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 5 && index < 32) {
                    if (!this.moveItemStackTo(itemStack2, 32, 41, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 32 && index < 41) {
                    if (!this.moveItemStackTo(itemStack2, 5, 32, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemStack2, 5, 41, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemStack2, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemStack2, itemStack);
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

    public int getFuel() {
        return this.propertyDelegate.get(1);
    }

    public int getBrewTime() {
        return this.propertyDelegate.get(0);
    }

    static class PotionSlot extends Slot {
        public PotionSlot(Container inventory, int i, int j, int k) {
            super(inventory, i, j, k);
        }

        public boolean mayPlace(ItemStack stack) {
            return matches(stack);
        }

        public int getMaxStackSize() {
            return 1;
        }

        public static boolean matches(ItemStack stack) {
            return stack.is(Items.GLASS_BOTTLE) || stack.is(Items.POTION);
        }
    }

    private static class IngredientSlot extends Slot {
        public IngredientSlot(Container inventory, int i, int j, int k) {
            super(inventory, i, j, k);
        }

        public boolean mayPlace(ItemStack stack) {
            return matches(stack);
        }

        public int getMaxStackSize() {
            return 1;
        }

        public static boolean matches(ItemStack stack) {
            // Water bottles do not match. Items without a potion component pass.
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);

            if (contents != null && contents.is(Potions.WATER)) {
                return false;
            }

            return stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION);
        }
    }

    private static class FuelSlot extends Slot {
        public FuelSlot(Container inventory, int i, int j, int k) {
            super(inventory, i, j, k);
        }

        public boolean mayPlace(ItemStack stack) {
            return matches(stack);
        }

        public static boolean matches(ItemStack stack) {
            return stack.is(Items.GUNPOWDER);
        }

        public int getMaxStackSize() {
            return 64;
        }
    }
}

