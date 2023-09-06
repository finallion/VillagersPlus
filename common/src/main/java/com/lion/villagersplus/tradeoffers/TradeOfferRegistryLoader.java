package com.lion.villagersplus.tradeoffers;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class TradeOfferRegistryLoader {
    private static final HashMap<VillagerProfession, Int2ObjectOpenHashMap<List<TradeOffers.Factory>>> TRADES_REGISTRY = new HashMap<>();

    public static HashMap<VillagerProfession, Int2ObjectOpenHashMap<TradeOffers.Factory[]>> getRegistryForLoading() {
        HashMap<VillagerProfession, Int2ObjectOpenHashMap<TradeOffers.Factory[]>> villagerTrades = new HashMap<>();

        TRADES_REGISTRY.forEach(((villagerProfession, listInt2ObjectOpenHashMap) -> {
            Int2ObjectOpenHashMap<TradeOffers.Factory[]> factories = villagerTrades.getOrDefault(villagerProfession, new Int2ObjectOpenHashMap<>());

            listInt2ObjectOpenHashMap.forEach((level, factoryList) -> {
                final TradeOffers.Factory[] oldFactories = factories.getOrDefault(level.intValue(), new TradeOffers.Factory[0]);
                factories.put(level.intValue(), ArrayUtils.addAll(oldFactories, factoryList.toArray(new TradeOffers.Factory[0])));
            });

            villagerTrades.put(villagerProfession, factories);
        }));


        return villagerTrades;
    }

    public static void registerVillagerTrade(VillagerProfession profession, int level, TradeOffers.Factory trade) {
        getVillagerTradeList(profession, level).add(trade);
    }

    private static List<TradeOffers.Factory> getVillagerTradeList(VillagerProfession profession, int level) {
        Int2ObjectOpenHashMap<List<TradeOffers.Factory>> villagerMap = getOrDefaultAndAdd(TRADES_REGISTRY, profession, new Int2ObjectOpenHashMap<>());
        return getOrDefaultAndAdd(villagerMap, level, new ArrayList<>());
    }

    public static <K, V> V getOrDefaultAndAdd(Map<K, V> map, K key, V defaultValue) {
        if (map.containsKey(key)) {
            return map.get(key);
        }

        map.put(key, defaultValue);
        return defaultValue;
    }
}
