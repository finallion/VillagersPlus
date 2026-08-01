package com.lion.villagersplus.config;

import com.lion.villagersplus.config.annotations.Description;

public class VPConfig implements Config {

    /**
     * The file name carries the feature line, so it changes with {@code mod_version}. Bumping it
     * means 4.0 writes a fresh file next to the old 3.0 one instead of silently reading a layout that
     * no longer matches - the ten trade settings moved to VillagerTradingPlus in this very release.
     */
    @Override
    public String getName() {
        return "villagersplus-4.0-config";
    }

    @Override
    public String getExtension() {
        return "json5";
    }

    @Override
    public String getDirectory() {
        return "villagersplus";
    }

    // Everything that shapes trades themselves now lives in VillagerTradingPlus' own config - the
    // trade system moved into that library, and its settings apply to every mod feeding it, not just
    // to this one.

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
    public int plains_horticulturist_weight = 7;
    public int plains_oceanographer_weight = 15;
    public int plains_miner_weight = 25;

    @Description("Weight of the house in a village. Higher values increase the chance of generating.")
    public int taiga_alchemist_weight = 10;
    public int taiga_occultist_weight = 10;
    public int taiga_horticulturist_weight = 7;
    public int taiga_oceanographer_weight = 15;
    public int taiga_miner_weight = 25;

    @Description("Weight of the house in a village. Higher values increase the chance of generating.")
    public int savanna_alchemist_weight = 10;
    public int savanna_occultist_weight = 10;
    public int savanna_horticulturist_weight = 7;
    public int savanna_oceanographer_weight = 15;
    public int savanna_miner_weight = 25;

    @Description("Weight of the house in a village. Higher values increase the chance of generating.")
    public int snowy_alchemist_weight = 10;
    public int snowy_occultist_weight = 10;
    public int snowy_horticulturist_weight = 7;
    public int snowy_oceanographer_weight = 15;

    @Description("Weight of the house in a village. Higher values increase the chance of generating.")
    public int desert_alchemist_weight = 10;
    public int desert_occultist_weight = 10;
    public int desert_horticulturist_weight = 7;
    public int desert_oceanographer_weight = 15;

    @Description("When true, the mineshaft below the miner's house ends in a real vanilla mineshaft instead of just dead-ending. (Default: true)")
    public boolean mineshaft_connects_to_vanilla_mineshaft = true;

    @Description("Scales the ore grinder's base yields. 1.0 keeps the defaults: iron/gold/coal/diamond/emerald/quartz ore -> 2, copper -> 3, lapis/redstone -> 8. Examples: 0.5 halves everything (iron ore -> 1 raw iron), 2.0 doubles it (iron ore -> 4 raw iron). Minimum result is always 1 item. (Default: 1.0)")
    public float ore_grinder_output_multiplier = 1.0F;

    @Description("Whether Fortune on the inserted pickaxe additionally increases the ore grinder's yield. (Default: true)")
    public boolean ore_grinder_fortune_enabled = true;

    @Description("Durability the inserted pickaxe loses per ground ore. Unbreaking is honored per durability point. (Default: 3)")
    public int ore_grinder_pickaxe_damage = 3;

    @Description("Two flower positions in the flower tub (absolute X/Z offset from the block, applied per plant).")
    public float first_flower_in_two_X = 0.15F;
    public float first_flower_in_two_Z = 0.15F;

    public float second_flower_in_two_X = -0.15F;
    public float second_flower_in_two_Z = -0.20F;

    @Description("Three flower positions in the flower tub (absolute X/Z offset from the block, applied per plant).")
    public float first_flower_in_three_X = 0.15F;
    public float first_flower_in_three_Z = 0F;

    public float second_flower_in_three_X = -0.15F;
    public float second_flower_in_three_Z = -0.15F;

    public float third_flower_in_three_X = -0.20F;
    public float third_flower_in_three_Z = 0.15F;

    @Description("Four flower positions in the flower tub (absolute X/Z offset from the block, applied per plant).")
    public float first_flower_in_four_X = 0.15F;
    public float first_flower_in_four_Z = 0.15F;

    public float second_flower_in_four_X = 0.15F;
    public float second_flower_in_four_Z = -0.20F;

    public float third_flower_in_four_X = -0.15F;
    public float third_flower_in_four_Z = -0.20F;

    public float forth_flower_in_four_X = -0.20F;
    public float forth_flower_in_four_Z = 0.17F;


}


