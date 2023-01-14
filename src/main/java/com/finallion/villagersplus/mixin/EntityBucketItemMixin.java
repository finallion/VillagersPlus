package com.finallion.villagersplus.mixin;

import com.finallion.villagersplus.util.DuckBucketable;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.EntityBucketItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityBucketItem.class)
public class EntityBucketItemMixin extends BucketItem implements DuckBucketable {

    public EntityBucketItemMixin(Fluid fluid, Settings settings) {
        super(fluid, settings);
    }

    @Final
    @Shadow
    private EntityType<?> entityType;

    public EntityType<?> getEntityType() {
        return entityType;
    }
}
