package com.finallion.villagersplus.villagers;

import com.finallion.villagersplus.VillagersPlus;
import com.google.common.collect.ImmutableSet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;

public class ModProfessions {

    public static VillagerProfession HORTICULTURIST;
    public static VillagerProfession OCCULTIST;
    public static VillagerProfession OCEANOGRAPHER;
    public static VillagerProfession ALCHEMIST;

    public static void registerProfessions() {
        HORTICULTURIST = Registry.register(Registry.VILLAGER_PROFESSION, new Identifier(VillagersPlus.MOD_ID, "horticulturist"),
                new VillagerProfession("horticulturist", holder -> holder.value().equals(ModPointOfInterestType.HORTICULTURIST_POI), holder -> holder.value().equals(ModPointOfInterestType.HORTICULTURIST_POI), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_FARMER));
        OCCULTIST = Registry.register(Registry.VILLAGER_PROFESSION, new Identifier(VillagersPlus.MOD_ID, "occultist"),
                new VillagerProfession("occultist", holder -> holder.value().equals(ModPointOfInterestType.OCCULTIST_POI), holder -> holder.value().equals(ModPointOfInterestType.OCCULTIST_POI), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_CLERIC));
        OCEANOGRAPHER = Registry.register(Registry.VILLAGER_PROFESSION, new Identifier(VillagersPlus.MOD_ID, "oceanographer"),
                new VillagerProfession("oceanographer", holder -> holder.value().equals(ModPointOfInterestType.OCEANOGRAPHER_POI), holder -> holder.value().equals(ModPointOfInterestType.OCEANOGRAPHER_POI), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ITEM_BUCKET_FILL));
        ALCHEMIST = Registry.register(Registry.VILLAGER_PROFESSION, new Identifier(VillagersPlus.MOD_ID, "alchemist"),
                new VillagerProfession("alchemist", holder -> holder.value().equals(ModPointOfInterestType.ALCHEMIST_POI), holder -> holder.value().equals(ModPointOfInterestType.ALCHEMIST_POI), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_CLERIC));

    }


}
