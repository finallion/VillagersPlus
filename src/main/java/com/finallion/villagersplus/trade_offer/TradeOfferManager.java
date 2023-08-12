package com.finallion.villagersplus.trade_offer;

import com.finallion.villagersplus.VillagersPlus;
import com.finallion.villagersplus.trade_offer.tradeOffers.*;
import com.google.gson.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class TradeOfferManager {
    public static final Map<String, Integer> professionMapping = new HashMap<>();
    public static final Map<Identifier, JsonTradeOffer> tradeOfferRegistry = new HashMap<>();

    static {
        professionMapping.put("novice", 1);
        professionMapping.put("apprentice", 2);
        professionMapping.put("journeyman", 3);
        professionMapping.put("expert", 4);
        professionMapping.put("master", 5);
    }

    public static void registerTradeOffers() {
        tradeOfferRegistry.put(new Identifier(VillagersPlus.MOD_ID,"sell_item"), new JsonSellItemTradeOffer());
        tradeOfferRegistry.put(new Identifier(VillagersPlus.MOD_ID,"buy_item"), new JsonBuyItemTradeOffer());
        tradeOfferRegistry.put(new Identifier(VillagersPlus.MOD_ID,"process_item"), new JsonProcessItemTradeOffer());
        tradeOfferRegistry.put(new Identifier(VillagersPlus.MOD_ID,"sell_potion"), new JsonSellPotionTradeOffer());
        tradeOfferRegistry.put(new Identifier(VillagersPlus.MOD_ID,"sell_enchanted_tool"), new JsonSellEnchantedToolTradeOffer());
        tradeOfferRegistry.put(new Identifier(VillagersPlus.MOD_ID,"sell_enchanted_book"), new JsonSellEnchantedBookTradeOffer());
        tradeOfferRegistry.put(new Identifier(VillagersPlus.MOD_ID,"sell_map"), new JsonSellStructureMapTradeOffer());
    }

    public static void deserializeJson(JsonObject jsonRoot) {
        Identifier professionId = Identifier.tryParse(jsonRoot.get("profession").getAsString());
        VillagersPlus.LOGGER.info("Deserializing default trades of profession: " + professionId);

        Registries.VILLAGER_PROFESSION
                .getOrEmpty(professionId)
                .ifPresent(villagerProfession
                        -> deserializeTrades(jsonRoot, (integer, factory)
                        -> TradeOfferRegistryLoader.registerVillagerTrade(villagerProfession, integer, factory)));
    }

    private static void deserializeTrades(@NotNull JsonObject jsonRoot, BiConsumer<Integer, TradeOffers.Factory> tradeConsumer) {
        for (Map.Entry<String, JsonElement> entry : jsonRoot.get("trades").getAsJsonObject().entrySet()) {

            int level = professionMapping.get(entry.getKey());
            JsonArray tradesArray = entry.getValue().getAsJsonArray();

            for (JsonElement tradeElement : tradesArray) {
                JsonObject trade = tradeElement.getAsJsonObject();
                JsonTradeOffer adapter = tradeOfferRegistry.get(Identifier.tryParse(trade.get("type").getAsString()));

                if (adapter == null) {
                    VillagersPlus.LOGGER.error("Trade type: " + trade.get("type").getAsString());
                    VillagersPlus.LOGGER.error("Error in deserializing trades." +
                            "Trade element: " + tradeElement + " and " +
                            "Trade: " + trade + " in " + tradesArray + " is null.");
                } else {
                    tradeConsumer.accept(level, adapter.deserialize(trade));
                }

            }
        }
    }
}
