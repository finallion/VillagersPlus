package com.finallion.villagersplus.client.renderer;

import com.finallion.villagersplus.blockentities.OceanographerTableBlockEntity;
import com.finallion.villagersplus.blocks.OceanographerTableBlock;
import com.finallion.villagersplus.mixin.EntityAccessorMixin;
import com.finallion.villagersplus.util.DuckBucketable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CoralFanBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.*;
import net.minecraft.item.EntityBucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.random.Random;;
import net.minecraft.world.World;


public class OceanographerTableBlockEntityRenderer implements BlockEntityRenderer<OceanographerTableBlockEntity> {
    private final BlockRenderManager manager;
    private final EntityRenderDispatcher entityRenderDispatcher;

    private float[] xOffset = new float[]{0.06F, 1.5F, 1.3F, 0.1F};
    private final float[] yOffsetNorth = new float[]{0.45F, 0.75F, 0.3F, 0.45F};
    private final float[] yOffsetSouth = new float[]{0.75F, 0.45F, 0.45F, 0.3F};
    private final float[] yOffsetEast = new float[]{0.45F, 0.3F, 0.45F, 0.75F};
    private final float[] yOffsetWest = new float[]{0.3F, 0.45F, 0.75F, 0.45F};
    private float[] zOffset = new float[]{0.06F, 1.5F, 0.3F, 1.45F};

    private float[] xOffsetFan = new float[]{0.2F, 1.25F, 1.2F, 0.2F};
    private float[] zOffsetFan = new float[]{0.17F, 1.25F, 0.3F, 1.2F};

    private float prevValue = 0.0F;

    public OceanographerTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.manager = ctx.getRenderManager();
        this.entityRenderDispatcher = ctx.getEntityRenderDispatcher();
    }

    public void render(OceanographerTableBlockEntity blockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        BlockState blockState = blockEntity.getCachedState();
        BlockPos pos = blockEntity.getPos();
        World world = blockEntity.getWorld();
        DefaultedList<ItemStack> defaultedList = blockEntity.getInventory();

        if (blockState.getBlock() instanceof OceanographerTableBlock) {
            float rotation = 0;
            float[] yOffset = new float[4];
            switch (blockState.get(OceanographerTableBlock.FACING)) {
                case EAST -> {
                    rotation = 90.0F;
                    yOffset = yOffsetEast;
                    xOffset = new float[]{0.06F, 1.5F, 1.3F, 0.015F}; // offset adjustments
                    zOffset = new float[]{0.06F, 1.5F, 0.3F, 1.5F};
                    xOffsetFan = new float[]{0.2F, 1.25F, 1.2F, 0.115F};
                    zOffsetFan = new float[]{0.17F, 1.25F, 0.3F, 1.3F};
                }
                case WEST -> {
                    rotation = 270.0F;
                    yOffset = yOffsetWest;
                    xOffset = new float[]{0.06F, 1.5F, 1.55F, 0.1F}; // offset adjustments
                    zOffset = new float[]{0.06F, 1.5F, 0.15F, 1.45F};
                    xOffsetFan = new float[]{0.2F, 1.25F, 1.45F, 0.2F};
                    zOffsetFan = new float[]{0.17F, 1.25F, 0.15F, 1.2F};
                }
                case NORTH -> {
                    rotation = 180.0F;
                    yOffset = yOffsetNorth;
                }
                case SOUTH -> {
                    rotation = 0.0F;
                    yOffset = yOffsetSouth;
                }
            }

            for (int it = 0; it < 4; it++) {
                matrixStack.push();
                Block coral = Block.getBlockFromItem(defaultedList.get(it).getItem());
                Vec3d offset = coral.getDefaultState().getModelOffset(world, pos);
                matrixStack.scale(0.4F, 0.4F, 0.4F);
                if (coral instanceof CoralFanBlock) {
                    matrixStack.translate(-offset.x + xOffsetFan[it], -offset.y + yOffset[it], -offset.z + zOffsetFan[it]);
                } else {
                    matrixStack.translate(-offset.x + xOffset[it], -offset.y + yOffset[it], -offset.z + zOffset[it]);
                }
                renderCoral(coral, world, pos, matrixStack, vertexConsumerProvider, j);
                matrixStack.pop();
            }

            ItemStack itemStack = defaultedList.get(4);
            if (itemStack.getItem() instanceof EntityBucketItem bucketItem) {
                EntityType<?> type = ((DuckBucketable) bucketItem).getEntityType();
                if (type != null) {
                    Entity fish = type.create(world);

                    if (fish != null && itemStack.getNbt() != null) {
                        if (fish instanceof TropicalFishEntity && itemStack.getNbt().contains("BucketVariantTag")) {
                            ((TropicalFishEntity) fish).setVariant(itemStack.getNbt().getInt("BucketVariantTag"));
                        } else {
                            fish.readNbt(itemStack.getOrCreateNbt());
                        }
                    }

                    matrixStack.push();

                    if (fish != null && world != null) {
                        ((EntityAccessorMixin)fish).setTouchingWater(true);

                        long time = world.getTime();
                        float g = 0.53125F;
                        float h = Math.max(fish.getWidth(), fish.getHeight());
                        if ((double) h > 1.0D) {
                            g /= h;
                        }

                        float k = MathHelper.sin(time * 0.1F) / 2.0F + 0.5F;
                        k += k * k;
                        matrixStack.translate(0.5D, (double) (0.5F + k * 0.05F), 0.5D);

                        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotation));
                        Vec3f vec3f = new Vec3f(0.5F, 1.0F, 0.5F);
                        vec3f.normalize();
                        matrixStack.multiply(vec3f.getDegreesQuaternion(h));
                        matrixStack.scale(g, g, g);

                        if (!(fish instanceof AxolotlEntity axolotlEntity)) {
                            f = (float) world.getTime() / 4;
                            matrixStack.translate(0.0D, -0.2F, 0.0D);

                            this.entityRenderDispatcher.render(fish, 0.0D, 0.0D, 0.0D, 0.0F, f, matrixStack, vertexConsumerProvider, i);
                        } else {
                            matrixStack.translate(0.0D, -0.2F, 0.0D);
                            matrixStack.scale(0.8F, 0.8F, 0.8F);

                            float animationProgress = world.getTime() % 20 / 20.0F;
                            float targetValue = 2.15F * MathHelper.sin(animationProgress * (float) Math.PI * 4F);
                            float smoothedValue = prevValue + 0.002F * (targetValue - prevValue);
                            prevValue = smoothedValue;

                            if (MinecraftClient.getInstance().isPaused()) {
                                smoothedValue = 0.0F;
                                prevValue = 0.0F;
                            }

                            this.entityRenderDispatcher.getRenderer(axolotlEntity).render(axolotlEntity, 0.0F, smoothedValue, matrixStack, vertexConsumerProvider, i);
                        }
                    }

                    matrixStack.pop();
                }
            }
        }
    }

    private void renderCoral(Block coral, World world, BlockPos pos, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int overlay) {
        this.manager.getModelRenderer().render(world, this.manager.getModel(coral.getDefaultState()), coral.getDefaultState(), pos, matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getCutoutMipped()), false, Random.create(), coral.getDefaultState().getRenderingSeed(pos), overlay);
    }
}

