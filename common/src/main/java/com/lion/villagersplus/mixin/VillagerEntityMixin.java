package com.lion.villagersplus.mixin;

import com.lion.villagersplus.VillagersPlus;
import net.minecraft.entity.passive.VillagerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;


@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {

    @ModifyConstant(method = "fillRecipes", constant = @Constant(intValue = 2))
    private int changeTradeOfferPerLevelCount(int value) {
        return VillagersPlus.CONFIG.trade_offers_per_level;
    }

    /*
    @ModifyConstant(method = "readCustomDataFromNbt", constant = @Constant(intValue = 10, ordinal = 1))
    private int changeTradeOfferCount(int value) {
        return VillagersPlus.CONFIG.trade_offer_amount;
    }

        @ModifyVariable(method = "readCustomDataFromNbt", at = @At("HEAD"), argsOnly = true)
    private NbtCompound modifyNBT(NbtCompound nbtCompound) {
        nbtCompound.putInt("Offers", VillagersPlus.CONFIG.trade_offer_amount);
        return nbtCompound;
    }


     */

}
