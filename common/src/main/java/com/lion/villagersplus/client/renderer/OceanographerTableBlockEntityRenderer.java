package com.lion.villagersplus.client.renderer;

import com.lion.villagersplus.blockentities.OceanographerTableBlockEntity;
import com.lion.villagersplus.blocks.OceanographerTableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.CoralFanBlock;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
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
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class OceanographerTableBlockEntityRenderer implements BlockEntityRenderer<OceanographerTableBlockEntity, OceanographerTableBlockEntityRenderer.State> {

    public static class State extends BlockEntityRenderState {
        final Block[] corals = new Block[4];
        final Vec3d[] coralOffsets = new Vec3d[4];
        float coralScale = 1.0F;
        final List<FishDraw> fish = new ArrayList<>();
    }

    private static class FishDraw {
        EntityRenderState entityState;
        int dx;
        int dy;
        int dz;
        float x;
        float y;
        float z;
        float bob;
        float pitch;
        float bodyYaw;
        boolean pitched;
        float scale;
        float roll;
        boolean axolotl;
        float width;
        float height;
    }

    private final EntityRenderManager entityRenderDispatcher;

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

    public OceanographerTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.entityRenderDispatcher = ctx.entityRenderDispatcher();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void updateRenderState(
            OceanographerTableBlockEntity blockEntity,
            State state,
            float f,
            Vec3d cameraPos,
            ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay
    ) {
        BlockEntityRenderState.updateBlockEntityRenderState(blockEntity, state, crumblingOverlay);
        BlockPos pos = blockEntity.getPos();
        DefaultedList<ItemStack> inventory = blockEntity.getInventory();

        for (int it = 0; it < 4; it++) {
            ItemStack stack = inventory.get(it);

            if (stack.isEmpty()) {
                state.corals[it] = null;
                continue;
            }

            Block coral = Block.getBlockFromItem(stack.getItem());
            state.corals[it] = coral;
            state.coralOffsets[it] = coral.getDefaultState().getModelOffset(pos);
        }
        state.coralScale = blockEntity.getCoralScale();

        state.fish.clear();

        if (blockEntity.drawsOwnFish()) {
            addFish(state, blockEntity, 0, 0, 0, f);
        }

        for (OceanographerTableBlockEntity guest : blockEntity.getGuestFish()) {
            // The guest list is pruned only on tick, so a fish that moved on may still be in it.
            if (!guest.isFishInBlock(pos)) {
                continue;
            }

            BlockPos guestPos = guest.getPos();
            addFish(state, guest, guestPos.getX() - pos.getX(), guestPos.getY() - pos.getY(), guestPos.getZ() - pos.getZ(), f);
        }
    }

    private void addFish(State state, OceanographerTableBlockEntity owner, int dx, int dy, int dz, float f) {
        Entity fish = owner.getDisplayFish();

        if (fish == null) {
            return;
        }

        FishDraw draw = new FishDraw();
        draw.entityState = this.entityRenderDispatcher.getAndUpdateRenderState(fish, f);
        draw.entityState.light = state.lightmapCoordinates;
        // The display fish sits at the world origin, so shadows stay off.
        draw.entityState.shadowRadius = 0.0F;
        draw.entityState.shadowPieces.clear();
        draw.dx = dx;
        draw.dy = dy;
        draw.dz = dz;
        draw.width = fish.getWidth();
        draw.height = fish.getHeight();
        draw.scale = owner.getFishScale();
        draw.roll = owner.getRoll(f);
        float bob = MathHelper.sin(owner.getAnimAge(f) * 0.1F) / 2.0F + 0.5F;
        draw.bob = bob + bob * bob;
        draw.x = owner.getFishX(f);
        draw.y = owner.getFishY(f);
        draw.z = owner.getFishZ(f);
        draw.pitch = owner.getFishPitch(f);

        if (fish instanceof LivingEntity fishLiving) {
            draw.pitched = true;
            draw.bodyYaw = MathHelper.lerpAngleDegrees(f, fishLiving.lastBodyYaw, fishLiving.bodyYaw);
        }

        draw.axolotl = fish instanceof AxolotlEntity;
        state.fish.add(draw);
    }

    @Override
    public void render(
            State state,
            MatrixStack matrixStack,
            OrderedRenderCommandQueue queue,
            CameraRenderState camera
    ) {
        if (!(state.blockState.getBlock() instanceof OceanographerTableBlock)) {
            return;
        }

        float[] yOffset;
        float[] xOffset = X_OFFSET_DEFAULT;
        float[] zOffset = Z_OFFSET_DEFAULT;
        float[] xOffsetFan = X_OFFSET_FAN_DEFAULT;
        float[] zOffsetFan = Z_OFFSET_FAN_DEFAULT;
        switch (state.blockState.get(OceanographerTableBlock.FACING)) {
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

        float coralScale = state.coralScale;
        for (int it = 0; it < 4; it++) {
            Block coral = state.corals[it];

            if (coral == null) {
                continue;
            }

            Vec3d offset = state.coralOffsets[it];
            matrixStack.push();
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

            queue.submitBlock(matrixStack, coral.getDefaultState(), state.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0);
            matrixStack.pop();
        }

        for (FishDraw draw : state.fish) {
            renderFish(draw, matrixStack, queue, camera);
        }
    }

    private void renderFish(
            FishDraw draw,
            MatrixStack matrixStack,
            OrderedRenderCommandQueue queue,
            CameraRenderState camera
    ) {
        matrixStack.push();

        float g = 0.53125F;
        float hh = Math.max(draw.width, draw.height);

        if ((double) hh > 1.0D) {
            g /= hh;
        }

        g *= draw.scale;

        matrixStack.translate(
                (double) (draw.dx + draw.x),
                (double) (draw.dy + draw.y + draw.bob * 0.05F),
                (double) (draw.dz + draw.z)
        );

        // Undo the entity renderer's body yaw before the pitch.
        if (draw.pitch != 0.0F && draw.pitched) {
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-draw.bodyYaw));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(draw.pitch));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(draw.bodyYaw));
        }

        Vector3f vec3f = new Vector3f(0.5F, 1.0F, 0.5F);
        vec3f.normalize();
        matrixStack.multiply((new Quaternionf()).rotationAxis(hh * 0.017453292F, vec3f));
        matrixStack.scale(g, g, g);
        matrixStack.translate(0.0D, -0.2F, 0.0D);

        if (draw.roll != 0.0F) {
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(draw.roll));
        }

        if (draw.axolotl) {
            matrixStack.scale(0.8F, 0.8F, 0.8F);
        }

        this.entityRenderDispatcher.render(draw.entityState, camera, 0.0D, 0.0D, 0.0D, matrixStack, queue);

        matrixStack.pop();
    }

    /// Render up to 128 blocks, so the glass and the fish stay visible.
    @Override
    public int getRenderDistance() {
        return 128;
    }

    /// Culling stays off for all blocks, because the method gets no block entity.
    @Override
    public boolean rendersOutsideBoundingBox() {
        return true;
    }
}
