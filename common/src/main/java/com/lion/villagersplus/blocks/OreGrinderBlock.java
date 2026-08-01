package com.lion.villagersplus.blocks;

import com.mojang.serialization.MapCodec;
import com.lion.villagersplus.blockentities.OreGrinderBlockEntity;
import com.lion.villagersplus.init.VPBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class OreGrinderBlock extends WorkstationBlock {
    public static final EnumProperty<Direction> FACING;
    public static final BooleanProperty LIT;

    // The onPlaced override that copied a named block item's name onto the block entity is gone:
    // LockableContainerBlockEntity reads minecraft:custom_name out of the item's components itself now
    // (see its readComponents), so doing it here would only duplicate vanilla.

    /** BlockWithEntity requires a codec as of 1.20.5; this block has no state beyond its settings. */
    public static final MapCodec<OreGrinderBlock> CODEC = createCodec(OreGrinderBlock::new);

    @Override
    protected MapCodec<? extends OreGrinderBlock> getCodec() {
        return CODEC;
    }

    public OreGrinderBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(LIT, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OreGrinderBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, VPBlockEntities.ORE_GRINDER_BLOCK_ENTITY.get(), OreGrinderBlockEntity::tick);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof OreGrinderBlockEntity) {
                player.openHandledScreen((OreGrinderBlockEntity) blockEntity);
            }

            return ActionResult.CONSUME;
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!state.get(LIT)) {
            return;
        }

        double d = (double) pos.getX() + 0.5D;
        double e = (double) pos.getY() + 0.55D + (double) random.nextFloat() * 0.15D;
        double f = (double) pos.getZ() + 0.5D;

        if (random.nextDouble() < 0.1D) {
            world.playSoundClient(d, e, f, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
        }

        world.addParticleClient(ParticleTypes.SMOKE, d + (random.nextDouble() - 0.5D) * 0.3D, e, f + (random.nextDouble() - 0.5D) * 0.3D, 0.0D, 0.02D, 0.0D);
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        // Since 1.21.6 this only fires when the block really changed and only on the server, so
        // the old isOf(newState) guard is gone along with the newState parameter.
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof OreGrinderBlockEntity) {
            ItemScatterer.spawn(world, pos, (OreGrinderBlockEntity) blockEntity);
        }

        super.onStateReplaced(state, world, pos, moved);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    static {
        FACING = HorizontalFacingBlock.FACING;
        LIT = Properties.LIT;
    }
}
