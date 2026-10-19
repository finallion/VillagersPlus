package com.lion.villagersplus.blocks;

import com.mojang.serialization.MapCodec;
import com.lion.villagersplus.blockentities.AlchemistTableBlockEntity;
import com.lion.villagersplus.init.VPBlockEntities;
import net.minecraft.world.level.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.*;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class AlchemistTableBlock extends WorkstationBlock {
    public static final BooleanProperty[] BOTTLE_PROPERTIES;
    public static final BooleanProperty HAS_FUEL;
    public static final BooleanProperty IS_BREWING;
    public static final EnumProperty<Direction> FACING;
    protected static final VoxelShape SHAPE;

    public static final MapCodec<AlchemistTableBlock> CODEC = simpleCodec(AlchemistTableBlock::new);

    @Override
    protected MapCodec<? extends AlchemistTableBlock> codec() {
        return CODEC;
    }

    public AlchemistTableBlock(BlockBehaviour.Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BOTTLE_PROPERTIES[0], false)
                .setValue(BOTTLE_PROPERTIES[1], false)
                .setValue(BOTTLE_PROPERTIES[2], false)
                .setValue(BOTTLE_PROPERTIES[3], false)
                .setValue(IS_BREWING, false)
                .setValue(HAS_FUEL, false)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemistTableBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 1.0F;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level world,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return world.isClientSide() ? null : createTickerHelper(type, VPBlockEntities.ALCHEMIST_TABLE_BLOCK_ENTITY.get(), AlchemistTableBlockEntity::tick);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof AlchemistTableBlockEntity) {
                player.openMenu((AlchemistTableBlockEntity)blockEntity);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        double d, f;
        double e = (double)pos.getY() + 0.125D + (double)random.nextFloat() * 0.15D;

        switch (state.getValue(FACING)) {
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

        if (state.getValue(IS_BREWING) && random.nextInt(3) == 0) {
            world.addParticle(ParticleTypes.FLAME, d, e, f, 0.0D, 0.0D, 0.0D);
        }

        if (state.getValue(HAS_FUEL) || state.getValue(IS_BREWING)) {
            world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0D, 0.05D, 0.0D);
        }
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof AlchemistTableBlockEntity) {
            Containers.dropContents(world, pos, (AlchemistTableBlockEntity)blockEntity);
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
        builder.add(BOTTLE_PROPERTIES[0], BOTTLE_PROPERTIES[1], BOTTLE_PROPERTIES[2], BOTTLE_PROPERTIES[3], HAS_FUEL, IS_BREWING, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
    }

    static {
        BOTTLE_PROPERTIES = new BooleanProperty[]{BlockStateProperties.HAS_BOTTLE_0, BlockStateProperties.HAS_BOTTLE_1, BlockStateProperties.HAS_BOTTLE_2, BlockStateProperties.EXTENDED};
        HAS_FUEL = BooleanProperty.create("has_fuel");
        IS_BREWING = BooleanProperty.create("is_brewing");
        FACING = HorizontalDirectionalBlock.FACING;
        SHAPE = box(0.0D, 1.0D, 0.0D, 16.0D, 15.0D, 16.0D);
    }
}
