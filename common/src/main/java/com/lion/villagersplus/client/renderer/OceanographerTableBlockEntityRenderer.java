package com.lion.villagersplus.client.renderer;

import com.lion.villagersplus.blockentities.OceanographerTableBlockEntity;
import com.lion.villagersplus.blocks.OceanographerTableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CoralFanBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.item.ItemStack;
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

    private static final float[] Y_OFFSET_NORTH = {0.45F, 0.75F, 0.3F, 0.45F};
    private static final float[] Y_OFFSET_SOUTH = {0.75F, 0.45F, 0.45F, 0.3F};
    private static final float[] Y_OFFSET_EAST = {0.45F, 0.3F, 0.45F, 0.75F};
    private static final float[] Y_OFFSET_WEST = {0.3F, 0.45F, 0.75F, 0.45F};

    private static final float[] X_OFFSET_DEFAULT = {0.06F, 1.5F, 1.3F, 0.1F};
    private static final float[] Z_OFFSET_DEFAULT = {0.06F, 1.5F, 0.3F, 1.45F};
    private static final float[] X_OFFSET_FAN_DEFAULT = {0.2F, 1.25F, 1.2F, 0.2F};
    private static final float[] Z_OFFSET_FAN_DEFAULT = {0.17F, 1.25F, 0.3F, 1.2F};

    private static final float[] X_OFFSET_EAST = {0.06F, 1.5F, 1.3F, 0.015F};
    private static final float[] Z_OFFSET_EAST = {0.06F, 1.5F, 0.3F, 1.5F};
    private static final float[] X_OFFSET_FAN_EAST = {0.2F, 1.25F, 1.2F, 0.115F};
    private static final float[] Z_OFFSET_FAN_EAST = {0.17F, 1.25F, 0.3F, 1.3F};

    private static final float[] X_OFFSET_WEST = {0.06F, 1.5F, 1.55F, 0.1F};
    private static final float[] Z_OFFSET_WEST = {0.06F, 1.5F, 0.15F, 1.45F};
    private static final float[] X_OFFSET_FAN_WEST = {0.2F, 1.25F, 1.45F, 0.2F};
    private static final float[] Z_OFFSET_FAN_WEST = {0.17F, 1.25F, 0.15F, 1.2F};

    private final Random renderRandom = Random.create();

    public OceanographerTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.manager = ctx.getRenderManager();
        this.entityRenderDispatcher = ctx.getEntityRenderDispatcher();
    }

    @Override
    public void render(
            OceanographerTableBlockEntity blockEntity,
            float f,
            MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider,
            int i,
            int j,
            net.minecraft.util.math.Vec3d cameraPos
    ) {
        BlockState blockState = blockEntity.getCachedState();
        BlockPos pos = blockEntity.getPos();
        World world = blockEntity.getWorld();
        DefaultedList<ItemStack> defaultedList = blockEntity.getInventory();

        if (blockState.getBlock() instanceof OceanographerTableBlock) {
            float[] yOffset;
            float[] xOffset = X_OFFSET_DEFAULT;
            float[] zOffset = Z_OFFSET_DEFAULT;
            float[] xOffsetFan = X_OFFSET_FAN_DEFAULT;
            float[] zOffsetFan = Z_OFFSET_FAN_DEFAULT;
            switch (blockState.get(OceanographerTableBlock.FACING)) {
                case EAST -> {
                    yOffset = Y_OFFSET_EAST;
                    xOffset = X_OFFSET_EAST;
                    zOffset = Z_OFFSET_EAST;
                    xOffsetFan = X_OFFSET_FAN_EAST;
                    zOffsetFan = Z_OFFSET_FAN_EAST;
                }
                case WEST -> {
                    yOffset = Y_OFFSET_WEST;
                    xOffset = X_OFFSET_WEST;
                    zOffset = Z_OFFSET_WEST;
                    xOffsetFan = X_OFFSET_FAN_WEST;
                    zOffsetFan = Z_OFFSET_FAN_WEST;
                }
                case SOUTH -> yOffset = Y_OFFSET_SOUTH;
                default -> yOffset = Y_OFFSET_NORTH;
            }

            float coralScale = blockEntity.getCoralScale();
            for (int it = 0; it < 4; it++) {
                if (defaultedList.get(it).isEmpty()) {
                    continue;
                }

                matrixStack.push();
                Block coral = Block.getBlockFromItem(defaultedList.get(it).getItem());
                Vec3d offset = coral.getDefaultState().getModelOffset(pos);
                matrixStack.scale(0.4F, 0.4F, 0.4F);

                if (coral instanceof CoralFanBlock) {
                    matrixStack.translate(-offset.x + xOffsetFan[it], -offset.y + yOffset[it], -offset.z + zOffsetFan[it]);
                } else {
                    matrixStack.translate(-offset.x + xOffset[it], -offset.y + yOffset[it], -offset.z + zOffset[it]);
                }

                if (coralScale != 1.0F) {
                    matrixStack.translate(0.5F * (1.0F - coralScale), 0.0F, 0.5F * (1.0F - coralScale));
                    matrixStack.scale(coralScale, coralScale, coralScale);
                }

                renderCoral(coral, world, pos, matrixStack, vertexConsumerProvider, i, j);
                matrixStack.pop();
            }

            if (blockEntity.drawsOwnFish()) {
                renderFish(blockEntity, 0, 0, 0, f, matrixStack, vertexConsumerProvider, i);
            }

            for (OceanographerTableBlockEntity guest : blockEntity.getGuestFish()) {
                if (!guest.isFishInBlock(pos)) {
                    continue;
                }

                BlockPos guestPos = guest.getPos();
                renderFish(
                        guest,
                        guestPos.getX() - pos.getX(),
                        guestPos.getY() - pos.getY(),
                        guestPos.getZ() - pos.getZ(),
                        f,
                        matrixStack,
                        vertexConsumerProvider,
                        i
                );
            }
        }
    }

    private void renderFish(
            OceanographerTableBlockEntity owner,
            int dx,
            int dy,
            int dz,
            float f,
            MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider,
            int light
    ) {
        Entity fish = owner.getDisplayFish();

        if (fish == null) {
            return;
        }

        matrixStack.push();

        float scale = owner.getFishScale();
        float g = 0.53125F;
        float hh = Math.max(fish.getWidth(), fish.getHeight());

        if ((double) hh > 1.0D) {
            g /= hh;
        }

        g *= scale;

        float roll = owner.getRoll(f);

        float bob = MathHelper.sin(owner.getAnimAge(f) * 0.1F) / 2.0F + 0.5F;
        bob += bob * bob;
        matrixStack.translate(
                (double) (dx + owner.getFishX(f)),
                (double) (dy + owner.getFishY(f) + bob * 0.05F),
                (double) (dz + owner.getFishZ(f))
        );

        // Pitch around the lateral axis after the body yaw.
        float pitch = owner.getFishPitch(f);

        if (pitch != 0.0F && fish instanceof LivingEntity fishLiving) {
            float bodyYaw = MathHelper.lerpAngleDegrees(f, fishLiving.lastBodyYaw, fishLiving.bodyYaw);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-bodyYaw));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(bodyYaw));
        }

        Vector3f vec3f = new Vector3f(0.5F, 1.0F, 0.5F);
        vec3f.normalize();
        matrixStack.multiply((new Quaternionf()).rotationAxis(hh * 0.017453292F, vec3f));
        matrixStack.scale(g, g, g);
        matrixStack.translate(0.0D, -0.2F, 0.0D);

        if (roll != 0.0F) {
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(roll));
        }

        if (fish instanceof AxolotlEntity) {
            matrixStack.scale(0.8F, 0.8F, 0.8F);
        }

        // Shadows stay off. The display fish sits at the world origin.
        this.entityRenderDispatcher.setRenderShadows(false);
        this.entityRenderDispatcher.render(fish, 0.0D, 0.0D, 0.0D, f, matrixStack, vertexConsumerProvider, light);
        this.entityRenderDispatcher.setRenderShadows(true);

        matrixStack.pop();
    }

    /// Draws the tank contents with the reach of the block, not the default 64 blocks.
    @Override
    public int getRenderDistance() {
        return 128;
    }

    /// Skips section culling, because a fish may swim into neighbouring blocks.
    @Override
    public boolean rendersOutsideBoundingBox() {
        return true;
    }

    private void renderCoral(
            Block coral,
            World world,
            BlockPos pos,
            MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider,
            int light,
            int overlay
    ) {
        this.manager.renderBlockAsEntity(coral.getDefaultState(), matrixStack, vertexConsumerProvider, light, overlay);
    }
}
