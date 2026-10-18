package com.lion.villagersplus.mixin;

import net.minecraft.world.entity.animal.fish.TropicalFish;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TropicalFish.class)
public interface TropicalFishEntityInvoker {

    @Invoker("setPackedVariant")
    public void setTropicalFishVariantMixin(int variant);
}
