package com.lion.villagersplus.mixin.neoforge;

import com.lion.villagersplus.util.DuckBucketable;
import net.minecraft.entity.EntityType;
import net.minecraft.item.EntityBucketItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/// Shadows vanilla's `entityType`. NeoForge 21.1 leaves that field alone rather than patching it
/// into a `Supplier`, and a shadow of the wrong name fails at class load, not at build time.
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
