package com.lion.villagersplus.mixin;

import com.lion.villagersplus.VillagersPlus;
import net.minecraft.entity.passive.WanderingTraderEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WanderingTraderEntity.class)
public class WanderingTraderMixin {

    @ModifyConstant(method = "fillRecipes", constant = @Constant(intValue = 5))
    private int changeTradeOfferCount(int value) {
        return VillagersPlus.CONFIG.trade_offers_wandering_trader;
    }

}
