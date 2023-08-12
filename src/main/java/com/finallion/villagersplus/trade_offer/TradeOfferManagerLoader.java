package com.finallion.villagersplus.trade_offer;

import com.finallion.villagersplus.VillagersPlus;
import com.finallion.villagersplus.trade_offer.tradeOffers.JsonTradeOffer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TradeOfferManagerLoader implements ResourceReloader, IdentifiableResourceReloadListener {
    private final Map<String, Map<String, List<TradeOffers.Factory>>> mergedTradeOffers = new HashMap<>();

    public TradeOfferManagerLoader() {
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return CompletableFuture.runAsync(() -> {
            mergedTradeOffers.clear();

            for (ResourcePack pack : manager.streamResourcePacks().collect(Collectors.toList())) {

                    //loadAndMergeTradeOffers(pack);

            }
        }, applyExecutor);
    }

    private void loadAndMergeTradeOffers(ResourcePack pack) throws IOException {
        InputSupplier<InputStream> tradeInputStreamSupplier = pack.open(ResourceType.SERVER_DATA, new Identifier(VillagersPlus.MOD_ID, "villager_trades/horticulturist.json"));

        if (tradeInputStreamSupplier == null || tradeInputStreamSupplier.get() == null) {
            return;
        }

        JsonObject tradeData = new Gson().fromJson(new InputStreamReader(tradeInputStreamSupplier.get()), JsonObject.class);

        Identifier professionId = Identifier.tryParse(tradeData.get("profession").getAsString());

        VillagersPlus.LOGGER.info("Deserializing datapack added trades of profession: " + professionId);

        for (Map.Entry<String, JsonElement> entry : tradeData.get("trades").getAsJsonObject().entrySet()) {

            int level = TradeOfferManager.professionMapping.get(entry.getKey());
            JsonArray tradesArray = entry.getValue().getAsJsonArray();

            for (JsonElement tradeElement : tradesArray) {
                JsonObject trade = tradeElement.getAsJsonObject();
                JsonTradeOffer adapter = TradeOfferManager.tradeOfferRegistry.get(Identifier.tryParse(trade.get("type").getAsString()));

                if (adapter == null) {
                    VillagersPlus.LOGGER.error("Trade type: " + trade.get("type").getAsString());
                    VillagersPlus.LOGGER.error("Error in deserializing trades." +
                            "Trade element: " + tradeElement + " and " +
                            "Trade: " + trade + " in " + tradesArray + " is null.");
                } else {
                    Optional<VillagerProfession> profession = Registries.VILLAGER_PROFESSION.getOrEmpty(professionId);
                    profession.ifPresent(villagerProfession -> TradeOfferRegistryLoader.registerVillagerTrade(villagerProfession, level,  adapter.deserialize(trade)));
                }

            }
        }

        tradeInputStreamSupplier.get().close();
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(VillagersPlus.MOD_ID,"datapack-merger");
    }
}
