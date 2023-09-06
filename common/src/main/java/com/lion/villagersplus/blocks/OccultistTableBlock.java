package com.lion.villagersplus.blocks;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.blockentities.OccultistTableBlockEntity;
import com.lion.villagersplus.init.VPParticles;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class OccultistTableBlock extends WorkstationBlock {
    public static final IntProperty FILLING;

    public OccultistTableBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FILLING, 0));
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OccultistTableBlockEntity(pos, state);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof OccultistTableBlockEntity tile && !player.isCreative()) {
            tile.interact(world, player);

            if (world.isClient) {
                if (player.isSneaking()) {
                    createParticleSpiral(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, 250, ParticleTypes.SOUL, world.random);
                    world.playSound(player, pos, SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.BLOCKS, 3.0F, 0.0F);
                } else {
                    createParticleSpiral(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, 250, VPParticles.EXPERIENCE_PARTICLE, world.random);
                    world.playSound(player, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 3.0F, 1.0F);
                }
            }

            if (!world.isClient()) {
                int levels = tile.getLevels();
                int maxLevels = VillagersPlus.CONFIG.max_exp_amount;

                if (levels >= 0.8 * maxLevels) {
                    state = state.with(OccultistTableBlock.FILLING, 5);
                } else if (levels >= 0.6 * maxLevels) {
                    state = state.with(OccultistTableBlock.FILLING, 4);
                } else if (levels >= 0.4 * maxLevels) {
                    state = state.with(OccultistTableBlock.FILLING, 3);
                } else if (levels >= 0.2 * maxLevels) {
                    state = state.with(OccultistTableBlock.FILLING, 2);
                } else if (levels > 0) {
                    state = state.with(OccultistTableBlock.FILLING, 1);
                } else {
                    state = state.with(OccultistTableBlock.FILLING, 0);
                }

                world.setBlockState(pos, state, 2);
            }
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;

    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }



    public static <T extends ParticleEffect> void createParticleSpiral(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int length, T type, Random random) {
        double yCoord = y + 1.1D; // top of block

        for (int i = 0; i < length; i++) {
            float densityFactor = (float) i / 15;
            double xCoord = x + MathHelper.sin(densityFactor) / 3;
            yCoord += 0.0075;
            double zCoord = z + MathHelper.cos(densityFactor) / 3;
            if (random.nextInt(7) == 0) {
                world.addParticle(type, xCoord, yCoord, zCoord, velocityX, velocityY, velocityZ);
            }
        }
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(FILLING) > 0 && random.nextInt(3) == 0) {
            world.addParticle(VPParticles.EXPERIENCE_PARTICLE, pos.getX() + 0.5D + random.nextDouble() - random.nextDouble(), pos.getY() + 1.0D + random.nextDouble(), pos.getZ() + 0.5D + random.nextDouble() - random.nextDouble(), 0.0D, 0.05D, 0.0D);
        }
    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof OccultistTableBlockEntity table && !world.isClient()) {
                this.dropExperienceWhenMined((ServerWorld) world, pos, ItemStack.EMPTY, ConstantIntProvider.create(table.getLevels()));
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FILLING);
    }

    static {
        FILLING = IntProperty.of("filling", 0, 5);
    }
}
