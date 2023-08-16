package com.finallion.villagersplus.blocks;

import com.finallion.villagersplus.blockentities.HorticulturistTableBlockEntity;
import com.finallion.villagersplus.init.ModTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class HorticulturistTableBlock extends WorkstationBlock {
    public static final IntProperty FLOWERS;
    public static final BooleanProperty IS_TALL_FLOWER;

    public HorticulturistTableBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FLOWERS, 0).with(IS_TALL_FLOWER, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HorticulturistTableBlockEntity(pos, state);
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (world.getBlockEntity(pos) instanceof HorticulturistTableBlockEntity blockEntity && state.get(FLOWERS) < 4) {
            if (itemStack.isIn(ModTags.TALL_PLANTABLE_ITEMS) && state.get(FLOWERS) == 0) {
                blockEntity.insertFlower(itemStack, state.get(FLOWERS));

                if (!player.isCreative()) {
                    itemStack.decrement(1);
                }
                if (!world.isClient()) {
                    world.setBlockState(pos, state.with(FLOWERS, 4).with(IS_TALL_FLOWER, true), 3);
                    world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }

                if (world.isClient) {
                    world.playSoundAtBlockCenter(pos, SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                }

                return ActionResult.success(world.isClient);
            } else if (itemStack.isIn(ModTags.SMALL_PLANTABLE_ITEMS)) {
                blockEntity.insertFlower(itemStack, state.get(FLOWERS));

                if (!player.isCreative()) {
                    itemStack.decrement(1);
                }
                if (!world.isClient()) {
                    world.setBlockState(pos, state.with(FLOWERS, state.get(FLOWERS) + 1).with(IS_TALL_FLOWER, false), 3);
                    world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }

                if (world.isClient) {
                    world.playSoundAtBlockCenter(pos, SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                }
                return ActionResult.success(world.isClient);
            }
        }
        return ActionResult.PASS;
    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof HorticulturistTableBlockEntity) {
                ItemScatterer.spawn(world, pos, (HorticulturistTableBlockEntity)blockEntity);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(FLOWERS);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FLOWERS, IS_TALL_FLOWER);
    }

    static {
        IS_TALL_FLOWER = BooleanProperty.of("is_tall_flower");
        FLOWERS = IntProperty.of("flowers", 0, 4);
    }
}
