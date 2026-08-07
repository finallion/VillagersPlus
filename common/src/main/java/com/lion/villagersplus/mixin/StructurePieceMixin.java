package com.lion.villagersplus.mixin;

import com.lion.villagersplus.util.VanillaMineshaftAttachment;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
}
