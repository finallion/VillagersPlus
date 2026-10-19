package com.lion.villagersplus.blockentities;

import com.lion.villagersplus.init.VPBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class HorticulturistTableBlockEntity extends BlockEntity implements Container, WorldlyContainer {
    public static final float MIN_PLANT_SCALE = 0.5F;
    public static final float MAX_PLANT_SCALE = 2.0F;
    // Tall plants at 2x reach about 3 blocks and get culled with the section above.
    public static final float MAX_TALL_PLANT_SCALE = 1.5F;
    public static final float PLANT_SCALE_STEP = 0.25F;

    private NonNullList<ItemStack> inventory;
    private float plantScale = 1.0F;
    private static final int[] SLOTS = new int[]{0, 1, 2, 3};

    public HorticulturistTableBlockEntity(BlockPos pos, BlockState state) {
        super(VPBlockEntities.HORTICULTURIST_TABLE_BLOCK_ENTITY.get(), pos, state);
        this.inventory = NonNullList.withSize(4, ItemStack.EMPTY);
    }

    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }

    public float getPlantScale() {
        return this.plantScale;
    }

    public boolean adjustPlantScale(float delta, float max) {
        float next = Mth.clamp(this.plantScale + delta, MIN_PLANT_SCALE, max);
        if (next == this.plantScale) {
            return false;
        }
        this.plantScale = next;
        this.updateListeners();
        return true;
    }

    public boolean insertFlower(ItemStack flower, int slot) {
        ItemStack itemStack = (ItemStack)this.inventory.get(slot);
        if (itemStack.isEmpty()) {
            this.inventory.set(slot, flower.split(1));
            this.updateListeners();
            return true;
        }
        return false;
    }

    public ItemStack removeFlower(int slot) {
        if (slot < 0 || slot >= this.inventory.size()) {
            return ItemStack.EMPTY;
        }
        ItemStack removed = this.inventory.get(slot);
        if (removed.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.inventory.set(slot, ItemStack.EMPTY);
        if (this.isEmpty()) {
            this.plantScale = 1.0F;
        }
        this.updateListeners();
        return removed;
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
        this.plantScale = view.getFloatOr("PlantScale", 1.0F);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        ContainerHelper.saveAllItems(view, this.inventory);
        view.putFloat("PlantScale", this.plantScale);
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
        return false;
    }

    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.canPlaceItem(slot, stack);
    }

    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return false;
    }
}
