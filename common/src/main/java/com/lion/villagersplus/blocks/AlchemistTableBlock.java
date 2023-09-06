package com.lion.villagersplus.blocks;

import com.lion.villagersplus.blockentities.AlchemistTableBlockEntity;
import com.lion.villagersplus.init.VPBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AlchemistTableBlock extends WorkstationBlock {
    public static final BooleanProperty[] BOTTLE_PROPERTIES;
    public static final BooleanProperty HAS_FUEL;
    public static final BooleanProperty IS_BREWING;
    public static final DirectionProperty FACING;
    protected static final VoxelShape SHAPE;

    public AlchemistTableBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(BOTTLE_PROPERTIES[0], false).with(BOTTLE_PROPERTIES[1], false).with(BOTTLE_PROPERTIES[2], false).with(BOTTLE_PROPERTIES[3], false).with(IS_BREWING, false).with(HAS_FUEL, false).with(FACING, Direction.NORTH));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemistTableBlockEntity(pos, state);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, VPBlockEntities.ALCHEMIST_TABLE_BLOCK_ENTITY.get(), AlchemistTableBlockEntity::tick);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AlchemistTableBlockEntity) {
                player.openHandledScreen((AlchemistTableBlockEntity)blockEntity);
            }

            return ActionResult.CONSUME;
        }
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AlchemistTableBlockEntity) {
                ((AlchemistTableBlockEntity)blockEntity).setCustomName(itemStack.getName());
            }
        }
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        double d, f;
        double e = (double)pos.getY() + 0.125D + (double)random.nextFloat() * 0.15D;

        switch (state.get(FACING)) {
            default -> {
                d = (double)pos.getX() + 0.36D;
                f = (double)pos.getZ() + 0.15D;
            }
            case SOUTH -> {
                d = (double)pos.getX() + 1.0D - 0.36D;
                f = (double)pos.getZ() + 1.0D - 0.15D;
            }
            case EAST -> {
                d = (double)pos.getX() + 1.2D - 0.36D;
                f = (double)pos.getZ() + 0.37D;
            }
            case WEST -> {
                d = (double)pos.getX() + 0.15D;
                f = (double)pos.getZ() + 0.56D;
            }
        }

        if (state.get(IS_BREWING) && random.nextInt(3) == 0) {
            world.addParticle(ParticleTypes.FLAME, d, e, f, 0.0D, 0.0D, 0.0D);
        }

        if (state.get(HAS_FUEL) || state.get(IS_BREWING)) {
            world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0D, 0.05D, 0.0D);
        }
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AlchemistTableBlockEntity) {
                ItemScatterer.spawn(world, pos, (AlchemistTableBlockEntity)blockEntity);
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
        builder.add(BOTTLE_PROPERTIES[0], BOTTLE_PROPERTIES[1], BOTTLE_PROPERTIES[2], BOTTLE_PROPERTIES[3], HAS_FUEL, IS_BREWING, FACING);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
    }

    static {
        BOTTLE_PROPERTIES = new BooleanProperty[]{Properties.HAS_BOTTLE_0, Properties.HAS_BOTTLE_1, Properties.HAS_BOTTLE_2, Properties.EXTENDED};
        HAS_FUEL = BooleanProperty.of("has_fuel");
        IS_BREWING = BooleanProperty.of("is_brewing");
        FACING = HorizontalFacingBlock.FACING;
        SHAPE = createCuboidShape(0.0D, 1.0D, 0.0D, 16.0D, 15.0D, 16.0D);
    }
}
