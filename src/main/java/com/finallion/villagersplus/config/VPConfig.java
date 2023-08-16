package com.finallion.villagersplus.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "villagersplus")
public class VPConfig implements ConfigData {
    @ConfigEntry.Category("vanilla_villager_improvements")
    @Comment("Amount of new trades per level a villager can have at max. (Default: 2)")
    public int trade_offers_per_level = 2;
    @Comment("Amount of trades the wandering trader can have at max. (Default: 5)")
    public int trade_offers_wandering_trader = 5;

    @ConfigEntry.Category("occultist_workstation")
    @Comment("Max amount of experience storable in the enchanted basin.")
    public int max_exp_amount = 500;

    @ConfigEntry.Category("occultist_workstation")
    @Comment("Amount of experience taken or given to the enchanted basin per interaction.")
    public int exp_amount = 50;

    @ConfigEntry.Category("alchemist_workstation")
    @Comment("Enable explosion potential of the alchemist workstation.")
    public boolean can_explode = true;

    @ConfigEntry.Category("alchemist_workstation")
    @Comment("Chance of the alchemist workstation NOT exploding (1 in ?). Must be greater than 0!")
    public int explosion_chance = 3;

    @ConfigEntry.Category("plains_village_pool_weight")
    @Comment("Weight of the house in the village structure pool. Higher values increase the chance of generating.")
    public int plains_alchemist_weight = 10;
    public int plains_occultist_weight = 10;
    public int plains_horticulturist_weight = 10;
    public int plains_oceanographer_weight = 15;

    @ConfigEntry.Category("taiga_village_pool_weight")
    @Comment("Weight of the house in a village. Higher values increase the chance of generating.")
    public int taiga_alchemist_weight = 10;
    public int taiga_occultist_weight = 10;
    public int taiga_horticulturist_weight = 10;
    public int taiga_oceanographer_weight = 15;

    @ConfigEntry.Category("savanna_village_pool_weight")
    @Comment("Weight of the house in a village. Higher values increase the chance of generating.")
    public int savanna_alchemist_weight = 10;
    public int savanna_occultist_weight = 10;
    public int savanna_horticulturist_weight = 10;
    public int savanna_oceanographer_weight = 15;

    @ConfigEntry.Category("snowy_village_pool_weight")
    @Comment("Weight of the house in a village. Higher values increase the chance of generating.")
    public int snowy_alchemist_weight = 10;
    public int snowy_occultist_weight = 10;
    public int snowy_horticulturist_weight = 10;
    public int snowy_oceanographer_weight = 15;

    @ConfigEntry.Category("desert_village_pool_weight")
    @Comment("Weight of the house in a village. Higher values increase the chance of generating.")
    public int desert_alchemist_weight = 10;
    public int desert_occultist_weight = 10;
    public int desert_horticulturist_weight = 10;
    public int desert_oceanographer_weight = 15;
}
