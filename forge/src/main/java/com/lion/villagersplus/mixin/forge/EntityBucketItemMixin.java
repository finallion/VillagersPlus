package com.lion.villagersplus.mixin.forge;

import com.lion.villagersplus.util.DuckBucketable;
import net.minecraft.entity.EntityType;
import net.minecraft.item.EntityBucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;


@Mixin(EntityBucketItem.class)
public class EntityBucketItemMixin implements DuckBucketable {

    @Shadow
    private Supplier<? extends EntityType<?>> entityTypeSupplier;

    @Override
    public EntityType<?> getEntityType() {
        return entityTypeSupplier.get();
    }
}


