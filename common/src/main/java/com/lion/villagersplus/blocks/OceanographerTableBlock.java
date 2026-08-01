package com.lion.villagersplus.blocks;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import com.mojang.serialization.MapCodec;
import com.lion.villagersplus.blockentities.OceanographerTableBlockEntity;
import com.lion.villagersplus.init.VPBlockEntities;
import com.lion.villagersplus.init.VPItems;
import com.lion.villagersplus.init.VPParticles;
import com.lion.villagersplus.init.VPTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class OceanographerTableBlock extends WorkstationBlock {
    public static final IntProperty CORALS;
    public static final IntProperty FISH;
    public static final DirectionProperty FACING;
    public static final BooleanProperty IS_FILLED;
    // Connections to adjacent aquariums; a connected side loses its glass wall (or lid/floor
    // for vertical connections) so several blocks merge into one larger tank.
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;
    // Sneak-placed aquariums never connect (in either direction).
    public static final BooleanProperty STANDALONE;

    /** BlockWithEntity requires a codec as of 1.20.5; this block has no state beyond its settings. */
    public static final MapCodec<OceanographerTableBlock> CODEC = createCodec(OceanographerTableBlock::new);

    @Override
    protected MapCodec<? extends OceanographerTableBlock> getCodec() {
        return CODEC;
    }

    public OceanographerTableBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(CORALS, 0).with(FISH, 0).with(IS_FILLED, false).with(FACING, Direction.NORTH)
                .with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false).with(STANDALONE, false));
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

    /** Mutual consent: both blocks must be non-standalone aquariums to connect. */
    private boolean connectsTo(BlockState state, BlockState neighborState) {
        return !state.get(STANDALONE) && neighborState.isOf(this) && !neighborState.get(STANDALONE);
    }

    /**
     * {@return how many aquariums the tank would hold if a block were placed at {@code pos}}
     *
     * <p>Counts the block itself plus every tank it would join - placing between two separate tanks
     * merges them, so they are walked as one. Follows the same connection properties as the block
     * entity's own scan, so both agree on what "one tank" means. The walk stops once the count is
     * past {@code limit}, which keeps it cheap and bounded even next to an oversized structure built
     * before this cap existed.
     */
    private int mergedTankSize(WorldView world, BlockPos pos, int limit) {
        Set<BlockPos> seen = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        seen.add(pos);

        // The new block has no connection properties yet; it would connect to every adjacent
        // non-standalone aquarium, so those are the roots of the walk.
        for (Direction direction : Direction.values()) {
            BlockPos neighbor = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighbor);
            if (neighborState.isOf(this) && !neighborState.get(STANDALONE) && seen.add(neighbor)) {
                queue.add(neighbor);
            }
        }

        while (!queue.isEmpty() && seen.size() <= limit) {
            BlockPos current = queue.poll();
            BlockState currentState = world.getBlockState(current);
            for (Direction direction : Direction.values()) {
                if (!currentState.get(connectionProperty(direction))) {
                    continue;
                }
                BlockPos next = current.offset(direction);
                BlockState nextState = world.getBlockState(next);
                if (nextState.isOf(this) && !nextState.get(STANDALONE) && seen.add(next)) {
                    queue.add(next);
                }
            }
        }
        return seen.size();
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OceanographerTableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        // Only the client needs to tick: it drives the display fish's swim animation.
        if (!world.isClient) {
            return null;
        }
        return validateTicker(type, VPBlockEntities.OCEANOGRAPHER_TABLE_BLOCK_ENTITY.get(),
                OceanographerTableBlockEntity::clientTick);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextBoolean()) {
            double x = pos.getX() + 0.1D + (pos.getX() + 0.9D - (pos.getX() + 0.1D)) * random.nextDouble();
            double y = pos.getY() + 0.1D + (pos.getY() + 0.4D - (pos.getY() + 0.1D)) * random.nextDouble();
            double z = pos.getZ() + 0.1D + (pos.getZ() + 0.9D - (pos.getZ() + 0.1D)) * random.nextDouble();

            world.addParticle(VPParticles.BUBBLE_PARTICLE, x, y, z, 0.0D, 0.000001D, 0.0D);
        }

    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack itemStack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (world.getBlockEntity(pos) instanceof OceanographerTableBlockEntity blockEntity) {
                boolean isFishFood = itemStack.isOf(VPItems.FISH_FOOD.get());
                boolean isDietFood = itemStack.isOf(VPItems.DIET_FOOD.get());
                if ((isFishFood || isDietFood) && state.get(FISH) >= 1) {
                    if (!world.isClient()) {
                        float delta = isFishFood ? OceanographerTableBlockEntity.FISH_SCALE_STEP
                                                 : -OceanographerTableBlockEntity.FISH_SCALE_STEP;
                        if (blockEntity.adjustFishScale(delta)) {
                            if (!player.getAbilities().creativeMode) {
                                itemStack.decrement(1);
                            }
                            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                        }
                    } else {
                        world.playSoundAtBlockCenter(pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1.0F, isFishFood ? 1.2F : 0.6F, false);
                    }
                    return ItemActionResult.success(world.isClient);
                }

                // Bone meal grows the corals, shears trim them back down (mirrors the fish foods).
                boolean isBoneMeal = itemStack.isOf(Items.BONE_MEAL);
                boolean isShears = itemStack.isOf(Items.SHEARS);
                if ((isBoneMeal || isShears) && state.get(CORALS) >= 1) {
                    if (!world.isClient()) {
                        float delta = isBoneMeal ? OceanographerTableBlockEntity.CORAL_SCALE_STEP
                                                 : -OceanographerTableBlockEntity.CORAL_SCALE_STEP;
                        if (blockEntity.adjustCoralScale(delta)) {
                            if (isBoneMeal) {
                                if (!player.getAbilities().creativeMode) {
                                    itemStack.decrement(1);
                                }
                            } else {
                                itemStack.damage(1, player, LivingEntity.getSlotForHand(hand));
                            }
                            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                        }
                    } else {
                        world.playSoundAtBlockCenter(pos, isBoneMeal ? SoundEvents.ITEM_BONE_MEAL_USE : SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                    }
                    return ItemActionResult.success(world.isClient);
                }

                if (itemStack.isOf(VPItems.CALM_FOOD.get()) && state.get(FISH) >= 1) {
                    if (!world.isClient()) {
                        blockEntity.toggleStationary();
                        if (!player.getAbilities().creativeMode) {
                            itemStack.decrement(1);
                        }
                        world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    } else {
                        world.playSoundAtBlockCenter(pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1.0F, 0.9F, false);
                    }
                    return ItemActionResult.success(world.isClient);
                }

                // No planting in stacked upper blocks - they have no floor for the corals.
                if (itemStack.isIn(VPTags.AQUARIUM_PLANTABLE_ITEMS) && state.get(CORALS) < 4 && !state.get(DOWN)) {
                    blockEntity.insertCoral(itemStack, state.get(CORALS));

                    if (!world.isClient()) {
                        world.setBlockState(pos, state.with(CORALS, state.get(CORALS) + 1), 3);
                        world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    }

                    if (world.isClient) {
                        world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_CORAL_BLOCK_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                    }

                    return ItemActionResult.success(world.isClient);
                } else if (itemStack.getItem() instanceof EntityBucketItem && state.get(FISH) < 1) {
                    // Only set FISH if the slot was actually free; otherwise a desynced slot
                    // would eat the state change without a fish. The tank is handed a copy, so
                    // the stack in hand survives and can be exchanged for an empty bucket below -
                    // the same trade vanilla's fish bucket makes when you release the fish.
                    if (!blockEntity.insertCoral(itemStack.copy(), OceanographerTableBlockEntity.FISH_SLOT)) {
                        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                    }

                    if (!world.isClient()) {
                        world.setBlockState(pos, state.with(FISH, state.get(FISH) + 1), 3);
                        player.setStackInHand(hand, ItemUsage.exchangeStack(itemStack, player, new ItemStack(Items.BUCKET)));
                        world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    }

                    if (world.isClient) player.playSoundToPlayer(SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    return ItemActionResult.success(world.isClient);
                    // Either bucket works: the tank holds its own water, so the empty one handed
                    // back when the fish went in is enough to get it out again.
                } else if ((itemStack.isOf(Items.WATER_BUCKET) || itemStack.isOf(Items.BUCKET)) && state.get(FISH) >= 1) {
                    ItemStack fish = blockEntity.getStack(OceanographerTableBlockEntity.FISH_SLOT);
                    if (!fish.isEmpty()) {
                        if (!world.isClient()) {
                            ItemStack fishBucket = blockEntity.extractFish();
                            // exchangeStack spends one bucket and hands back the fish bucket - into
                            // the hand when that emptied the stack, into the inventory otherwise
                            // (empty buckets stack, water buckets do not).
                            player.setStackInHand(hand, ItemUsage.exchangeStack(itemStack, player, fishBucket));
                            world.setBlockState(pos, state.with(FISH, 0), 3);
                            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                        } else {
                            player.playSoundToPlayer(SoundEvents.ITEM_BUCKET_FILL_FISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        }

                        return ItemActionResult.success(world.isClient);
                    }
                }
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof OceanographerTableBlockEntity) {
                ItemScatterer.spawn(world, pos, (OceanographerTableBlockEntity)blockEntity);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CORALS, FISH, IS_FILLED, FACING, NORTH, EAST, SOUTH, WEST, UP, DOWN, STANDALONE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        boolean standalone = ctx.getPlayer() != null && ctx.getPlayer().isSneaking();

        // A tank may not grow past MAX_TANK_BLOCKS. Beyond that the block entity's flood fill would
        // stop early and the rest of the structure - still physically connected - would never be
        // scanned, leaving the fish's swimmable area ending in the middle of open water. Rather than
        // refuse the placement, the block is kept standalone: it still gets placed, just with its
        // glass intact, which is immediate visual feedback that it did not join.
        if (!standalone
                && mergedTankSize(ctx.getWorld(), ctx.getBlockPos(), OceanographerTableBlockEntity.MAX_TANK_BLOCKS)
                        > OceanographerTableBlockEntity.MAX_TANK_BLOCKS) {
            standalone = true;
        }

        BlockState state = this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(STANDALONE, standalone);
        for (Direction direction : Direction.values()) {
            state = state.with(connectionProperty(direction), this.connectsTo(state, ctx.getWorld().getBlockState(ctx.getBlockPos().offset(direction))));
        }
        return state;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state.with(connectionProperty(direction), this.connectsTo(state, neighborState));
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        // Connecting upward removes the upper block's floor - existing corals there would
        // float in the water, so pop them out.
        if (!world.isClient && state.get(UP)) {
            BlockPos above = pos.up();
            BlockState aboveState = world.getBlockState(above);
            if (aboveState.isOf(this) && aboveState.get(CORALS) > 0
                    && world.getBlockEntity(above) instanceof OceanographerTableBlockEntity blockEntity) {
                blockEntity.ejectCorals();
                world.setBlockState(above, aboveState.with(CORALS, 0), 3);
            }
        }
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        BlockState result = state.with(FACING, rotation.rotate(state.get(FACING)));
        return switch (rotation) {
            case CLOCKWISE_180 -> result.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
            case COUNTERCLOCKWISE_90 -> result.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
            case CLOCKWISE_90 -> result.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
            default -> result;
        };
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        Direction facing = state.get(FACING);
        return switch (mirror) {
            case LEFT_RIGHT -> state.with(FACING, facing.getAxis() == Direction.Axis.Z ? facing.getOpposite() : facing)
                    .with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
            case FRONT_BACK -> state.with(FACING, facing.getAxis() == Direction.Axis.X ? facing.getOpposite() : facing)
                    .with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
            default -> state;
        };
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return (Integer)state.get(FISH) + state.get(CORALS);
    }

    static {
        IS_FILLED = BooleanProperty.of("is_filled");
        FISH = IntProperty.of("fish", 0, 1);
        CORALS = IntProperty.of("corals", 0, 4);
        FACING = HorizontalFacingBlock.FACING;
        NORTH = net.minecraft.state.property.Properties.NORTH;
        EAST = net.minecraft.state.property.Properties.EAST;
        SOUTH = net.minecraft.state.property.Properties.SOUTH;
        WEST = net.minecraft.state.property.Properties.WEST;
        UP = net.minecraft.state.property.Properties.UP;
        DOWN = net.minecraft.state.property.Properties.DOWN;
        STANDALONE = BooleanProperty.of("standalone");
    }
}
