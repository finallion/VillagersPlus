package com.lion.villagersplus.blockentities;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.blocks.OccultistTableBlock;
import com.lion.villagersplus.init.VPBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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

    public void interact(Level world, Player player) {
        if (world.isClientSide()) {
            return;
        }

        if (player.isShiftKeyDown()) {
            deposit(player);
        } else {
            withdraw(player);
        }

        setChanged();
    }

    private void deposit(Player player) {
        if (levels > MAX_EXP_STORAGE - AMOUNT) {
            return;
        }

        int amount = Math.min(AMOUNT, currentExperience(player));

        if (amount > 0) {
            player.giveExperiencePoints(-amount);
            this.levels += amount;
        }
    }

    private void withdraw(Player player) {
        int amount = Math.min(AMOUNT, levels);

        if (amount > 0) {
            player.giveExperiencePoints(amount);
            this.levels -= amount;
        }
    }

    private static int currentExperience(Player player) {
        int level = player.experienceLevel;
        double pointsForLevels;

        if (level <= 16) {
            pointsForLevels = level * level + 6.0D * level;
        } else if (level <= 31) {
            pointsForLevels = 2.5D * level * level - 40.5D * level + 360.0D;
        } else {
            pointsForLevels = 4.5D * level * level - 162.5D * level + 2220.0D;
        }

        return (int) Math.round(pointsForLevels) + Math.round(player.experienceProgress * player.getXpNeededForNextLevel());
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState oldState) {
        super.preRemoveSideEffects(pos, oldState);

        if (getLevel() instanceof ServerLevel serverLevel && levels > 0 && oldState.getBlock() instanceof OccultistTableBlock block) {
            block.dropStoredExperience(serverLevel, pos, levels);
            levels = 0;
        }
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        this.levels = view.getIntOr("Levels", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        view.putInt("Levels", this.levels);
    }

}
