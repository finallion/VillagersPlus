package com.finallion.villagersplus.client.renderer;

import com.finallion.villagersplus.blockentities.OceanographerTableBlockEntity;
import com.finallion.villagersplus.blocks.OceanographerTableBlock;
import com.finallion.villagersplus.init.ModTags;
import com.finallion.villagersplus.mixin.TropicalFishEntityInvoker;
import com.finallion.villagersplus.util.DuckBucketable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CoralFanBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.*;
import net.minecraft.item.EntityBucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class OceanographerTableBlockEntityRenderer implements BlockEntityRenderer<OceanographerTableBlockEntity> {
    private final BlockRenderManager manager;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final MinecraftClient minecraft;
    private float[] xOffset = new float[]{0.06F, 1.5F, 1.3F, 0.1F};
    private final float[] yOffsetNorth = new float[]{0.45F, 0.75F, 0.3F, 0.45F};
    private final float[] yOffsetSouth = new float[]{0.75F, 0.45F, 0.45F, 0.3F};
    private final float[] yOffsetEast = new float[]{0.45F, 0.3F, 0.45F, 0.75F};
    private final float[] yOffsetWest = new float[]{0.3F, 0.45F, 0.75F, 0.45F};
    private float[] zOffset = new float[]{0.06F, 1.5F, 0.3F, 1.45F};

    private float[] xOffsetFan = new float[]{0.2F, 1.25F, 1.2F, 0.2F};
    private float[] zOffsetFan = new float[]{0.17F, 1.25F, 0.3F, 1.2F};

    public OceanographerTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.minecraft = MinecraftClient.getInstance();
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
                Entity fish = ((DuckBucketable) bucketItem).getEntityType().create(world);

                if (fish != null && itemStack.getNbt() != null) {
                    fish.readNbt(itemStack.getOrCreateNbt());

                    if (fish instanceof TropicalFishEntity && itemStack.getNbt().contains("BucketVariantTag")) {
                        int id = itemStack.getNbt().getInt("BucketVariantTag");
                        DyeColor pattern = TropicalFishEntity.getPatternDyeColor(id);
                        DyeColor base = TropicalFishEntity.getBaseDyeColor(id);
                        TropicalFishEntity.Variant variant = new TropicalFishEntity.Variant(TropicalFishEntity.getVariety(id), base, pattern);
                        ((TropicalFishEntityInvoker) fish).setTropicalFishVariantMixin(variant.getId());
                    }
                }

                matrixStack.push();

                if (fish != null) {
                    long time = world.getTime();
                    float g = 0.53125F;
                    float h = Math.max(fish.getWidth(), fish.getHeight());
                    if ((double) h > 1.0D) {
                        g /= h;
                    }

                    float k = MathHelper.sin(time * 0.1F) / 2.0F + 0.5F;
                    k += k * k;
                    matrixStack.translate(0.5D, (double) (0.5F + k * 0.05F), 0.5D);

                    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
                    Vector3f vec3f = new Vector3f(0.5F, 1.0F, 0.5F);
                    vec3f.normalize();
                    matrixStack.multiply((new Quaternionf()).rotationAxis(h * 0.017453292F, vec3f));

                    if (itemStack.isIn(ModTags.ROTATIONAL_BUCKET_ENTITIES)) {
                        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
                    }

                    matrixStack.scale(g, g, g);

                    if (!(fish instanceof AxolotlEntity)) {
                        f = (float) time / 4;
                    } else {
                        matrixStack.translate(0.0D, -0.2F, 0.0D);
                        matrixStack.scale(0.8F, 0.8F, 0.8F);
                        //f = Math.abs(f) * 10.0F;
                        f = 0.0F;
                    }

                    this.entityRenderDispatcher.render(fish, 0.0D, 0.0D, 0.0D, 0.0F, f, matrixStack, vertexConsumerProvider, i);
                }

                matrixStack.pop();
            }
        }
    }

    private void renderCoral(Block coral, World world, BlockPos pos, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int overlay) {
        this.manager.getModelRenderer().render(world, this.manager.getModel(coral.getDefaultState()), coral.getDefaultState(), pos, matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getCutoutMipped()), false, Random.create(), coral.getDefaultState().getRenderingSeed(pos), overlay);
    }

    public EntityRendererFactory.Context getEntityRenderer() {
        return new EntityRendererFactory.Context(minecraft.getEntityRenderDispatcher(), minecraft.getItemRenderer(), minecraft.getBlockRenderManager(), minecraft.getEntityRenderDispatcher().getHeldItemRenderer(), minecraft.getResourceManager(), minecraft.getEntityModelLoader(), minecraft.textRenderer);
    }

}

