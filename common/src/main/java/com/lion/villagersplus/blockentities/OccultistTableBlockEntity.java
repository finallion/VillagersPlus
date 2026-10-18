package com.lion.villagersplus.blockentities;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.init.VPBlockEntities;
import net.minecraft.core.BlockPos;
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
        if (player.isShiftKeyDown()) {
            if (levels <= MAX_EXP_STORAGE - AMOUNT) {
                if (player.totalExperience < AMOUNT) {
                    if (!world.isClientSide()) {
                        this.levels += player.totalExperience;
                        player.giveExperiencePoints(-(player.totalExperience));
                    }
                } else {
                    if (!world.isClientSide()) {
                        player.giveExperiencePoints(-AMOUNT);
                        this.levels += AMOUNT;
                    }
                }
            }
        } else {
            if (levels > 0) {
                if (levels >= AMOUNT) {
                    if (!world.isClientSide()) {
                        player.giveExperiencePoints(AMOUNT);
                        this.levels -= AMOUNT;
                    }
                } else {
                    if (!world.isClientSide()) {
                        player.giveExperiencePoints(levels);
                        this.levels = 0;
                    }
                }
            }
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
