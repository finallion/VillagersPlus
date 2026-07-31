package com.lion.villagersplus.mixin.neoforge;

import com.lion.villagersplus.util.DuckBucketable;
import net.minecraft.entity.EntityType;
import net.minecraft.item.EntityBucketItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Identical to the Fabric copy now. Forge used to patch {@code EntityBucketItem} to hold a
 * {@code Supplier<? extends EntityType<?>>} called {@code entityTypeSupplier}; NeoForge 21.1 dropped
 * that patch, so the field is vanilla's {@code entityType} on both loaders. Shadowing the old name
 * would have failed at class load, not at build time.
 */
@Mixin(EntityBucketItem.class)
public class EntityBucketItemMixin implements DuckBucketable {

    @Final
    @Shadow
    private EntityType<?> entityType;

    @Override
    public EntityType<?> getEntityType() {
        return entityType;
    }
}
