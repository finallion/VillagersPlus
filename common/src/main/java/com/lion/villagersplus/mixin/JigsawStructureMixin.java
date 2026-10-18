package com.lion.villagersplus.mixin;

import com.lion.villagersplus.util.VanillaMineshaftAttachment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

@Mixin(JigsawStructure.class)
public class JigsawStructureMixin {

    /// Grows a vanilla mineshaft off the miner's shaft after the jigsaw run.
    @Inject(method = "findGenerationPoint", at = @At("RETURN"), cancellable = true)
    private void villagersplus$attachVanillaMineshaft(Structure.GenerationContext context, CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        Optional<Structure.GenerationStub> result = cir.getReturnValue();

        if (result.isEmpty()) {
            return;
        }

        Structure.GenerationStub position = result.get();
        Optional<Consumer<StructurePiecesBuilder>> generator = position.generator().left();
        if (generator.isEmpty()) {
            return;
        }

        Consumer<StructurePiecesBuilder> original = generator.get();
        cir.setReturnValue(Optional.of(new Structure.GenerationStub(position.position(), collector -> {
            original.accept(collector);
            VanillaMineshaftAttachment.append(collector, context);
        })));
    }
}
