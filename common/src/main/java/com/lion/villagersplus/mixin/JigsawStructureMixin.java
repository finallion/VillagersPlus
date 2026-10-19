package com.lion.villagersplus.mixin;

import com.lion.villagersplus.util.VanillaMineshaftAttachment;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(JigsawStructure.class)
public class JigsawStructureMixin {

    /// Wraps the jigsaw piece generator so a vanilla mineshaft can be grown off the miner's shaft
    /// after the jigsaw run has finished. The wrapper stays lazy - the original consumer is only
    /// invoked when the structure start is actually built, same as without this mixin.
    @Inject(method = "getStructurePosition", at = @At("RETURN"), cancellable = true)
    private void villagersplus$attachVanillaMineshaft(Structure.Context context, CallbackInfoReturnable<Optional<Structure.StructurePosition>> cir) {
        Optional<Structure.StructurePosition> result = cir.getReturnValue();

        if (result.isEmpty()) {
            return;
        }

        Structure.StructurePosition position = result.get();
        Optional<Consumer<StructurePiecesCollector>> generator = position.generator().left();

        if (generator.isEmpty()) {
            return;
        }

        Consumer<StructurePiecesCollector> original = generator.get();
        cir.setReturnValue(Optional.of(new Structure.StructurePosition(position.position(), collector -> {
            original.accept(collector);
            VanillaMineshaftAttachment.append(collector, context);
        })));
    }
}
