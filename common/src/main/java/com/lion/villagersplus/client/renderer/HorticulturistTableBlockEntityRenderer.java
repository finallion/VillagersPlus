package com.lion.villagersplus.client.renderer;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.blockentities.HorticulturistTableBlockEntity;
import com.lion.villagersplus.blocks.HorticulturistTableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.*;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SegmentableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;

public class HorticulturistTableBlockEntityRenderer implements BlockEntityRenderer<HorticulturistTableBlockEntity, HorticulturistTableBlockEntityRenderer.State> {

    private static final BlockDisplayContext DISPLAY_CONTEXT = BlockDisplayContext.create();
    private static final int UPPER_HALF = 4;

    public static class State extends BlockEntityRenderState {
        final Block[] plants = new Block[4];
        final BlockModelRenderState[] models = new BlockModelRenderState[5];
        float plantScale = 1.0F;
        boolean valid;
        boolean tallFlower;
        int flowers;

        State() {
            for (int n = 0; n < models.length; n++) {
                models[n] = new BlockModelRenderState();
            }
        }
    }

    public static float[] twoFlowerXOffset = new float[]{
                VillagersPlus.CONFIG.first_flower_in_two_X,
                VillagersPlus.CONFIG.second_flower_in_two_X};
    public static float[] twoFlowerZOffset = new float[]{
                VillagersPlus.CONFIG.first_flower_in_two_Z,
                VillagersPlus.CONFIG.second_flower_in_two_Z};

    public static float[] threeFlowerXOffset = new float[]{
                VillagersPlus.CONFIG.first_flower_in_three_X,
                VillagersPlus.CONFIG.second_flower_in_three_X,
                VillagersPlus.CONFIG.third_flower_in_three_X};
    public static float[] threeFlowerZOffset = new float[]{
                VillagersPlus.CONFIG.first_flower_in_three_Z,
                VillagersPlus.CONFIG.second_flower_in_three_Z,
                VillagersPlus.CONFIG.third_flower_in_three_Z};

    public static float[] fourFlowerXOffset = new float[]{
                VillagersPlus.CONFIG.first_flower_in_four_X,
                VillagersPlus.CONFIG.second_flower_in_four_X,
                VillagersPlus.CONFIG.third_flower_in_four_X,
                VillagersPlus.CONFIG.forth_flower_in_four_X};
    public static float[] fourFlowerZOffset = new float[]{
                VillagersPlus.CONFIG.first_flower_in_four_Z,
                VillagersPlus.CONFIG.second_flower_in_four_Z,
                VillagersPlus.CONFIG.third_flower_in_four_Z,
                VillagersPlus.CONFIG.forth_flower_in_four_Z};

    // A fuller tub steps the plants down so they do not overlap.
    private static final float[] twoFlowerY = new float[]{0.95F, 0.95F};
    private static final float[] threeFlowerY = new float[]{0.95F, 0.90F, 0.90F};
    private static final float[] fourFlowerY = new float[]{0.95F, 0.90F, 0.90F, 0.85F};

    private final BlockModelResolver blockModelResolver;

    public HorticulturistTableBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.blockModelResolver = ctx.blockModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(
            HorticulturistTableBlockEntity blockEntity,
            State state,
            float tickProgress,
            Vec3 cameraPos,
            ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderState.extractBase(blockEntity, state, crumblingOverlay);
        NonNullList<ItemStack> inventory = blockEntity.getInventory();
        for (int n = 0; n < state.plants.length; n++) {
            state.plants[n] = Block.byItem(inventory.get(n).getItem());
        }
        state.plantScale = blockEntity.getPlantScale();

        BlockState tableState = blockEntity.getBlockState();
        state.valid = tableState.getBlock() instanceof HorticulturistTableBlock;
        for (BlockModelRenderState model : state.models) {
            model.clear();
        }

        if (!state.valid) {
            return;
        }

        state.tallFlower = tableState.getValue(HorticulturistTableBlock.IS_TALL_FLOWER);
        state.flowers = tableState.getValue(HorticulturistTableBlock.FLOWERS);

        if (state.tallFlower) {
            Block flower = state.plants[0];

            if (flower instanceof DoublePlantBlock) {
                resolve(state.models[0], tallState(flower, DoubleBlockHalf.LOWER));
                resolve(state.models[UPPER_HALF], tallState(flower, DoubleBlockHalf.UPPER));
            } else {
                resolve(state.models[0], displayState(flower));
            }

            return;
        }

        for (int n = 0; n < Math.min(state.flowers, state.plants.length); n++) {
            resolve(state.models[n], displayState(state.plants[n]));
        }
    }

