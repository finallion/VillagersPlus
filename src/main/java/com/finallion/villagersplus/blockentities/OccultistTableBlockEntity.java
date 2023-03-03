package com.finallion.villagersplus.blockentities;

import com.finallion.villagersplus.blocks.OccultistTableBlock;
import com.finallion.villagersplus.init.ModBlocks;
import net.minecraft.block.BlockState;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class OccultistTableBlockEntity extends BlockEntity {
    private int levels = 0;
    private final int MAX_EXP_STORAGE = 500;
    private final int AMOUNT = 50;

    public OccultistTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.OCCULTIST_TABLE_BLOCK_ENTITY, pos, state);
    }

    public int getLevels() {
        return levels;
    }

    public void interact(World world, PlayerEntity player) {
        if (player.isSneaking()) {
            if (levels <= MAX_EXP_STORAGE - AMOUNT) {
                if (player.totalExperience < AMOUNT) {
                    if (!world.isClient()) {
                        this.levels += player.totalExperience;
                        player.addExperience(-(player.totalExperience));
                    }
                } else {
                    if (!world.isClient()) {
                        player.addExperience(-AMOUNT);
                        this.levels += AMOUNT;
                    }
                }
            }
        } else {
            if (levels > 0) {
                if (levels >= AMOUNT) {
                    if (!world.isClient()) {
                        player.addExperience(AMOUNT);
                        this.levels -= AMOUNT;
                    }
                } else {
                    if (!world.isClient()) {
                        player.addExperience(levels);
                        this.levels = 0;
                    }

                }
            }
        }
    }



    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.levels = nbt.getInt("Levels");
    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("Levels", this.levels);
    }

}
