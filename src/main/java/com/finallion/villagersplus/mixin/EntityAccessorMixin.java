package com.finallion.villagersplus.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AxolotlEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessorMixin {

    @Accessor
    void setTouchingWater(boolean inWater);
}
