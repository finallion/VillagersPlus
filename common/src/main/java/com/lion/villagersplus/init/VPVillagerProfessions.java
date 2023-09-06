package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import com.google.common.collect.ImmutableSet;
import com.lion.villagersplus.init.VPPointOfInterestTypes;
import com.lion.villagersplus.platform.RegistryHelper;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class VPVillagerProfessions {

    public static void init() {
    }

    public static Supplier<VillagerProfession> HORTICULTURIST;
    public static Supplier<VillagerProfession> OCCULTIST;
    public static Supplier<VillagerProfession> OCEANOGRAPHER;
    public static Supplier<VillagerProfession> ALCHEMIST;

    public static final Predicate<RegistryEntry<PointOfInterestType>> HORTICULTURIST_PREDICATE = (registryEntry) -> registryEntry.value() ==
            VPPointOfInterestTypes.HORTICULTURIST_ACACIA_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_BAMBOO_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_BIRCH_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_CHERRY_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_CRIMSON_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_DARK_OAK_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_JUNGLE_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_MANGROVE_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_SPRUCE_POI.get()
                    || registryEntry.value() == VPPointOfInterestTypes.HORTICULTURIST_WARPED_POI.get();

    static {
        HORTICULTURIST = RegistryHelper.registerVillagerProfession("horticulturist", () -> new VillagerProfession(
                VillagersPlus.createStringID("horticulturist"),
                HORTICULTURIST_PREDICATE,
                HORTICULTURIST_PREDICATE,
                ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_FARMER
        ));

        OCCULTIST = RegistryHelper.registerVillagerProfession("occultist", () -> new VillagerProfession(
                VillagersPlus.createStringID("occultist"),
                holder -> holder.value().equals(VPPointOfInterestTypes.OCCULTIST_POI.get()),
                holder -> holder.value().equals(VPPointOfInterestTypes.OCCULTIST_POI.get()),
                ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_CLERIC
        ));

        OCEANOGRAPHER = RegistryHelper.registerVillagerProfession("oceanographer", () -> new VillagerProfession(
                VillagersPlus.createStringID("oceanographer"),
                holder -> holder.value().equals(VPPointOfInterestTypes.OCEANOGRAPHER_POI.get()),
                holder -> holder.value().equals(VPPointOfInterestTypes.OCEANOGRAPHER_POI.get()),
                ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ITEM_BUCKET_FILL
        ));

        ALCHEMIST = RegistryHelper.registerVillagerProfession("alchemist", () -> new VillagerProfession(
                VillagersPlus.createStringID("alchemist"),
                holder -> holder.value().equals(VPPointOfInterestTypes.ALCHEMIST_POI.get()),
                holder -> holder.value().equals(VPPointOfInterestTypes.ALCHEMIST_POI.get()),
                ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_CLERIC
        ));
    }
}
