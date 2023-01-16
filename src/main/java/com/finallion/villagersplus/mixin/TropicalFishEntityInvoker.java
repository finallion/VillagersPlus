package com.finallion.villagersplus.mixin;

import net.minecraft.entity.passive.TropicalFishEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TropicalFishEntity.class)
public interface TropicalFishEntityInvoker {

    @Invoker("setTropicalFishVariant")
    public void setTropicalFishVariantMixin(int variant);
}
