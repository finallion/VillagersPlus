package com.lion.villagersplus.mixin;

import com.lion.villagersplus.util.VanillaMineshaftAttachment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// Keeps the miner's shaft and its vanilla mineshaft out of the village's terrain adaptation.
///
/// Pieces in a village inherit `beard_thin`. `StructureWeightSampler` reads it from the structure start.
/// The sampler skips a piece that fails `intersectsChunk`, so returning false skips the adaptation.
@Mixin(StructurePiece.class)
public class StructurePieceMixin {

    @Inject(method = "isCloseToChunk", at = @At("HEAD"), cancellable = true)
    private void villagersplus$skipTerrainAdaptation(ChunkPos pos, int offset, CallbackInfoReturnable<Boolean> cir) {
        if (VanillaMineshaftAttachment.isBeardExempt((StructurePiece) (Object) this)) {
            cir.setReturnValue(false);
        }
    }

    /// Skips post-processing marks on finished chunks. They only log a warning.
    @WrapOperation(
            method = "placeBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/ChunkAccess;markPosForPostprocessing(Lnet/minecraft/core/BlockPos;)V"
            )
    )
    private void villagersplus$onlyPostProcessUnfinishedChunks(ChunkAccess chunk, BlockPos pos, Operation<Void> original) {
        if (chunk instanceof ProtoChunk) {
            original.call(chunk, pos);
        }
    }
}
