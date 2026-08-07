package com.lion.villagersplus.blockentities;

import com.lion.villagersplus.init.VPBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class HorticulturistTableBlockEntity extends BlockEntity implements Inventory, SidedInventory {
    public static final float MIN_PLANT_SCALE = 0.5F;
    public static final float MAX_PLANT_SCALE = 2.0F;
    // Tall plants at 2x would reach ~3 blocks and clip the block above / get section-culled.
    public static final float MAX_TALL_PLANT_SCALE = 1.5F;
    public static final float PLANT_SCALE_STEP = 0.25F;

    private DefaultedList<ItemStack> inventory;
    private float plantScale = 1.0F;
    private static final int[] SLOTS = new int[]{0, 1, 2, 3};

    public HorticulturistTableBlockEntity(BlockPos pos, BlockState state) {
        super(VPBlockEntities.HORTICULTURIST_TABLE_BLOCK_ENTITY.get(), pos, state);
        this.inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
    }

    public int size() {
        return this.inventory.size();
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        // createNbt runs writeData, which already serialises the whole inventory.
        return createNbt(registryLookup);
    }

    public float getPlantScale() {
        return this.plantScale;
    }

    /**
     * Adjusts the display size of the tub's plants by {@code delta}, clamped to [MIN, max].
     * @return true if the size actually changed (i.e. it was not already at the limit).
     */
    public boolean adjustPlantScale(float delta, float max) {
        float next = MathHelper.clamp(this.plantScale + delta, MIN_PLANT_SCALE, max);
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

    /** Removes and returns the flower in the given slot, syncing the change to clients. */
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
            // A freshly planted tub always starts at normal size.
            this.plantScale = 1.0F;
        }
        this.updateListeners();
        return removed;
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
        this.plantScale = view.getFloat("PlantScale", 1.0F);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        Inventories.writeData(view, this.inventory);
        view.putFloat("PlantScale", this.plantScale);
    }

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

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }
}
