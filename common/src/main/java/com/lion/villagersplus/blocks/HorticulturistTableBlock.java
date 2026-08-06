package com.lion.villagersplus.blocks;

import com.mojang.serialization.MapCodec;
import com.lion.villagersplus.blockentities.HorticulturistTableBlockEntity;
import com.lion.villagersplus.init.VPTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class HorticulturistTableBlock extends WorkstationBlock {
    public static final IntProperty FLOWERS;
    public static final BooleanProperty IS_TALL_FLOWER;
    /** Light the tub gives off, driven by what is planted in it. Read by the block's luminance. */
    public static final IntProperty LIGHT;

    /**
     * What one torchflower is worth in the tub. It gives off no light where it grows, and the tub
     * holds four, so this is a quarter of the way to full brightness: 4, 8, 12, 15.
     */
    private static final int TORCHFLOWER_LIGHT = 4;

    /** Brightest a tub can get, whatever is packed into it. */
    private static final int MAX_LIGHT = 15;

    /** BlockWithEntity requires a codec as of 1.20.5; this block has no state beyond its settings. */
    public static final MapCodec<HorticulturistTableBlock> CODEC = createCodec(HorticulturistTableBlock::new);

    @Override
    protected MapCodec<? extends HorticulturistTableBlock> getCodec() {
        return CODEC;
    }

    public HorticulturistTableBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FLOWERS, 0).with(IS_TALL_FLOWER, false).with(LIGHT, 0));
    }

    /**
     * Runs every planted block's own ambient effect - a firefly bush's fireflies, an eyeblossom's
     * particles, whatever a future plant brings - so the tub does not have to know about any of
     * them. Anchored one block up, where the plants are actually drawn.
     */
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        if (state.get(FLOWERS) == 0 || !(world.getBlockEntity(pos) instanceof HorticulturistTableBlockEntity blockEntity)) {
            return;
        }

        BlockPos plantPos = pos.up();
        for (ItemStack stack : blockEntity.getInventory()) {
            if (stack.isEmpty()) {
                continue;
            }
            Block plant = Block.getBlockFromItem(stack.getItem());
            if (plant != Blocks.AIR) {
                plant.randomDisplayTick(plant.getDefaultState(), world, plantPos, random);
            }
        }
    }

    /** Every plant adds its own light, so a tub packed with them is brighter than one holding a single one. */
    private static int lightFor(HorticulturistTableBlockEntity blockEntity) {
        int light = 0;
        for (ItemStack stack : blockEntity.getInventory()) {
            if (!stack.isEmpty()) {
                light += plantLight(Block.getBlockFromItem(stack.getItem()));
            }
        }
        return Math.min(light, MAX_LIGHT);
    }

    /**
     * What one plant contributes. A torchflower lights the tub even though it glows nowhere else;
     * every other plant contributes whatever light it already emits as a block, so a glowing plant
     * needs no entry here.
     */
    private static int plantLight(Block plant) {
        if (plant == Blocks.TORCHFLOWER) {
            return TORCHFLOWER_LIGHT;
        }
        return plant.getDefaultState().getLuminance();
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HorticulturistTableBlockEntity(pos, state);
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack itemStack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (!(world.getBlockEntity(pos) instanceof HorticulturistTableBlockEntity blockEntity)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }

        boolean holdingTall = itemStack.isIn(VPTags.TALL_PLANTABLE_ITEMS);
        boolean holdingSmall = itemStack.isIn(VPTags.SMALL_PLANTABLE_ITEMS);
        boolean holdingBoneMeal = itemStack.isOf(Items.BONE_MEAL);
        boolean holdingShears = itemStack.isOf(Items.SHEARS);

        // Bone meal grows the tub's plants, shears trim them back down. Handled (and consumed as
        // a use) BEFORE the removal fall-through below so neither item ever pulls a plant out.
        if ((holdingBoneMeal || holdingShears) && state.get(FLOWERS) > 0) {
            if (!world.isClient()) {
                float max = state.get(IS_TALL_FLOWER)
                        ? HorticulturistTableBlockEntity.MAX_TALL_PLANT_SCALE
                        : HorticulturistTableBlockEntity.MAX_PLANT_SCALE;
                float delta = holdingBoneMeal ? HorticulturistTableBlockEntity.PLANT_SCALE_STEP
                                              : -HorticulturistTableBlockEntity.PLANT_SCALE_STEP;
                if (blockEntity.adjustPlantScale(delta, max)) {
                    if (holdingBoneMeal) {
                        if (!player.getAbilities().creativeMode) {
                            itemStack.decrement(1);
                        }
                    } else {
                        itemStack.damage(1, player, LivingEntity.getSlotForHand(hand));
                    }
                    world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
            } else {
                world.playSound(null, pos, holdingBoneMeal ? SoundEvents.ITEM_BONE_MEAL_USE : SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return ActionResult.SUCCESS;
        }

        if (state.get(FLOWERS) < 4) {
            if (holdingTall && state.get(FLOWERS) == 0) {
                blockEntity.insertFlower(itemStack, state.get(FLOWERS));

                if (!world.isClient()) {
                    world.setBlockState(pos, state.with(FLOWERS, 4).with(IS_TALL_FLOWER, true).with(LIGHT, lightFor(blockEntity)), 3);
                    world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }

                if (world.isClient) {
                    world.playSound(null, pos, SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }

                return ActionResult.SUCCESS;
            } else if (holdingSmall) {
                blockEntity.insertFlower(itemStack, state.get(FLOWERS));

                if (!world.isClient()) {
                    world.setBlockState(pos, state.with(FLOWERS, state.get(FLOWERS) + 1).with(IS_TALL_FLOWER, false).with(LIGHT, lightFor(blockEntity)), 3);
                    world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }

                if (world.isClient) {
                    world.playSound(null, pos, SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                return ActionResult.SUCCESS;
            }
        }

        // Right-clicking without a plantable in hand returns the top-most plant, one per use.
        if (!holdingTall && !holdingSmall && state.get(FLOWERS) > 0) {
            boolean tall = state.get(IS_TALL_FLOWER);
            int topSlot = tall ? 0 : state.get(FLOWERS) - 1;

            if (!world.isClient()) {
                ItemStack removed = blockEntity.removeFlower(topSlot);
                if (removed.isEmpty()) {
                    return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
                }
                player.getInventory().offerOrDrop(removed);
                int remaining = tall ? 0 : state.get(FLOWERS) - 1;
                world.setBlockState(pos, state.with(FLOWERS, remaining).with(IS_TALL_FLOWER, false).with(LIGHT, lightFor(blockEntity)), 3);
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }

            if (world.isClient) {
                world.playSound(null, pos, SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        // Since 1.21.6 this only fires when the block really changed and only on the server, so
        // the old isOf(newState) guard is gone along with the newState parameter.
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof HorticulturistTableBlockEntity) {
            ItemScatterer.spawn(world, pos, (HorticulturistTableBlockEntity)blockEntity);
        }

        super.onStateReplaced(state, world, pos, moved);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(FLOWERS);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FLOWERS, IS_TALL_FLOWER, LIGHT);
    }

    static {
        IS_TALL_FLOWER = BooleanProperty.of("is_tall_flower");
        FLOWERS = IntProperty.of("flowers", 0, 4);
        LIGHT = IntProperty.of("light", 0, 15);
    }
}
