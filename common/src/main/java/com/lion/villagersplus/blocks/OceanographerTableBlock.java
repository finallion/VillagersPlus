package com.lion.villagersplus.blocks;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import com.lion.villagersplus.blockentities.OceanographerTableBlockEntity;
import com.lion.villagersplus.init.VPBlockEntities;
import com.lion.villagersplus.init.VPItems;
import com.lion.villagersplus.init.VPParticles;
import com.lion.villagersplus.init.VPTags;
import net.minecraft.world.level.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.item.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.*;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class OceanographerTableBlock extends WorkstationBlock {
    public static final IntegerProperty CORALS;
    public static final IntegerProperty FISH;
    public static final EnumProperty<Direction> FACING;
    public static final BooleanProperty IS_FILLED;
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;
    public static final BooleanProperty STANDALONE;

    public OceanographerTableBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(CORALS, 0)
                .setValue(FISH, 0)
                .setValue(IS_FILLED, false)
                .setValue(FACING, Direction.NORTH)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(STANDALONE, false));
    }

    public static BooleanProperty connectionProperty(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    private boolean connectsTo(BlockState state, BlockState neighborState) {
        return !state.getValue(STANDALONE) && neighborState.is(this) && !neighborState.getValue(STANDALONE);
    }

    private Set<BlockPos> collectTank(LevelReader world, BlockPos start, int budget) {
        Set<BlockPos> tank = new HashSet<>();
        tank.add(start);

        BlockState startState = world.getBlockState(start);

        if (!startState.is(this) || startState.getValue(STANDALONE)) {
            return tank;
        }

        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        while (!queue.isEmpty() && tank.size() <= budget) {
            BlockPos current = queue.poll();
            BlockState currentState = world.getBlockState(current);
            for (Direction direction : Direction.values()) {
                if (!currentState.getValue(connectionProperty(direction))) {
                    continue;
                }

                BlockPos next = current.relative(direction);
                BlockState nextState = world.getBlockState(next);

                if (nextState.is(this) && !nextState.getValue(STANDALONE) && tank.add(next)) {
                    queue.add(next);
                }
            }
        }
        return tank;
    }

    /// Both sides of a seam give the same answer.
    private boolean joinStaysWithinLimit(LevelReader world, BlockPos pos, BlockPos neighborPos) {
        int limit = OceanographerTableBlockEntity.MAX_TANK_BLOCKS;
        Set<BlockPos> tank = collectTank(world, pos, limit);

        if (tank.contains(neighborPos)) {
            return true;
        }

        tank.addAll(collectTank(world, neighborPos, limit));
        return tank.size() <= limit;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OceanographerTableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level world,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (!world.isClientSide()) {
            return null;
        }

        return createTickerHelper(
                type,
                VPBlockEntities.OCEANOGRAPHER_TABLE_BLOCK_ENTITY.get(),
                OceanographerTableBlockEntity::clientTick
        );
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (random.nextBoolean()) {
            double x = pos.getX() + 0.1D + (pos.getX() + 0.9D - (pos.getX() + 0.1D)) * random.nextDouble();
            double y = pos.getY() + 0.1D + (pos.getY() + 0.4D - (pos.getY() + 0.1D)) * random.nextDouble();
            double z = pos.getZ() + 0.1D + (pos.getZ() + 0.9D - (pos.getZ() + 0.1D)) * random.nextDouble();

            world.addParticle(VPParticles.BUBBLE_PARTICLE, x, y, z, 0.0D, 0.000001D, 0.0D);
        }

    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack itemStack,
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {

        if (world.getBlockEntity(pos) instanceof OceanographerTableBlockEntity blockEntity) {
                boolean isFishFood = itemStack.is(VPItems.FISH_FOOD.get());
                boolean isDietFood = itemStack.is(VPItems.DIET_FOOD.get());

                if ((isFishFood || isDietFood) && state.getValue(FISH) >= 1) {
                    if (!world.isClientSide()) {
                        float delta = isFishFood ? OceanographerTableBlockEntity.FISH_SCALE_STEP
                                                 : -OceanographerTableBlockEntity.FISH_SCALE_STEP;

                        if (blockEntity.adjustFishScale(delta)) {
                            if (!player.getAbilities().instabuild) {
                                itemStack.shrink(1);
                            }

                            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                        }
                    } else {
                        world.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, isFishFood ? 1.2F : 0.6F);
                    }

                    return InteractionResult.SUCCESS;
                }

                boolean isBoneMeal = itemStack.is(Items.BONE_MEAL);
                boolean isShears = itemStack.is(Items.SHEARS);

                if ((isBoneMeal || isShears) && state.getValue(CORALS) >= 1) {
                    if (!world.isClientSide()) {
                        float delta = isBoneMeal ? OceanographerTableBlockEntity.CORAL_SCALE_STEP
                                                 : -OceanographerTableBlockEntity.CORAL_SCALE_STEP;

                        if (blockEntity.adjustCoralScale(delta)) {
                            if (isBoneMeal) {
                                if (!player.getAbilities().instabuild) {
                                    itemStack.shrink(1);
                                }
                            } else {
                                itemStack.hurtAndBreak(1, player, hand.asEquipmentSlot());
                            }

                            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                        }
                    } else {
                        world.playSound(null, pos, isBoneMeal ? SoundEvents.BONE_MEAL_USE : SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }

                    return InteractionResult.SUCCESS;
                }

                if (itemStack.is(VPItems.CALM_FOOD.get()) && state.getValue(FISH) >= 1) {
                    if (!world.isClientSide()) {
                        blockEntity.toggleStationary();

                        if (!player.getAbilities().instabuild) {
                            itemStack.shrink(1);
                        }

                        world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    } else {
                        world.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 0.9F);
                    }

                    return InteractionResult.SUCCESS;
                }

                if (itemStack.is(VPTags.AQUARIUM_PLANTABLE_ITEMS) && state.getValue(CORALS) < 4 && !state.getValue(DOWN)) {
                    if (!blockEntity.insertCoral(itemStack, state.getValue(CORALS))) {
                        return InteractionResult.TRY_WITH_EMPTY_HAND;
                    }

                    if (!world.isClientSide()) {
                        world.setBlock(pos, state.setValue(CORALS, state.getValue(CORALS) + 1), 3);
                        world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    }

                    if (world.isClientSide()) {
                        world.playSound(null, pos, SoundEvents.CORAL_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }

                    return InteractionResult.SUCCESS;
                } else if (itemStack.getItem() instanceof MobBucketItem && state.getValue(FISH) < 1) {
                    if (!blockEntity.insertCoral(itemStack.copy(), OceanographerTableBlockEntity.FISH_SLOT)) {
                        return InteractionResult.TRY_WITH_EMPTY_HAND;
                    }

                    if (!world.isClientSide()) {
                        world.setBlock(pos, state.setValue(FISH, state.getValue(FISH) + 1), 3);
                        player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.BUCKET)));
                        world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    }

                    if (world.isClientSide()) {
                        world.playPlayerSound(SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }

                    return InteractionResult.SUCCESS;
                } else if ((itemStack.is(Items.WATER_BUCKET) || itemStack.is(Items.BUCKET)) && state.getValue(FISH) >= 1) {
                    ItemStack fish = blockEntity.getItem(OceanographerTableBlockEntity.FISH_SLOT);

                    if (!fish.isEmpty()) {
                        if (!world.isClientSide()) {
                            ItemStack fishBucket = blockEntity.extractFish();
                            player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, fishBucket));
                            world.setBlock(pos, state.setValue(FISH, 0), 3);
                            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                        } else {
                            world.playPlayerSound(SoundEvents.BUCKET_FILL_FISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                        }

                        return InteractionResult.SUCCESS;
                    }
                }
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof OceanographerTableBlockEntity) {
            Containers.dropContents(world, pos, (OceanographerTableBlockEntity)blockEntity);
        }

        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CORALS, FISH, IS_FILLED, FACING, NORTH, EAST, SOUTH, WEST, UP, DOWN, STANDALONE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        boolean standalone = ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown();

        BlockState state = this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(STANDALONE, standalone);

        // Cap the tank per side, so a block between two tanks joins the one with room.
        LevelReader world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        int limit = OceanographerTableBlockEntity.MAX_TANK_BLOCKS;
        Set<BlockPos> tank = new HashSet<>();
        tank.add(pos);

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            boolean connect = this.connectsTo(state, world.getBlockState(neighborPos));

            if (connect && !tank.contains(neighborPos)) {
                Set<BlockPos> branch = collectTank(world, neighborPos, limit);

                if (tank.size() + branch.size() > limit) {
                    connect = false;
                } else {
                    tank.addAll(branch);
                }
            }

            state = state.setValue(connectionProperty(direction), connect);
        }
        return state;
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            net.minecraft.world.level.LevelReader world,
            net.minecraft.world.level.ScheduledTickAccess tickView,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            net.minecraft.util.RandomSource random
    ) {
        // An open seam stays open. Only a new seam is checked against the cap.
        boolean connect = this.connectsTo(state, neighborState)
                && (state.getValue(connectionProperty(direction)) || joinStaysWithinLimit(world, pos, neighborPos));

        if (world.isClientSide() && world.getBlockEntity(pos) instanceof OceanographerTableBlockEntity tank) {
            tank.requestTankRescan();
        }

        return state.setValue(connectionProperty(direction), connect);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        // Connecting upward removes the upper floor, so existing corals there pop out.
        if (!world.isClientSide() && state.getValue(UP)) {
            BlockPos above = pos.above();
            BlockState aboveState = world.getBlockState(above);

            if (aboveState.is(this) && aboveState.getValue(CORALS) > 0
                    && world.getBlockEntity(above) instanceof OceanographerTableBlockEntity blockEntity) {
                blockEntity.ejectCorals();
                world.setBlock(above, aboveState.setValue(CORALS, 0), 3);
            }
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        BlockState result = state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
        return switch (rotation) {
            case CLOCKWISE_180 -> result.setValue(NORTH, state.getValue(SOUTH))
                    .setValue(EAST, state.getValue(WEST))
                    .setValue(SOUTH, state.getValue(NORTH))
                    .setValue(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90 -> result.setValue(NORTH, state.getValue(EAST))
                    .setValue(EAST, state.getValue(SOUTH))
                    .setValue(SOUTH, state.getValue(WEST))
                    .setValue(WEST, state.getValue(NORTH));
            case CLOCKWISE_90 -> result.setValue(NORTH, state.getValue(WEST))
                    .setValue(EAST, state.getValue(NORTH))
                    .setValue(SOUTH, state.getValue(EAST))
                    .setValue(WEST, state.getValue(SOUTH));
            default -> result;
        };
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        Direction facing = state.getValue(FACING);
        return switch (mirror) {
            case LEFT_RIGHT -> state.setValue(FACING, facing.getAxis() == Direction.Axis.Z ? facing.getOpposite() : facing)
                    .setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
            case FRONT_BACK -> state.setValue(FACING, facing.getAxis() == Direction.Axis.X ? facing.getOpposite() : facing)
                    .setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
            default -> state;
        };
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return (Integer)state.getValue(FISH) + state.getValue(CORALS);
    }

    static {
        IS_FILLED = BooleanProperty.create("is_filled");
        FISH = IntegerProperty.create("fish", 0, 1);
        CORALS = IntegerProperty.create("corals", 0, 4);
        FACING = HorizontalDirectionalBlock.FACING;
        NORTH = net.minecraft.world.level.block.state.properties.BlockStateProperties.NORTH;
        EAST = net.minecraft.world.level.block.state.properties.BlockStateProperties.EAST;
        SOUTH = net.minecraft.world.level.block.state.properties.BlockStateProperties.SOUTH;
        WEST = net.minecraft.world.level.block.state.properties.BlockStateProperties.WEST;
        UP = net.minecraft.world.level.block.state.properties.BlockStateProperties.UP;
        DOWN = net.minecraft.world.level.block.state.properties.BlockStateProperties.DOWN;
        STANDALONE = BooleanProperty.create("standalone");
    }
}
