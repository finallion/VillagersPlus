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

    // Per-facing coral placement. Immutable and selected locally per render call: the renderer
    // instance is shared by ALL aquarium block entities, so mutating shared fields per facing
    // (as this used to do) leaked one aquarium's offsets into the next one rendered that frame.
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

    /** Reused for block model rendering; the model renderer re-seeds it per face anyway. */
    private final Random renderRandom = Random.create();

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
                // Skipped before the model path: an empty slot would otherwise cost a lookup, an
                // offset and a full render pass over air's empty model, per slot per aquarium per
                // frame.
                if (defaultedList.get(it).isEmpty()) {
                    continue;
                }
                matrixStack.push();
                Block coral = Block.getBlockFromItem(defaultedList.get(it).getItem());
                Vec3d offset = coral.getDefaultState().getModelOffset(world, pos);
                matrixStack.scale(0.4F, 0.4F, 0.4F);
                if (coral instanceof CoralFanBlock) {
                    matrixStack.translate(-offset.x + xOffsetFan[it], -offset.y + yOffset[it], -offset.z + zOffsetFan[it]);
                } else {
                    matrixStack.translate(-offset.x + xOffset[it], -offset.y + yOffset[it], -offset.z + zOffset[it]);
                }
                // Bone-meal / shears size, scaled around the centre of the coral's base.
                if (coralScale != 1.0F) {
                    matrixStack.translate(0.5F * (1.0F - coralScale), 0.0F, 0.5F * (1.0F - coralScale));
                    matrixStack.scale(coralScale, coralScale, coralScale);
                }
                renderCoral(coral, world, pos, matrixStack, vertexConsumerProvider, j);
                matrixStack.pop();
            }

            // A fish is drawn by the aquarium it currently swims in, not by the one that owns it -
            // see OceanographerTableBlockEntity#handOffFishToHost. So this block draws its own fish
            // only while it is still in here, plus any fish handed over by its neighbours.
            if (blockEntity.drawsOwnFish()) {
                renderFish(blockEntity, 0, 0, 0, f, matrixStack, vertexConsumerProvider, i);
            }
            for (OceanographerTableBlockEntity guest : blockEntity.getGuestFish()) {
                // The list is pruned on tick; re-check here so a fish that moved on between the
                // tick and this frame is not drawn twice.
                if (!guest.isFishInBlock(pos)) {
                    continue;
                }
                BlockPos guestPos = guest.getPos();
                renderFish(guest, guestPos.getX() - pos.getX(), guestPos.getY() - pos.getY(), guestPos.getZ() - pos.getZ(),
                        f, matrixStack, vertexConsumerProvider, i);
            }
        }
    }

    /**
     * Draws {@code owner}'s fish. The entity is created and ticked by the block entity (persistent
     * animation state), so here we only place and orient it along its interpolated path.
     *
     * <p>{@code dx/dy/dz} is the offset from the block doing the drawing to the fish's owner; the
     * tank-local swim position is added on top. It is zero whenever a block draws its own fish.
     */
    private void renderFish(OceanographerTableBlockEntity owner, int dx, int dy, int dz, float f,
                            MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        Entity fish = owner.getDisplayFish();
        if (fish == null) {
            return;
        }
        matrixStack.push();

        // Fish-food / diet-food size: scale the model, and shrink the swim circle for bigger
        // animals so they stay inside the tub.
        float scale = owner.getFishScale();
        float g = 0.53125F;
        float hh = Math.max(fish.getWidth(), fish.getHeight());
        if ((double) hh > 1.0D) {
            g /= hh;
        }
        g *= scale;

        float roll = owner.getRoll(f);

        // Position comes from the block entity: a circle in a single tub, or a free
        // wander path across all connected aquarium blocks (tank-local coordinates).
        float bob = MathHelper.sin(owner.getAnimAge(f) * 0.1F) / 2.0F + 0.5F;
        bob += bob * bob;
        matrixStack.translate((double) (dx + owner.getFishX(f)),
                (double) (dy + owner.getFishY(f) + bob * 0.05F),
                (double) (dz + owner.getFishZ(f)));

        // Nose up/down while travelling vertically: pitch around the fish's lateral
        // axis (conjugate by the body yaw, which the entity renderer applies itself).
        float pitch = owner.getFishPitch(f);
        if (pitch != 0.0F && fish instanceof LivingEntity fishLiving) {
            float bodyYaw = MathHelper.lerpAngleDegrees(f, fishLiving.prevBodyYaw, fishLiving.bodyYaw);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-bodyYaw));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(bodyYaw));
        }

        Vector3f vec3f = new Vector3f(0.5F, 1.0F, 0.5F);
        vec3f.normalize();
        matrixStack.multiply((new Quaternionf()).rotationAxis(hh * 0.017453292F, vec3f));
        matrixStack.scale(g, g, g);
        matrixStack.translate(0.0D, -0.2F, 0.0D);

        // Axolotl "tumble" - a playful barrel roll around its own axis.
        if (roll != 0.0F) {
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(roll));
        }
        if (fish instanceof AxolotlEntity) {
            matrixStack.scale(0.8F, 0.8F, 0.8F);
        }

        // The ticked entity's bodyYaw drives the heading; f interpolates the animation.
        // Shadows off for the same reason vanilla turns them off when it draws an entity
        // outside the world: EntityRenderDispatcher casts the shadow at the entity's *world*
        // position, and the display fish is never placed, so it sits at (0, 0, 0). Near
        // spawn that stacked a shadow blob at the origin for every aquarium in range.
        this.entityRenderDispatcher.setRenderShadows(false);
        this.entityRenderDispatcher.render(fish, 0.0D, 0.0D, 0.0D, 0.0F, f, matrixStack, vertexConsumerProvider, light);
        this.entityRenderDispatcher.setRenderShadows(true);

        matrixStack.pop();
    }

    /**
     * The tank itself is chunk geometry and stays visible as far as the chunk does, but everything
     * this renderer draws inside it - corals and fish - is gated by
     * {@code BlockEntityRenderDispatcher.render} on {@link #isInRenderDistance}, which defaults to
     * 64 blocks. At that line the aquarium keeps its glass and empties out in one step. Give the
     * contents the reach the block itself has instead.
     */
    @Override
    public int getRenderDistance() {
        return 128;
    }

    @Override
    public boolean rendersOutsideBoundingBox(OceanographerTableBlockEntity blockEntity) {
        // In a multi-block tank the fish may swim into neighbouring blocks; skip
        // per-section culling so it doesn't vanish at chunk section borders.
        // Read the connections from the block state, not the BE's lazily rescanned
        // flag: this is evaluated at chunk-rebuild time, where the state is fresh
        // but the flag can lag behind by up to a rescan interval.
        BlockState state = blockEntity.getCachedState();
        if (!(state.getBlock() instanceof OceanographerTableBlock)) {
            return false;
        }
        // Only a fish leaves its own block. Corals stay inside theirs, so an aquarium without a
        // fish gains nothing from being drawn every frame - and in a large tank most blocks are
        // empty, which is what keeps the always-drawn set small at the render distance above.
        if (state.get(OceanographerTableBlock.FISH) < 1) {
            return false;
        }
        return state.get(OceanographerTableBlock.NORTH) || state.get(OceanographerTableBlock.EAST)
                || state.get(OceanographerTableBlock.SOUTH) || state.get(OceanographerTableBlock.WEST)
                || state.get(OceanographerTableBlock.UP) || state.get(OceanographerTableBlock.DOWN);
    }

    private void renderCoral(Block coral, World world, BlockPos pos, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int overlay) {
        this.manager.getModelRenderer().render(world, this.manager.getModel(coral.getDefaultState()), coral.getDefaultState(), pos, matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getCutoutMipped()), false, this.renderRandom, coral.getDefaultState().getRenderingSeed(pos), overlay);
    }
}
