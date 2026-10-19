package com.lion.villagersplus.blockentities;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.blocks.OccultistTableBlock;
import com.lion.villagersplus.init.VPBlockEntities;
import net.minecraft.block.BlockState;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
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

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        super.onBlockReplaced(pos, oldState);

        if (getWorld() instanceof ServerWorld serverWorld && levels > 0 && oldState.getBlock() instanceof OccultistTableBlock block) {
            block.dropStoredExperience(serverWorld, pos, levels);
            levels = 0;
        }
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.levels = view.getInt("Levels", 0);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putInt("Levels", this.levels);
    }

}
