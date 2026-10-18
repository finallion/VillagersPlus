package com.lion.villagersplus.blocks;

import com.mojang.serialization.MapCodec;
import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.blockentities.OccultistTableBlockEntity;
import com.lion.villagersplus.init.VPParticles;
import net.minecraft.world.level.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.*;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class OccultistTableBlock extends WorkstationBlock {
    public static final IntegerProperty FILLING;

    public static final MapCodec<OccultistTableBlock> CODEC = simpleCodec(OccultistTableBlock::new);

    @Override
    protected MapCodec<? extends OccultistTableBlock> codec() {
        return CODEC;
    }

    public OccultistTableBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FILLING, 0));
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OccultistTableBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof OccultistTableBlockEntity tile && !player.isCreative()) {
            tile.interact(world, player);

            if (world.isClientSide()) {
                if (player.isShiftKeyDown()) {
                    createParticleSpiral(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, 250, ParticleTypes.SOUL, world.getRandom());
                    world.playSound(player, pos, SoundEvents.SOUL_ESCAPE.value(), SoundSource.BLOCKS, 3.0F, 0.0F);
                } else {
                    createParticleSpiral(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, 250, VPParticles.EXPERIENCE_PARTICLE, world.getRandom());
                    world.playSound(player, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 3.0F, 1.0F);
                }
            }

            if (!world.isClientSide()) {
                int levels = tile.getLevels();
                int maxLevels = VillagersPlus.CONFIG.max_exp_amount;

                if (levels >= 0.8 * maxLevels) {
                    state = state.setValue(OccultistTableBlock.FILLING, 5);
                } else if (levels >= 0.6 * maxLevels) {
                    state = state.setValue(OccultistTableBlock.FILLING, 4);
                } else if (levels >= 0.4 * maxLevels) {
                    state = state.setValue(OccultistTableBlock.FILLING, 3);
                } else if (levels >= 0.2 * maxLevels) {
                    state = state.setValue(OccultistTableBlock.FILLING, 2);
                } else if (levels > 0) {
                    state = state.setValue(OccultistTableBlock.FILLING, 1);
                } else {
                    state = state.setValue(OccultistTableBlock.FILLING, 0);
                }

                world.setBlock(pos, state, 2);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;

    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    public static <T extends ParticleOptions> void createParticleSpiral(Level world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int length, T type, RandomSource random) {
        double yCoord = y + 1.1D;

        for (int i = 0; i < length; i++) {
            float densityFactor = (float) i / 15;
            double xCoord = x + Mth.sin(densityFactor) / 3;
            yCoord += 0.0075;
            double zCoord = z + Mth.cos(densityFactor) / 3;
            if (random.nextInt(7) == 0) {
                world.addParticle(type, xCoord, yCoord, zCoord, velocityX, velocityY, velocityZ);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (state.getValue(FILLING) > 0 && random.nextInt(3) == 0) {
            world.addParticle(VPParticles.EXPERIENCE_PARTICLE, pos.getX() + 0.5D + random.nextDouble() - random.nextDouble(), pos.getY() + 1.0D + random.nextDouble(), pos.getZ() + 0.5D + random.nextDouble() - random.nextDouble(), 0.0D, 0.05D, 0.0D);
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof OccultistTableBlockEntity table && !world.isClientSide()) {
            this.tryDropExperience((ServerLevel) world, pos, ItemStack.EMPTY, ConstantInt.of(table.getLevels()));
        }

        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FILLING);
    }

    static {
        FILLING = IntegerProperty.create("filling", 0, 5);
    }
}
