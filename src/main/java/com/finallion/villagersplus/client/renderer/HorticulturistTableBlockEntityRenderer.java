package com.finallion.villagersplus.client.renderer;

import com.finallion.villagersplus.blockentities.HorticulturistTableBlockEntity;
import com.finallion.villagersplus.blocks.HorticulturistTableBlock;
import com.finallion.villagersplus.init.ModBlocks;
import com.finallion.villagersplus.init.ModTags;
import net.minecraft.block.*;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class HorticulturistTableBlockEntityRenderer implements BlockEntityRenderer<HorticulturistTableBlockEntity> {
    private final BlockRenderManager manager;

    public HorticulturistTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.manager = ctx.getRenderManager();
    }

    public void render(HorticulturistTableBlockEntity blockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        BlockState blockState = blockEntity.getCachedState();
        BlockPos pos = blockEntity.getPos();
        DefaultedList<ItemStack> defaultedList = blockEntity.getInventory();

        if (blockState.getBlock() instanceof HorticulturistTableBlock) {
            matrixStack.push();
            if (blockState.get(HorticulturistTableBlock.IS_TALL_FLOWER)) {
                Block flower = Block.getBlockFromItem(defaultedList.get(0).getItem());
                if (flower instanceof TallPlantBlock) {
                    Vec3d offset = flower.getDefaultState().getModelOffset(blockEntity.getWorld(), pos);
                    matrixStack.translate(-offset.x, -offset.y + 0.95D, -offset.z);
                    renderTallFlower(flower.getDefaultState().getBlock(), blockEntity.getWorld(), pos, matrixStack, vertexConsumerProvider, true, j);
                    matrixStack.translate(-offset.x, -offset.y + 1.0D, -offset.z);
                    renderTallFlower(flower.getDefaultState().getBlock(), blockEntity.getWorld(), pos, matrixStack, vertexConsumerProvider, false, j);
                } else {
                    Block flowerOne = Block.getBlockFromItem(defaultedList.get(0).getItem());
                    if (flowerOne.getDefaultState().isOf(Blocks.CACTUS)) {
                        matrixStack.scale(0.75F, 0.75F, 0.75F);
                        matrixStack.translate(0.15D, 0.15D, 0.15D);
                    }

                    Vec3d offset = flowerOne.getDefaultState().getModelOffset(blockEntity.getWorld(), pos);
                    matrixStack.translate(-offset.x, -offset.y + 0.95D, -offset.z);
                    renderFlower(Block.getBlockFromItem(defaultedList.get(0).getItem()), blockEntity.getWorld(), pos, matrixStack, vertexConsumerProvider, j);
                }
            } else {
                switch (blockState.get(HorticulturistTableBlock.FLOWERS)) {
                    case 1 -> {
                        Block flowerOne = Block.getBlockFromItem(defaultedList.get(0).getItem());
                        Vec3d offset = flowerOne.getDefaultState().getModelOffset(blockEntity.getWorld(), pos);
                        matrixStack.translate(-offset.x, -offset.y + 0.95D, -offset.z);
                        renderFlower(flowerOne, blockEntity.getWorld(), pos, matrixStack, vertexConsumerProvider, j);
                    }
                    case 2 -> {
                        Block flowerOne = Block.getBlockFromItem(defaultedList.get(0).getItem());
                        Block flowerTwo = Block.getBlockFromItem(defaultedList.get(1).getItem());
                        Vec3d offset = flowerOne.getDefaultState().getModelOffset(blockEntity.getWorld(), pos);
                        Vec3d offset2 = flowerTwo.getDefaultState().getModelOffset(blockEntity.getWorld(), pos);
                        matrixStack.translate(-offset.x + 0.15D, -offset.y + 0.95D, -offset.z + 0.15D);
                        renderFlower(flowerOne, blockEntity.getWorld(), pos, matrixStack, vertexConsumerProvider, j);
                        matrixStack.translate(-offset2.x - 0.3D, -offset2.y, -offset2.z - 0.3D - 0.05D);
                        renderFlower(flowerTwo, blockEntity.getWorld(), pos, matrixStack, vertexConsumerProvider, j);
                    }
                    case 3 -> {
                        Block flowerOne = Block.getBlockFromItem(defaultedList.get(0).getItem());
                        Block flowerTwo = Block.getBlockFromItem(defaultedList.get(1).getItem());
                        Block flowerThree = Block.getBlockFromItem(defaultedList.get(2).getItem());
                        Vec3d offset = flowerOne.getDefaultState().getModelOffset(blockEntity.getWorld(), pos);
                        Vec3d offset2 = flowerTwo.getDefaultState().getModelOffset(blockEntity.getWorld(), pos);
                        Vec3d offset3 = flowerThree.getDefaultState().getModelOffset(blockEntity.getWorld(), pos);
                        matrixStack.translate(-offset.x + 0.15D, -offset.y + 0.95D, -offset.z);
                        renderFlower(flowerOne, blockEntity.getWorld(), pos, matrixStack, vertexConsumerProvider, j);
                        matrixStack.translate(-offset2.x - 0.3D, -offset2.y - 0.05D, -offset2.z - 0.15D);
                        renderFlower(flowerTwo, blockEntity.getWorld(), pos, matrixStack, vertexConsumerProvider, j);
                        matrixStack.translate(-offset3.x -0.05D, -offset3.y + 0, -offset3.z + 0.3D);
                        renderFlower(flowerThree, blockEntity.getWorld(), pos, matrixStack, vertexConsumerProvider, j);
                    }
                    case 4 -> {
                        Block flowerOne = Block.getBlockFromItem(defaultedList.get(0).getItem());
                        Block flowerTwo = Block.getBlockFromItem(defaultedList.get(1).getItem());
                        Block flowerThree = Block.getBlockFromItem(defaultedList.get(2).getItem());
                        Block flowerFour = Block.getBlockFromItem(defaultedList.get(3).getItem());
                        Vec3d offset = flowerOne.getDefaultState().getModelOffset(blockEntity.getWorld(), pos);
                        Vec3d offset2 = flowerTwo.getDefaultState().getModelOffset(blockEntity.getWorld(), pos);
                        Vec3d offset3 = flowerThree.getDefaultState().getModelOffset(blockEntity.getWorld(), pos);
                        Vec3d offset4 = flowerFour.getDefaultState().getModelOffset(blockEntity.getWorld(), pos);
                        matrixStack.translate(-offset.x + 0.15D, -offset.y + 0.95D, -offset.z + 0.15D);
                        renderFlower(flowerOne, blockEntity.getWorld(), pos, matrixStack, vertexConsumerProvider, j);
                        matrixStack.translate(-offset2.x, -offset2.y - 0.05D, -offset2.z - 0.3D - 0.05D);
                        renderFlower(Block.getBlockFromItem(defaultedList.get(1).getItem()), blockEntity.getWorld(), pos, matrixStack, vertexConsumerProvider, j);
                        matrixStack.translate(-offset3.x - 0.3D, -offset3.y + 0, -offset3.z);
                        renderFlower(Block.getBlockFromItem(defaultedList.get(2).getItem()), blockEntity.getWorld(), pos, matrixStack, vertexConsumerProvider, j);
                        matrixStack.translate(-offset4.x - 0.05D, -offset4.y - 0.05D, -offset4.z + 0.37D);
                        renderFlower(Block.getBlockFromItem(defaultedList.get(3).getItem()), blockEntity.getWorld(), pos, matrixStack, vertexConsumerProvider, j);
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

