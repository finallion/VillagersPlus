package com.lion.villagersplus.mixin.fabric;

import com.lion.villagersplus.util.DuckBucketable;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.EntityBucketItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(EntityBucketItem.class)
public class EntityBucketItemMixin implements DuckBucketable {

    @Final
    @Shadow
    private EntityType<?> entityType;

    public EntityType<?> getEntityType() {
        return entityType;
    }
}


