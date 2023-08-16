package com.finallion.villagersplus.mixin;

import com.finallion.villagersplus.VillagersPlus;
import net.minecraft.entity.passive.VillagerEntity;

import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {

    @ModifyConstant(method = "fillRecipes", constant = @Constant(intValue = 2))
    private int changeTradeOfferPerLevelCount(int value) {
        return VillagersPlus.CONFIG.trade_offers_per_level;
    }
}
