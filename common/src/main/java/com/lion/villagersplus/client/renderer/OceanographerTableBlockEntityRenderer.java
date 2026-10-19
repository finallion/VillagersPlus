package com.lion.villagersplus.client.renderer;

import com.lion.villagersplus.blockentities.OceanographerTableBlockEntity;
import com.lion.villagersplus.blocks.OceanographerTableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CoralFanBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class OceanographerTableBlockEntityRenderer implements BlockEntityRenderer<OceanographerTableBlockEntity, OceanographerTableBlockEntityRenderer.State> {

    public static class State extends BlockEntityRenderState {
        final Block[] corals = new Block[4];
        final Vec3[] coralOffsets = new Vec3[4];
        final BlockModelRenderState[] coralModels = new BlockModelRenderState[4];
        float coralScale = 1.0F;
        boolean valid;
        Direction facing = Direction.NORTH;
        final List<FishDraw> fish = new ArrayList<>();

        State() {
            for (int n = 0; n < coralModels.length; n++) {
                coralModels[n] = new BlockModelRenderState();
            }
        }
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

    private static final BlockDisplayContext DISPLAY_CONTEXT = BlockDisplayContext.create();

    private final EntityRenderDispatcher entityRenderDispatcher;
    private final BlockModelResolver blockModelResolver;

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

    public OceanographerTableBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.entityRenderDispatcher = ctx.entityRenderer();
        this.blockModelResolver = ctx.blockModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(
            OceanographerTableBlockEntity blockEntity,
            State state,
            float f,
            Vec3 cameraPos,
            ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderState.extractBase(blockEntity, state, crumblingOverlay);
        BlockPos pos = blockEntity.getBlockPos();
        NonNullList<ItemStack> inventory = blockEntity.getInventory();

        BlockState tableState = blockEntity.getBlockState();
        state.valid = tableState.getBlock() instanceof OceanographerTableBlock;

        if (state.valid) {
            state.facing = tableState.getValue(OceanographerTableBlock.FACING);
        }

        for (int it = 0; it < 4; it++) {
            ItemStack stack = inventory.get(it);

            if (stack.isEmpty()) {
                state.corals[it] = null;
                continue;
            }

            Block coral = Block.byItem(stack.getItem());
            state.corals[it] = coral;
            state.coralOffsets[it] = coral.defaultBlockState().getOffset(pos);
            blockModelResolver.update(state.coralModels[it], coral.defaultBlockState(), DISPLAY_CONTEXT);
        }
        state.coralScale = blockEntity.getCoralScale();

        state.fish.clear();

        if (blockEntity.drawsOwnFish()) {
            addFish(state, blockEntity, 0, 0, 0, f);
        }

        for (OceanographerTableBlockEntity guest : blockEntity.getGuestFish()) {
            // The guest list is pruned on tick, so it may still list a fish that moved on.
            if (!guest.isFishInBlock(pos)) {
                continue;
            }

            BlockPos guestPos = guest.getBlockPos();
            addFish(state, guest, guestPos.getX() - pos.getX(), guestPos.getY() - pos.getY(), guestPos.getZ() - pos.getZ(), f);
        }
    }

    private void addFish(State state, OceanographerTableBlockEntity owner, int dx, int dy, int dz, float f) {
        Entity fish = owner.getDisplayFish();

        if (fish == null) {
            return;
        }

        FishDraw draw = new FishDraw();
        draw.entityState = this.entityRenderDispatcher.extractEntity(fish, f);
        draw.entityState.lightCoords = state.lightCoords;
        draw.entityState.shadowRadius = 0.0F;
        draw.entityState.shadowPieces.clear();
        draw.dx = dx;
        draw.dy = dy;
        draw.dz = dz;
        draw.width = fish.getBbWidth();
        draw.height = fish.getBbHeight();
        draw.scale = owner.getFishScale();
        draw.roll = owner.getRoll(f);
        float bob = Mth.sin(owner.getAnimAge(f) * 0.1F) / 2.0F + 0.5F;
        draw.bob = bob + bob * bob;
        draw.x = owner.getFishX(f);
        draw.y = owner.getFishY(f);
        draw.z = owner.getFishZ(f);
        draw.pitch = owner.getFishPitch(f);

        if (fish instanceof LivingEntity fishLiving) {
            draw.pitched = true;
            draw.bodyYaw = Mth.rotLerp(f, fishLiving.yBodyRotO, fishLiving.yBodyRot);
        }

        draw.axolotl = fish instanceof Axolotl;
        state.fish.add(draw);
    }

    @Override
    public void submit(State state, PoseStack matrixStack, SubmitNodeCollector queue, CameraRenderState camera) {
        if (!state.valid) {
            return;
        }

        float[] yOffset;
        float[] xOffset = X_OFFSET_DEFAULT;
        float[] zOffset = Z_OFFSET_DEFAULT;
        float[] xOffsetFan = X_OFFSET_FAN_DEFAULT;
        float[] zOffsetFan = Z_OFFSET_FAN_DEFAULT;
        switch (state.facing) {
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

            Vec3 offset = state.coralOffsets[it];
            matrixStack.pushPose();
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

            state.coralModels[it].submit(matrixStack, queue, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            matrixStack.popPose();
        }

        for (FishDraw draw : state.fish) {
            renderFish(draw, matrixStack, queue, camera);
        }
    }

    private void renderFish(FishDraw draw, PoseStack matrixStack, SubmitNodeCollector queue, CameraRenderState camera) {
        matrixStack.pushPose();

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

        if (draw.pitch != 0.0F && draw.pitched) {
            matrixStack.rotate(Axis.YP.rotationDegrees(-draw.bodyYaw));
            matrixStack.rotate(Axis.XP.rotationDegrees(draw.pitch));
            matrixStack.rotate(Axis.YP.rotationDegrees(draw.bodyYaw));
        }

        Vector3f vec3f = new Vector3f(0.5F, 1.0F, 0.5F);
        vec3f.normalize();
        matrixStack.rotate((new Quaternionf()).rotationAxis(hh * 0.017453292F, vec3f));
        matrixStack.scale(g, g, g);
        matrixStack.translate(0.0D, -0.2F, 0.0D);

        if (draw.roll != 0.0F) {
            matrixStack.rotate(Axis.XP.rotationDegrees(draw.roll));
        }

        if (draw.axolotl) {
            matrixStack.scale(0.8F, 0.8F, 0.8F);
        }

        this.entityRenderDispatcher.submit(draw.entityState, camera, 0.0D, 0.0D, 0.0D, matrixStack, queue);

        matrixStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 128;
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }
}
