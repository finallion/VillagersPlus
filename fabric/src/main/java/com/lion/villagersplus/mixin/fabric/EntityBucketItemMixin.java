package com.lion.villagersplus.mixin.fabric;

import com.lion.villagersplus.util.DuckBucketable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.MobBucketItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MobBucketItem.class)
public class EntityBucketItemMixin implements DuckBucketable {

    @Final
    @Shadow
    private EntityType<?> type;

    public EntityType<?> getEntityType() {
        return type;
    }
}

