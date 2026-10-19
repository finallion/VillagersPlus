package com.lion.villagersplus.config;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.config.annotations.Description;

import java.lang.reflect.Field;

public class VPConfig implements Config {

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

    // Trade settings live in the VillagerTradingPlus config.

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

    @Override
    public void validate() {
        max_exp_amount = clamp("max_exp_amount", max_exp_amount, 1, 100000);
        exp_amount = clamp("exp_amount", exp_amount, 1, max_exp_amount);
        explosion_chance = clamp("explosion_chance", explosion_chance, 1, Integer.MAX_VALUE);
        ore_grinder_pickaxe_damage = clamp("ore_grinder_pickaxe_damage", ore_grinder_pickaxe_damage, 0, 100);
        ore_grinder_output_multiplier = clampMultiplier(ore_grinder_output_multiplier);

        for (Field field : VPConfig.class.getFields()) {
            if (field.getName().endsWith("_weight") && field.getType() == int.class) {
                clampWeight(field);
            }
        }
    }

    private static int clamp(String name, int value, int min, int max) {
        int clamped = Math.max(min, Math.min(max, value));

        if (clamped != value) {
            VillagersPlus.LOGGER.warn("Config value {} = {} is out of range {}..{}, using {}.", name, value, min, max, clamped);
        }

        return clamped;
    }

    private static float clampMultiplier(float value) {
        float clamped = Float.isNaN(value) || Float.isInfinite(value) ? 1.0F : Math.max(0.0F, Math.min(64.0F, value));

        if (Float.compare(clamped, value) != 0) {
            VillagersPlus.LOGGER.warn("Config value ore_grinder_output_multiplier = {} is out of range 0..64, using {}.", value, clamped);
        }

        return clamped;
    }

    private void clampWeight(Field field) {
        try {
            field.setInt(this, clamp(field.getName(), field.getInt(this), 0, 100));
        } catch (IllegalAccessException e) {
            VillagersPlus.LOGGER.warn("Cannot validate config value {}.", field.getName());
        }
    }

}

