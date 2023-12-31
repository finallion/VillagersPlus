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

    public static float[] twoFlowerXOffset = new float[]{
                VillagersPlus.CONFIG.first_flower_in_two_X_offset,
                VillagersPlus.CONFIG.second_flower_in_two_X_offset};
    public static float[] twoFlowerZOffset = new float[]{
                VillagersPlus.CONFIG.first_flower_in_two_Z_offset,
                VillagersPlus.CONFIG.second_flower_in_two_Z_offset};

    public static float[] threeFlowerXOffset = new float[]{
                VillagersPlus.CONFIG.first_flower_in_three_X_offset,
                VillagersPlus.CONFIG.second_flower_in_three_X_offset,
                VillagersPlus.CONFIG.third_flower_in_three_X_offset};
    public static float[] threeFlowerZOffset = new float[]{
                VillagersPlus.CONFIG.first_flower_in_three_Z_offset,
                VillagersPlus.CONFIG.second_flower_in_three_Z_offset,
                VillagersPlus.CONFIG.third_flower_in_three_Z_offset};

    public static float[] fourFlowerXOffset = new float[]{
                VillagersPlus.CONFIG.first_flower_in_four_X_offset,
                VillagersPlus.CONFIG.second_flower_in_four_X_offset,
                VillagersPlus.CONFIG.third_flower_in_four_X_offset,
                VillagersPlus.CONFIG.forth_flower_in_four_X_offset};
    public static float[] fourFlowerZOffset = new float[]{
                VillagersPlus.CONFIG.first_flower_in_four_Z_offset,
                VillagersPlus.CONFIG.second_flower_in_four_Z_offset,
                VillagersPlus.CONFIG.third_flower_in_four_Z_offset,
                VillagersPlus.CONFIG.forth_flower_in_four_Z_offset};

    private final BlockRenderManager manager;

    public HorticulturistTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.manager = ctx.getRenderManager();
    }

    public void render(HorticulturistTableBlockEntity blockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        BlockState blockState = blockEntity.getCachedState();
        BlockPos pos = blockEntity.getPos();
        DefaultedList<ItemStack> defaultedList = blockEntity.getInventory();
        World world = blockEntity.getWorld();

        if (blockState.getBlock() instanceof HorticulturistTableBlock) {
            matrixStack.push();
            if (blockState.get(HorticulturistTableBlock.IS_TALL_FLOWER)) {
                Block flower = Block.getBlockFromItem(defaultedList.get(0).getItem());
                if (flower instanceof TallPlantBlock) {
                    Vec3d offset = flower.getDefaultState().getModelOffset(world, pos);
                    matrixStack.translate(-offset.x, -offset.y + 0.95D, -offset.z);
                    renderTallFlower(flower.getDefaultState().getBlock(), world, pos, matrixStack, vertexConsumerProvider, true, j);
                    matrixStack.translate(-offset.x, -offset.y + 1.0D, -offset.z);
                    renderTallFlower(flower.getDefaultState().getBlock(), world, pos, matrixStack, vertexConsumerProvider, false, j);
                } else {
                    Block flowerOne = Block.getBlockFromItem(defaultedList.get(0).getItem());
                    if (flowerOne.getDefaultState().isOf(Blocks.CACTUS)) {
                        matrixStack.scale(0.75F, 0.75F, 0.75F);
                        matrixStack.translate(0.15D, 0.15D, 0.15D);
                    }

                    Vec3d offset = flowerOne.getDefaultState().getModelOffset(world, pos);
                    matrixStack.translate(-offset.x, -offset.y + 0.95D, -offset.z);
                    renderFlower(Block.getBlockFromItem(defaultedList.get(0).getItem()), world, pos, matrixStack, vertexConsumerProvider, j);
                }
            } else {

                switch (blockState.get(HorticulturistTableBlock.FLOWERS)) {
                    case 1 -> {
                        Block flowerOne = Block.getBlockFromItem(defaultedList.get(0).getItem());
                        Vec3d offset = flowerOne.getDefaultState().getModelOffset(world, pos);
                        matrixStack.translate(-offset.x, -offset.y + 0.95D, -offset.z);
                        renderFlower(flowerOne, world, pos, matrixStack, vertexConsumerProvider, j);
                    }
                    case 2 -> {
                        Block flowerOne = Block.getBlockFromItem(defaultedList.get(0).getItem());
                        Block flowerTwo = Block.getBlockFromItem(defaultedList.get(1).getItem());
                        Vec3d offset = flowerOne.getDefaultState().getModelOffset(world, pos);
                        Vec3d offset2 = flowerTwo.getDefaultState().getModelOffset(world, pos);
                        matrixStack.translate(-offset.x + twoFlowerXOffset[0], -offset.y + 0.95D, -offset.z + twoFlowerZOffset[0]);
                        renderFlower(flowerOne, world, pos, matrixStack, vertexConsumerProvider, j);
                        matrixStack.translate(-offset2.x + twoFlowerXOffset[1], -offset2.y, -offset2.z + twoFlowerZOffset[1]);
                        renderFlower(flowerTwo, world, pos, matrixStack, vertexConsumerProvider, j);
                    }
                    case 3 -> {
                        Block flowerOne = Block.getBlockFromItem(defaultedList.get(0).getItem());
                        Block flowerTwo = Block.getBlockFromItem(defaultedList.get(1).getItem());
                        Block flowerThree = Block.getBlockFromItem(defaultedList.get(2).getItem());
                        Vec3d offset = flowerOne.getDefaultState().getModelOffset(world, pos);
                        Vec3d offset2 = flowerTwo.getDefaultState().getModelOffset(world, pos);
                        Vec3d offset3 = flowerThree.getDefaultState().getModelOffset(world, pos);
                        matrixStack.translate(-offset.x + threeFlowerXOffset[0], -offset.y + 0.95D, -offset.z + threeFlowerZOffset[0]);
                        renderFlower(flowerOne, world, pos, matrixStack, vertexConsumerProvider, j);
                        matrixStack.translate(-offset2.x + threeFlowerXOffset[1], -offset2.y - 0.05D, -offset2.z + threeFlowerZOffset[1]);
                        renderFlower(flowerTwo, world, pos, matrixStack, vertexConsumerProvider, j);
                        matrixStack.translate(-offset3.x + threeFlowerXOffset[2], -offset3.y + 0, -offset3.z + threeFlowerZOffset[2]);
                        renderFlower(flowerThree, world, pos, matrixStack, vertexConsumerProvider, j);
                    }
                    case 4 -> {
                        Block flowerOne = Block.getBlockFromItem(defaultedList.get(0).getItem());
                        Block flowerTwo = Block.getBlockFromItem(defaultedList.get(1).getItem());
                        Block flowerThree = Block.getBlockFromItem(defaultedList.get(2).getItem());
                        Block flowerFour = Block.getBlockFromItem(defaultedList.get(3).getItem());
                        Vec3d offset = flowerOne.getDefaultState().getModelOffset(world, pos);
                        Vec3d offset2 = flowerTwo.getDefaultState().getModelOffset(world, pos);
                        Vec3d offset3 = flowerThree.getDefaultState().getModelOffset(world, pos);
                        Vec3d offset4 = flowerFour.getDefaultState().getModelOffset(world, pos);
                        matrixStack.translate(-offset.x + fourFlowerXOffset[0], -offset.y + 0.95D, -offset.z + fourFlowerZOffset[0]);
                        renderFlower(flowerOne, world, pos, matrixStack, vertexConsumerProvider, j);
                        matrixStack.translate(-offset2.x + fourFlowerXOffset[1], -offset2.y - 0.05D, -offset2.z + fourFlowerZOffset[1]);
                        renderFlower(Block.getBlockFromItem(defaultedList.get(1).getItem()), world, pos, matrixStack, vertexConsumerProvider, j);
                        matrixStack.translate(-offset3.x + fourFlowerXOffset[2], -offset3.y + 0, -offset3.z + fourFlowerZOffset[2]);
                        renderFlower(Block.getBlockFromItem(defaultedList.get(2).getItem()), world, pos, matrixStack, vertexConsumerProvider, j);
                        matrixStack.translate(-offset4.x + fourFlowerXOffset[3], -offset4.y - 0.05D, -offset4.z + fourFlowerZOffset[3]);
                        renderFlower(Block.getBlockFromItem(defaultedList.get(3).getItem()), world, pos, matrixStack, vertexConsumerProvider, j);
                    }
                }
            }
            matrixStack.pop();
        }
    }

    private void renderFlower(Block flower, World world, BlockPos pos, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int overlay) {
        this.manager.getModelRenderer().render(world, this.manager.getModel(flower.getDefaultState()), flower.getDefaultState(), pos, matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getCutoutMipped()), false, Random.create(), flower.getDefaultState().getRenderingSeed(pos), overlay);
    }


    private void renderTallFlower(Block flower, World world, BlockPos pos, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, boolean lower, int overlay) {
        if (lower) {
            this.manager.getModelRenderer().render(world, this.manager.getModel(flower.getDefaultState().with(TallPlantBlock.HALF, DoubleBlockHalf.LOWER)), flower.getDefaultState(), pos, matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getCutoutMipped()), false, Random.create(), flower.getDefaultState().getRenderingSeed(pos), overlay);
        } else {
            this.manager.getModelRenderer().render(world, this.manager.getModel(flower.getDefaultState().with(TallPlantBlock.HALF, DoubleBlockHalf.UPPER)), flower.getDefaultState(), pos, matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getCutoutMipped()), false, Random.create(), flower.getDefaultState().getRenderingSeed(pos), overlay);
        }
    }
}

