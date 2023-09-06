package com.lion.villagersplus.platform.forge;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.tradeoffers.TradeOfferManager;
import com.lion.villagersplus.tradeoffers.TradeOfferRegistryLoader;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.village.TradeOffers;

import java.util.Map;

public class DefaultTradeOfferResourceListener extends JsonDataLoader implements ResourceReloader {

    public DefaultTradeOfferResourceListener() {
        super(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create(), "default_villager_trades");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> loader, ResourceManager manager, Profiler profiler) {
        loader.forEach((identifier, jsonElement) -> {
            if (!jsonElement.isJsonObject()) {
                return;
            }

            VillagersPlus.LOGGER.info("Deserializing default trades of profession: " + jsonElement.getAsJsonObject().get("profession").getAsString());

            TradeOfferManager.deserializeJson(jsonElement.getAsJsonObject());
        });

        TradeOffers.PROFESSION_TO_LEVELED_TRADE.putAll(TradeOfferRegistryLoader.getRegistryForLoading());
    }
}