package com.lion.villagersplus.blocks;

import com.mojang.serialization.MapCodec;
import com.lion.villagersplus.blockentities.HorticulturistTableBlockEntity;
import com.lion.villagersplus.init.VPTags;
import net.minecraft.world.level.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class HorticulturistTableBlock extends WorkstationBlock {
    public static final IntegerProperty FLOWERS;
    public static final BooleanProperty IS_TALL_FLOWER;
    public static final IntegerProperty LIGHT;

    /// Light of one torchflower. Four fill the tub: 4, 8, 12, 15.
    private static final int TORCHFLOWER_LIGHT = 4;

    private static final int MAX_LIGHT = 15;

    public static final MapCodec<HorticulturistTableBlock> CODEC = simpleCodec(HorticulturistTableBlock::new);

    @Override
    protected MapCodec<? extends HorticulturistTableBlock> codec() {
        return CODEC;
    }

    public HorticulturistTableBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FLOWERS, 0).setValue(IS_TALL_FLOWER, false).setValue(LIGHT, 0));
    }

    /// Runs the ambient effect of every planted block, anchored where the plants are drawn.
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (state.getValue(FLOWERS) == 0 || !(world.getBlockEntity(pos) instanceof HorticulturistTableBlockEntity blockEntity)) {
            return;
        }

        BlockPos plantPos = pos.above();
        for (ItemStack stack : blockEntity.getInventory()) {
            if (stack.isEmpty()) {
                continue;
            }
            Block plant = Block.byItem(stack.getItem());
            if (plant != Blocks.AIR) {
                plant.animateTick(plant.defaultBlockState(), world, plantPos, random);
            }
        }
    }

    private static int lightFor(HorticulturistTableBlockEntity blockEntity) {
        int light = 0;
        for (ItemStack stack : blockEntity.getInventory()) {
            if (!stack.isEmpty()) {
                light += plantLight(Block.byItem(stack.getItem()));
            }
        }
        return Math.min(light, MAX_LIGHT);
    }

    /// Torchflower adds light only inside the tub. Other plants add the light they emit.
    private static int plantLight(Block plant) {
        if (plant == Blocks.TORCHFLOWER) {
            return TORCHFLOWER_LIGHT;
        }
        return plant.defaultBlockState().getLightEmission();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HorticulturistTableBlockEntity(pos, state);
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        if (!(world.getBlockEntity(pos) instanceof HorticulturistTableBlockEntity blockEntity)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        boolean holdingTall = itemStack.is(VPTags.TALL_PLANTABLE_ITEMS);
        boolean holdingSmall = itemStack.is(VPTags.SMALL_PLANTABLE_ITEMS);
        boolean holdingBoneMeal = itemStack.is(Items.BONE_MEAL);
        boolean holdingShears = itemStack.is(Items.SHEARS);

        // Bone meal and shears act before the removal fall-through.
        if ((holdingBoneMeal || holdingShears) && state.getValue(FLOWERS) > 0) {
            if (!world.isClientSide()) {
                float max = state.getValue(IS_TALL_FLOWER)
                        ? HorticulturistTableBlockEntity.MAX_TALL_PLANT_SCALE
                        : HorticulturistTableBlockEntity.MAX_PLANT_SCALE;
                float delta = holdingBoneMeal ? HorticulturistTableBlockEntity.PLANT_SCALE_STEP
                                              : -HorticulturistTableBlockEntity.PLANT_SCALE_STEP;
                if (blockEntity.adjustPlantScale(delta, max)) {
                    if (holdingBoneMeal) {
                        if (!player.getAbilities().instabuild) {
                            itemStack.shrink(1);
                        }
                    } else {
                        itemStack.hurtAndBreak(1, player, hand.asEquipmentSlot());
                    }
                    world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
            } else {
                world.playSound(null, pos, holdingBoneMeal ? SoundEvents.BONE_MEAL_USE : SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        if (state.getValue(FLOWERS) < 4) {
            if (holdingTall && state.getValue(FLOWERS) == 0) {
                blockEntity.insertFlower(itemStack, state.getValue(FLOWERS));

                if (!world.isClientSide()) {
                    world.setBlock(pos, state.setValue(FLOWERS, 4).setValue(IS_TALL_FLOWER, true).setValue(LIGHT, lightFor(blockEntity)), 3);
                    world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }

                if (world.isClientSide()) {
                    world.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
                }

                return InteractionResult.SUCCESS;
            } else if (holdingSmall) {
                blockEntity.insertFlower(itemStack, state.getValue(FLOWERS));

                if (!world.isClientSide()) {
                    world.setBlock(pos, state.setValue(FLOWERS, state.getValue(FLOWERS) + 1).setValue(IS_TALL_FLOWER, false).setValue(LIGHT, lightFor(blockEntity)), 3);
                    world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }

                if (world.isClientSide()) {
                    world.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return InteractionResult.SUCCESS;
            }
        }

        if (!holdingTall && !holdingSmall && state.getValue(FLOWERS) > 0) {
            boolean tall = state.getValue(IS_TALL_FLOWER);
            int topSlot = tall ? 0 : state.getValue(FLOWERS) - 1;

            if (!world.isClientSide()) {
                ItemStack removed = blockEntity.removeFlower(topSlot);
                if (removed.isEmpty()) {
                    return InteractionResult.TRY_WITH_EMPTY_HAND;
                }
                player.getInventory().placeItemBackInInventory(removed);
                int remaining = tall ? 0 : state.getValue(FLOWERS) - 1;
                world.setBlock(pos, state.setValue(FLOWERS, remaining).setValue(IS_TALL_FLOWER, false).setValue(LIGHT, lightFor(blockEntity)), 3);
                world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }

            if (world.isClientSide()) {
                world.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof HorticulturistTableBlockEntity) {
            Containers.dropContents(world, pos, (HorticulturistTableBlockEntity)blockEntity);
        }

        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return state.getValue(FLOWERS);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FLOWERS, IS_TALL_FLOWER, LIGHT);
    }

    static {
        IS_TALL_FLOWER = BooleanProperty.create("is_tall_flower");
        FLOWERS = IntegerProperty.create("flowers", 0, 4);
        LIGHT = IntegerProperty.create("light", 0, 15);
    }
}
