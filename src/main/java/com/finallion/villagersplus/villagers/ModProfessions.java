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
        HORTICULTURIST = VillagerProfession.register("horticulturist", ModPointOfInterestType.HORTICULTURIST_POI, SoundEvents.ENTITY_VILLAGER_WORK_FARMER);
        OCCULTIST = VillagerProfession.register("occultist", ModPointOfInterestType.OCCULTIST_POI, SoundEvents.ENTITY_VILLAGER_WORK_CLERIC);
        OCEANOGRAPHER = VillagerProfession.register("oceanographer", ModPointOfInterestType.OCEANOGRAPHER_POI, SoundEvents.ITEM_BUCKET_FILL);
        ALCHEMIST = VillagerProfession.register("alchemist", ModPointOfInterestType.ALCHEMIST_POI, SoundEvents.ENTITY_VILLAGER_WORK_CLERIC);

    }


}
