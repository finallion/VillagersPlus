package com.lion.villagersplus.blockentities;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.init.VPBlockEntities;
import net.minecraft.block.BlockState;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OccultistTableBlockEntity extends BlockEntity {
    private int levels = 0;
    private final int MAX_EXP_STORAGE = VillagersPlus.CONFIG.max_exp_amount;
    private final int AMOUNT = VillagersPlus.CONFIG.exp_amount;

    public OccultistTableBlockEntity(BlockPos pos, BlockState state) {
        super(VPBlockEntities.OCCULTIST_TABLE_BLOCK_ENTITY.get(), pos, state);
    }

    public int getLevels() {
        return levels;
    }

    public void interact(World world, PlayerEntity player) {
        if (world.isClient()) {
            return;
        }

        if (player.isSneaking()) {
            deposit(player);
        } else {
            withdraw(player);
        }

        markDirty();
    }

    private void deposit(PlayerEntity player) {
        if (levels > MAX_EXP_STORAGE - AMOUNT) {
            return;
        }

        int amount = Math.min(AMOUNT, currentExperience(player));

        if (amount > 0) {
            player.addExperience(-amount);
            this.levels += amount;
        }
    }

    private void withdraw(PlayerEntity player) {
        int amount = Math.min(AMOUNT, levels);

        if (amount > 0) {
            player.addExperience(amount);
            this.levels -= amount;
        }
    }

    private static int currentExperience(PlayerEntity player) {
        int level = player.experienceLevel;
        double pointsForLevels;

        if (level <= 16) {
            pointsForLevels = level * level + 6.0D * level;
        } else if (level <= 31) {
            pointsForLevels = 2.5D * level * level - 40.5D * level + 360.0D;
        } else {
            pointsForLevels = 4.5D * level * level - 162.5D * level + 2220.0D;
        }

        return (int) Math.round(pointsForLevels) + Math.round(player.experienceProgress * player.getNextLevelExperience());
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
