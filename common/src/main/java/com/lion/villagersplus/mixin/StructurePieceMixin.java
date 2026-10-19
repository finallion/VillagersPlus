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
/// Pieces in a village inherit `beard_thin`. `StructureWeightSampler` reads it from the structure start.
/// The sampler skips a piece that fails `intersectsChunk`, so returning false skips the adaptation.
@Mixin(StructurePiece.class)
public class StructurePieceMixin {

    @Inject(method = "intersectsChunk", at = @At("HEAD"), cancellable = true)
    private void villagersplus$skipTerrainAdaptation(ChunkPos pos, int offset, CallbackInfoReturnable<Boolean> cir) {
        if (VanillaMineshaftAttachment.isBeardExempt((StructurePiece) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}
