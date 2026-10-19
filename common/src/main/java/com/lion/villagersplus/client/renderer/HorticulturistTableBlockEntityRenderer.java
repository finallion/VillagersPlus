package com.lion.villagersplus.client.renderer;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.blockentities.HorticulturistTableBlockEntity;
import com.lion.villagersplus.blocks.HorticulturistTableBlock;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;

public class HorticulturistTableBlockEntityRenderer implements BlockEntityRenderer<HorticulturistTableBlockEntity, HorticulturistTableBlockEntityRenderer.State> {

    public static class State extends BlockEntityRenderState {
        final Block[] plants = new Block[4];
        float plantScale = 1.0F;
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

    // Index is the plant slot. A fuller tub steps down, so the plants do not overlap.
    private static final float[] twoFlowerY = new float[]{0.95F, 0.95F};
    private static final float[] threeFlowerY = new float[]{0.95F, 0.90F, 0.90F};
    private static final float[] fourFlowerY = new float[]{0.95F, 0.90F, 0.90F, 0.85F};

    public HorticulturistTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void updateRenderState(
            HorticulturistTableBlockEntity blockEntity,
            State state,
            float tickProgress,
            Vec3d cameraPos,
            ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay
    ) {
        BlockEntityRenderState.updateBlockEntityRenderState(blockEntity, state, crumblingOverlay);
        DefaultedList<ItemStack> inventory = blockEntity.getInventory();
        for (int n = 0; n < state.plants.length; n++) {
            state.plants[n] = Block.getBlockFromItem(inventory.get(n).getItem());
        }
        state.plantScale = blockEntity.getPlantScale();
    }

    @Override
    public void render(
            State state,
            MatrixStack matrixStack,
            OrderedRenderCommandQueue queue,
            CameraRenderState camera
    ) {
        if (!(state.blockState.getBlock() instanceof HorticulturistTableBlock)) {
            return;
        }

        int light = state.lightmapCoordinates;
        float scale = state.plantScale;

        if (state.blockState.get(HorticulturistTableBlock.IS_TALL_FLOWER)) {
            matrixStack.push();
            Block flower = state.plants[0];

            if (flower instanceof TallPlantBlock) {
                matrixStack.translate(0.0D, 0.95D, 0.0D);
                // Scale both halves in one space, so they stay attached.
                applyPlantScale(matrixStack, scale);
                renderTallFlower(flower, matrixStack, queue, true, light);
                matrixStack.translate(0.0D, 1.0D, 0.0D);
                renderTallFlower(flower, matrixStack, queue, false, light);
            } else {
                if (flower.getDefaultState().isOf(Blocks.CACTUS)) {
                    matrixStack.scale(0.75F, 0.75F, 0.75F);
                    matrixStack.translate(0.15D, 0.15D, 0.15D);
                }

                matrixStack.translate(0.0D, 0.95D, 0.0D);
                applyPlantScale(matrixStack, scale);
                renderFlower(flower, matrixStack, queue, light);
            }

            matrixStack.pop();
            return;
        }

        switch (state.blockState.get(HorticulturistTableBlock.FLOWERS)) {
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

    /// Scales around the base centre. Block models render from the corner.
    private static void applyPlantScale(MatrixStack matrixStack, float scale) {
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
            MatrixStack matrixStack,
            OrderedRenderCommandQueue queue
    ) {
        matrixStack.push();
        matrixStack.translate(x, y, z);
        applyPlantScale(matrixStack, state.plantScale);
        renderFlower(state.plants[slot], matrixStack, queue, state.lightmapCoordinates);
        matrixStack.pop();
    }

    private void renderFlower(Block flower, MatrixStack matrixStack, OrderedRenderCommandQueue queue, int light) {
        queue.submitBlock(matrixStack, displayState(flower), light, OverlayTexture.DEFAULT_UV, 0);
    }

    /// Segmented plants draw one quadrant per segment, so fill every segment.
    private static BlockState displayState(Block flower) {
        if (flower instanceof Segmented segmented) {
            return flower.getDefaultState().with(segmented.getAmountProperty(), Segmented.MAX_SEGMENTS);
        }

        return flower.getDefaultState();
    }

    private void renderTallFlower(
            Block flower,
            MatrixStack matrixStack,
            OrderedRenderCommandQueue queue,
            boolean lower,
            int light
    ) {
        DoubleBlockHalf half = lower ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER;
        queue.submitBlock(matrixStack, flower.getDefaultState().with(TallPlantBlock.HALF, half), light, OverlayTexture.DEFAULT_UV, 0);
    }
}
