package com.lion.villagersplus.client.renderer;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.blockentities.HorticulturistTableBlockEntity;
import com.lion.villagersplus.blocks.HorticulturistTableBlock;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class HorticulturistTableBlockEntityRenderer implements BlockEntityRenderer<HorticulturistTableBlockEntity> {

    // Absolute X/Z positions per plant (offset from the block origin). Applied independently per
    // flower - each plant is rendered inside its own push()/pop() so the per-plant random model
    // offset does NOT accumulate across plants (which previously drifted 4 flowers up to ~1 block).
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

    // Slight per-plant height so a fuller tub steps down gently. Index = plant count - 1.
    private static final float[] twoFlowerY = new float[]{0.95F, 0.95F};
    private static final float[] threeFlowerY = new float[]{0.95F, 0.90F, 0.90F};
    private static final float[] fourFlowerY = new float[]{0.95F, 0.90F, 0.90F, 0.85F};

    private final BlockRenderManager manager;

    public HorticulturistTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.manager = ctx.getRenderManager();
    }

    // BlockEntityRenderer.render gained a camera position in 1.21.6. Nothing here needs it, but the
    // parameter has to be present or the class no longer implements the interface.
    @Override
    public void render(HorticulturistTableBlockEntity blockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, net.minecraft.util.math.Vec3d cameraPos) {
        BlockState blockState = blockEntity.getCachedState();
        BlockPos pos = blockEntity.getPos();
        DefaultedList<ItemStack> defaultedList = blockEntity.getInventory();
        World world = blockEntity.getWorld();

        if (!(blockState.getBlock() instanceof HorticulturistTableBlock)) {
            return;
        }

        // Bone-meal / shears size; applied per plant around its base so it stays on the soil.
        float scale = blockEntity.getPlantScale();

        if (blockState.get(HorticulturistTableBlock.IS_TALL_FLOWER)) {
            matrixStack.push();
            Block flower = Block.getBlockFromItem(defaultedList.get(0).getItem());
            if (flower instanceof TallPlantBlock) {
                Vec3d offset = flower.getDefaultState().getModelOffset(pos);
                matrixStack.translate(-offset.x, -offset.y + 0.95D, -offset.z);
                // Scale once before BOTH halves so the upper half's translate happens in
                // scaled space and the two halves stay attached.
                applyPlantScale(matrixStack, scale);
                renderTallFlower(flower.getDefaultState().getBlock(), world, pos, matrixStack, vertexConsumerProvider, true, i, j);
                matrixStack.translate(-offset.x, -offset.y + 1.0D, -offset.z);
                renderTallFlower(flower.getDefaultState().getBlock(), world, pos, matrixStack, vertexConsumerProvider, false, i, j);
            } else {
                Block flowerOne = Block.getBlockFromItem(defaultedList.get(0).getItem());
                if (flowerOne.getDefaultState().isOf(Blocks.CACTUS)) {
                    matrixStack.scale(0.75F, 0.75F, 0.75F);
                    matrixStack.translate(0.15D, 0.15D, 0.15D);
                }

                Vec3d offset = flowerOne.getDefaultState().getModelOffset(pos);
                matrixStack.translate(-offset.x, -offset.y + 0.95D, -offset.z);
                applyPlantScale(matrixStack, scale);
                renderFlower(flowerOne, world, pos, matrixStack, vertexConsumerProvider, i, j);
            }
            matrixStack.pop();
            return;
        }

        switch (blockState.get(HorticulturistTableBlock.FLOWERS)) {
            case 1 -> renderFlowerAt(defaultedList, 0, 0.0F, 0.95F, 0.0F, scale, world, pos, matrixStack, vertexConsumerProvider, i, j);
            case 2 -> {
                for (int n = 0; n < 2; n++) {
                    renderFlowerAt(defaultedList, n, twoFlowerXOffset[n], twoFlowerY[n], twoFlowerZOffset[n], scale, world, pos, matrixStack, vertexConsumerProvider, i, j);
                }
            }
            case 3 -> {
                for (int n = 0; n < 3; n++) {
                    renderFlowerAt(defaultedList, n, threeFlowerXOffset[n], threeFlowerY[n], threeFlowerZOffset[n], scale, world, pos, matrixStack, vertexConsumerProvider, i, j);
                }
            }
            case 4 -> {
                for (int n = 0; n < 4; n++) {
                    renderFlowerAt(defaultedList, n, fourFlowerXOffset[n], fourFlowerY[n], fourFlowerZOffset[n], scale, world, pos, matrixStack, vertexConsumerProvider, i, j);
                }
            }
        }
    }

    /** Scales around the centre of the plant's base (block models render 0..1 from the corner). */
    private static void applyPlantScale(MatrixStack matrixStack, float scale) {
        if (scale != 1.0F) {
            matrixStack.translate(0.5F * (1.0F - scale), 0.0F, 0.5F * (1.0F - scale));
            matrixStack.scale(scale, scale, scale);
        }
    }

    /** Renders one plant at an absolute X/Y/Z position, isolated in its own matrix so offsets never accumulate. */
    private void renderFlowerAt(DefaultedList<ItemStack> list, int slot, float x, float y, float z, float scale, World world, BlockPos pos, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
        Block flower = Block.getBlockFromItem(list.get(slot).getItem());
        Vec3d offset = flower.getDefaultState().getModelOffset(pos);
        matrixStack.push();
        matrixStack.translate(-offset.x + x, -offset.y + y, -offset.z + z);
        applyPlantScale(matrixStack, scale);
        renderFlower(flower, world, pos, matrixStack, vertexConsumerProvider, light, overlay);
        matrixStack.pop();
    }

    private void renderFlower(Block flower, World world, BlockPos pos, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
        // The model-renderer overload with an explicit model and seed is gone; renderBlockAsEntity
        // picks the right render layer for the state itself, which is what a block entity wants.
        this.manager.renderBlockAsEntity(flower.getDefaultState(), matrixStack, vertexConsumerProvider, light, overlay);
    }


    private void renderTallFlower(Block flower, World world, BlockPos pos, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, boolean lower, int light, int overlay) {
        if (lower) {
            this.manager.renderBlockAsEntity(flower.getDefaultState().with(TallPlantBlock.HALF, DoubleBlockHalf.LOWER), matrixStack, vertexConsumerProvider, light, overlay);
        } else {
            this.manager.renderBlockAsEntity(flower.getDefaultState().with(TallPlantBlock.HALF, DoubleBlockHalf.UPPER), matrixStack, vertexConsumerProvider, light, overlay);
        }
    }
}
