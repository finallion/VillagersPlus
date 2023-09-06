package com.lion.villagersplus.config;

import com.lion.villagersplus.config.annotations.Description;

public class VPConfig implements Config {

    @Override
    public String getName() {
        return "villagersplus-3.0-config";
    }

    @Override
    public String getExtension() {
        return "json5";
    }

    @Override
    public String getDirectory() {
        return "villagersplus";
    }

    @Description("Amount of new trades per level a villager can have at max. (Default: 2)")
    public int trade_offers_per_level = 2;
    @Description("Amount of trades the wandering trader can have at max. (Default: 5)")
    public int trade_offers_wandering_trader = 5;

    @Description("Max amount of experience storable in the enchanted basin. (Default: 500)")
    public int max_exp_amount = 500;

    @Description("Amount of experience taken or given to the enchanted basin per interaction. (Default: 50)")
    public int exp_amount = 50;

    @Description("Enable explosion potential of the alchemist workstation. (Default: true)")
    public boolean can_explode = true;

    @Description("Chance of the alchemist workstation NOT exploding (1 in ?). Must be greater than 0! (Default: 3)")
    public int explosion_chance = 3;

    @Description("Weight of the house in the village structure pool. Higher values increase the chance of generating.")
    public int plains_alchemist_weight = 10;
    public int plains_occultist_weight = 10;
    public int plains_horticulturist_weight = 10;
    public int plains_oceanographer_weight = 15;

    @Description("Weight of the house in a village. Higher values increase the chance of generating.")
    public int taiga_alchemist_weight = 10;
    public int taiga_occultist_weight = 10;
    public int taiga_horticulturist_weight = 10;
    public int taiga_oceanographer_weight = 15;

    @Description("Weight of the house in a village. Higher values increase the chance of generating.")
    public int savanna_alchemist_weight = 10;
    public int savanna_occultist_weight = 10;
    public int savanna_horticulturist_weight = 10;
    public int savanna_oceanographer_weight = 15;

    @Description("Weight of the house in a village. Higher values increase the chance of generating.")
    public int snowy_alchemist_weight = 10;
    public int snowy_occultist_weight = 10;
    public int snowy_horticulturist_weight = 10;
    public int snowy_oceanographer_weight = 15;

    @Description("Weight of the house in a village. Higher values increase the chance of generating.")
    public int desert_alchemist_weight = 10;
    public int desert_occultist_weight = 10;
    public int desert_horticulturist_weight = 10;
    public int desert_oceanographer_weight = 15;

}


