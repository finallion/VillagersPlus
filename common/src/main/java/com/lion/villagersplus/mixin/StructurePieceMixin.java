package com.lion.villagersplus.mixin;

import com.lion.villagersplus.util.VanillaMineshaftAttachment;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Keeps the miner's shaft and its vanilla mineshaft out of the village's terrain adaptation.
 * <p>
 * {@code StructureWeightSampler} reads the adaptation from the structure <i>start</i>, not from the
 * piece, and adds every non-pool child unconditionally. Our pieces live inside a village start, so
 * they inherit {@code beard_thin} - which carves air up to twelve blocks above each piece box and
 * packs terrain below it. Underground that turns the whole mineshaft into one long cavern: the
 * corridor walls are gone before the piece even runs, so all that is left of it is the plank floor
 * {@code tryPlaceFloor} lays down over the hole and the rails on top.
 * <p>
 * {@code intersectsChunk} is the sampler's own filter and, as of 1.20.1, its only caller in the
 * entire game - so returning false is exactly "do not adapt terrain for this piece" and nothing
 * else. Vanilla mineshafts are unaffected either way: their structure declares no adaptation, so
 * {@code NONE} keeps those starts out of the sampler before a piece is ever looked at.
 */
@Mixin(StructurePiece.class)
public class StructurePieceMixin {

    @Inject(method = "intersectsChunk", at = @At("HEAD"), cancellable = true)
    private void villagersplus$skipTerrainAdaptation(ChunkPos pos, int offset, CallbackInfoReturnable<Boolean> cir) {
        if (VanillaMineshaftAttachment.isBeardExempt((StructurePiece) (Object) this)) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Silences the "Trying to mark a block for PostProcessing … but this operation is not supported"
     * flood that the miner's mineshaft produces.
     * <p>
     * {@code addBlock} marks every block from {@code BLOCKS_NEEDING_POST_PROCESSING} - fences,
     * torches, ladders, rails, i.e. most of a mineshaft corridor - for post-processing. That list
     * only exists while a chunk is still being generated: {@link ProtoChunk} keeps it,
     * {@code WrapperProtoChunk} deliberately drops it, and the base {@link Chunk} implementation
     * does nothing but log a warning. Our mineshaft hangs off a <em>village</em> start, so it reaches
     * chunks that have already left the generation pipeline and are plain {@code WorldChunk}s by the
     * time the village places - one warning per fence post, thousands per village.
     * <p>
     * Skipping the call there is the same outcome vanilla already produces (the mark is discarded
     * either way, the block is placed regardless), minus the log noise. Anything still in the
     * pipeline is passed through untouched, so no real post-processing is lost.
     */
    @Redirect(
            method = "addBlock",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/Chunk;markBlockForPostProcessing(Lnet/minecraft/util/math/BlockPos;)V"))
    private void villagersplus$onlyPostProcessUnfinishedChunks(Chunk chunk, BlockPos pos) {
        if (chunk instanceof ProtoChunk) {
            chunk.markBlockForPostProcessing(pos);
        }
    }
}
