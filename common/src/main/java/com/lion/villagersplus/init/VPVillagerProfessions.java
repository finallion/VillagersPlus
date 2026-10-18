package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import com.google.common.collect.ImmutableSet;
import com.lion.villagersplus.init.VPPointOfInterestTypes;
import com.lion.villagersplus.platform.RegistryHelper;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;

public class VPVillagerProfessions {

    public static void init() {
    }

    public static Supplier<VillagerProfession> HORTICULTURIST;
    public static Supplier<VillagerProfession> OCCULTIST;
    public static Supplier<VillagerProfession> OCEANOGRAPHER;
    public static Supplier<VillagerProfession> ALCHEMIST;
    public static Supplier<VillagerProfession> MINER;

    public static final Predicate<Holder<PoiType>> HORTICULTURIST_PREDICATE = (registryEntry) -> registryEntry.value() ==
            VPPointOfInterestTypes.HORTICULTURIST_ACACIA_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_BAMBOO_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_OAK_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_BIRCH_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_CHERRY_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_CRIMSON_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_DARK_OAK_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_JUNGLE_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_MANGROVE_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_SPRUCE_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_PALE_OAK_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_WARPED_POI.get();

    private static Int2ObjectMap<ResourceKey<TradeSet>> tradeSets(String profession) {
        Int2ObjectMap<ResourceKey<TradeSet>> sets = new Int2ObjectOpenHashMap<>();
        for (int level = 1; level <= 5; level++) {
            sets.put(level, ResourceKey.create(Registries.TRADE_SET,
                    Identifier.fromNamespaceAndPath(VillagersPlus.MOD_ID, profession + "/level_" + level)));
        }
        return sets;
    }

    static {
        HORTICULTURIST = RegistryHelper.registerVillagerProfession("horticulturist", () -> new VillagerProfession(
                Component.translatable("entity.minecraft.villager.horticulturist"),
                HORTICULTURIST_PREDICATE,
                HORTICULTURIST_PREDICATE,
                ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_FARMER,
                tradeSets("horticulturist")
        ));

        OCCULTIST = RegistryHelper.registerVillagerProfession("occultist", () -> new VillagerProfession(
                Component.translatable("entity.minecraft.villager.occultist"),
                holder -> holder.value().equals(VPPointOfInterestTypes.OCCULTIST_POI.get()),
                holder -> holder.value().equals(VPPointOfInterestTypes.OCCULTIST_POI.get()),
                ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_CLERIC,
                tradeSets("occultist")
        ));

        OCEANOGRAPHER = RegistryHelper.registerVillagerProfession("oceanographer", () -> new VillagerProfession(
                Component.translatable("entity.minecraft.villager.oceanographer"),
                holder -> holder.value().equals(VPPointOfInterestTypes.OCEANOGRAPHER_POI.get()),
                holder -> holder.value().equals(VPPointOfInterestTypes.OCEANOGRAPHER_POI.get()),
                ImmutableSet.of(), ImmutableSet.of(), SoundEvents.BUCKET_FILL,
                tradeSets("oceanographer")
        ));

        ALCHEMIST = RegistryHelper.registerVillagerProfession("alchemist", () -> new VillagerProfession(
                Component.translatable("entity.minecraft.villager.alchemist"),
                holder -> holder.value().equals(VPPointOfInterestTypes.ALCHEMIST_POI.get()),
                holder -> holder.value().equals(VPPointOfInterestTypes.ALCHEMIST_POI.get()),
                ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_CLERIC,
                tradeSets("alchemist")
        ));

        MINER = RegistryHelper.registerVillagerProfession("miner", () -> new VillagerProfession(
                Component.translatable("entity.minecraft.villager.miner"),
                holder -> holder.value().equals(VPPointOfInterestTypes.MINER_POI.get()),
                holder -> holder.value().equals(VPPointOfInterestTypes.MINER_POI.get()),
                ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_TOOLSMITH,
                tradeSets("miner")
        ));
    }
}
