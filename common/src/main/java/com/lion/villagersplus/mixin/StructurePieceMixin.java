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

/// Keeps the miner's shaft and its vanilla mineshaft out of the village's terrain adaptation.
///
/// `StructureWeightSampler` reads the adaptation from the structure *start*, not from the piece, so
/// pieces living inside a village start inherit `beard_thin`, which carves air above each piece box
/// and packs terrain below it. Underground that turns the mineshaft into one long cavern, leaving
/// only the plank floor and its rails.
///
/// `intersectsChunk` is the sampler's own filter and its only caller in the game, so returning false
/// means exactly "do not adapt terrain for this piece". Vanilla mineshafts declare no adaptation and
/// never reach the sampler, so they are unaffected either way.
@Mixin(StructurePiece.class)
public class StructurePieceMixin {

    @Inject(method = "intersectsChunk", at = @At("HEAD"), cancellable = true)
    private void villagersplus$skipTerrainAdaptation(ChunkPos pos, int offset, CallbackInfoReturnable<Boolean> cir) {
        if (VanillaMineshaftAttachment.isBeardExempt((StructurePiece) (Object) this)) {
            cir.setReturnValue(false);
        }
    }

    /// Silences the "Trying to mark a block for PostProcessing" flood the miner's mineshaft produces.
    ///
    /// `addBlock` marks fences, torches, ladders and rails, most of a corridor, for post-processing,
    /// and only a chunk still in the generation pipeline can hold that list; on a finished chunk the
    /// base [Chunk] does nothing but log. Hanging off a *village* start, this mineshaft reaches
    /// chunks that are already plain `WorldChunk`s: one warning per fence post.
    ///
    /// Skipping the call there is the outcome vanilla produces anyway, since the mark is discarded
    /// and the block placed regardless. Chunks still in the pipeline pass through untouched.
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
