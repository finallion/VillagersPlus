package com.finallion.villagersplus.trade_offer;

import com.finallion.villagersplus.VillagersPlus;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

import java.util.*;
import java.util.stream.Stream;

public class TradeOfferResourceListener extends JsonDataLoader implements IdentifiableResourceReloadListener {

    public TradeOfferResourceListener() {
        super(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create(), "villager_trades");
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(VillagersPlus.MOD_ID,"villager_data_loader");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> loader, ResourceManager manager, Profiler profiler) {
        loader.forEach((identifier, jsonElement) -> {
            if (!jsonElement.isJsonObject()) {
                return;
            }

            TradeOfferManager.deserializeJson(jsonElement.getAsJsonObject());
        });

        // trades from datapacks
        Map<VillagerProfession, Int2ObjectOpenHashMap<TradeOffers.Factory[]>> datapackTrades = TradeOfferRegistryLoader.getRegistryForLoading();

        // merge trades added from datapack with the default trades added via data-json
        TradeOffers.PROFESSION_TO_LEVELED_TRADE.forEach((profession, defaultProfessionTrades) -> {
            Int2ObjectOpenHashMap<TradeOffers.Factory[]> datapackProfessionTrades = datapackTrades.getOrDefault(profession, new Int2ObjectOpenHashMap<>());

            datapackProfessionTrades.forEach((level, datapackLevelTrades) -> { // collect trades per level and per profession from the datapack
                TradeOffers.Factory[] defaultLevelTrades = defaultProfessionTrades.get(level);

                TradeOffers.Factory[] mergedTrades = Stream.concat( // if default trades are available, merge them with the datapack trades
                        (defaultLevelTrades != null) ? Arrays.stream(defaultLevelTrades) : Stream.empty(),
                            Arrays.stream(datapackLevelTrades))
                        .distinct()
                        .toArray(TradeOffers.Factory[]::new);

                defaultProfessionTrades.put(level, mergedTrades);
            });
        });
    }
}