    private void resolve(BlockModelRenderState model, BlockState blockState) {
        blockModelResolver.update(model, blockState, DISPLAY_CONTEXT);
    }

    @Override
    public void submit(State state, PoseStack matrixStack, SubmitNodeCollector queue, CameraRenderState camera) {
        if (!state.valid) {
            return;
        }

        int light = state.lightCoords;
        float scale = state.plantScale;

        if (state.tallFlower) {
            matrixStack.pushPose();
            Block flower = state.plants[0];

            if (flower instanceof DoublePlantBlock) {
                matrixStack.translate(0.0D, 0.95D, 0.0D);
                applyPlantScale(matrixStack, scale);
                state.models[0].submit(matrixStack, queue, light, OverlayTexture.NO_OVERLAY, 0);
                matrixStack.translate(0.0D, 1.0D, 0.0D);
                state.models[UPPER_HALF].submit(matrixStack, queue, light, OverlayTexture.NO_OVERLAY, 0);
            } else {
                if (flower.defaultBlockState().is(Blocks.CACTUS)) {
                    matrixStack.scale(0.75F, 0.75F, 0.75F);
                    matrixStack.translate(0.15D, 0.15D, 0.15D);
                }

                matrixStack.translate(0.0D, 0.95D, 0.0D);
                applyPlantScale(matrixStack, scale);
                state.models[0].submit(matrixStack, queue, light, OverlayTexture.NO_OVERLAY, 0);
            }

            matrixStack.popPose();
            return;
        }

        switch (state.flowers) {
            case 1 -> renderFlowerAt(state, 0, 0.0F, 0.95F, 0.0F, matrixStack, queue);
            case 2 -> {
                for (int n = 0; n < 2; n++) {
                    renderFlowerAt(state, n, twoFlowerXOffset[n], twoFlowerY[n], twoFlowerZOffset[n], matrixStack, queue);
                }
            }
            case 3 -> {
                for (int n = 0; n < 3; n++) {
                    renderFlowerAt(state, n, threeFlowerXOffset[n], threeFlowerY[n], threeFlowerZOffset[n], matrixStack, queue);
                }
            }
            case 4 -> {
                for (int n = 0; n < 4; n++) {
                    renderFlowerAt(state, n, fourFlowerXOffset[n], fourFlowerY[n], fourFlowerZOffset[n], matrixStack, queue);
                }
            }
        }
    }

    private static void applyPlantScale(PoseStack matrixStack, float scale) {
        if (scale != 1.0F) {
            matrixStack.translate(0.5F * (1.0F - scale), 0.0F, 0.5F * (1.0F - scale));
            matrixStack.scale(scale, scale, scale);
        }
    }

    private void renderFlowerAt(
            State state,
            int slot,
            float x,
            float y,
            float z,
            PoseStack matrixStack,
            SubmitNodeCollector queue
    ) {
        matrixStack.pushPose();
        matrixStack.translate(x, y, z);
        applyPlantScale(matrixStack, state.plantScale);
        state.models[slot].submit(matrixStack, queue, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        matrixStack.popPose();
    }

    private static BlockState displayState(Block flower) {
        if (flower instanceof SegmentableBlock segmented) {
            return flower.defaultBlockState().setValue(segmented.getSegmentAmountProperty(), SegmentableBlock.MAX_SEGMENT);
        }

        return flower.defaultBlockState();
    }

    private static BlockState tallState(Block flower, DoubleBlockHalf half) {
        return flower.defaultBlockState().setValue(DoublePlantBlock.HALF, half);
    }
}
