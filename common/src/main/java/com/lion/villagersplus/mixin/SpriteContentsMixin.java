package com.lion.villagersplus.mixin;

import com.mojang.blaze3d.platform.Transparency;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpriteContents.class)
public abstract class SpriteContentsMixin {

    @Inject(method = "computeTransparency", at = @At("RETURN"), cancellable = true)
    private void villagersplus$keepExperienceFillingCutout(
            float minU,
            float minV,
            float maxU,
            float maxV,
            CallbackInfoReturnable<Transparency> cir
    ) {
        SpriteContents self = (SpriteContents) (Object) this;

        if (self.name().toString().equals("villagersplus:block/experience_filling") && cir.getReturnValue().hasTranslucent()) {
            cir.setReturnValue(Transparency.TRANSPARENT);
        }
    }
}
