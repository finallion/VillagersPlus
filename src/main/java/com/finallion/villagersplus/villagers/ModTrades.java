package com.finallion.villagersplus.villagers;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffers;

public class ModTrades {
    // Swamp: frog, lily pads, mangrove stuff, mud, mud bricks,

    public static void registerTradeOffers() {
        TradeOfferHelper.registerVillagerOffers(ModProfessions.HORTICULTURIST, 1, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.OXEYE_DAISY, 2, 1, 1));
            factories.add(new TradeOffers.SellItemFactory(Items.POPPY, 2, 1, 1));
            factories.add(new TradeOffers.SellItemFactory(Items.ALLIUM, 2, 1, 1));
            factories.add(new TradeOffers.SellItemFactory(Items.AZURE_BLUET, 2, 1, 1));
            factories.add(new TradeOffers.SellItemFactory(Items.CORNFLOWER, 2, 1, 1));
            factories.add(new TradeOffers.SellItemFactory(Items.LILY_OF_THE_VALLEY, 2, 1, 1));
            factories.add(new TradeOffers.SellItemFactory(Items.DANDELION, 2, 1, 1));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.WHEAT_SEEDS, 1, 8, 1));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.BEETROOT_SEEDS, 1, 12, 1));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.MELON_SEEDS, 1, 12, 2));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.PUMPKIN_SEEDS, 1, 12, 2));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.HORTICULTURIST, 2, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.SPRUCE_LEAVES, 4, 2, 3));
            factories.add(new TradeOffers.SellItemFactory(Items.BIRCH_LEAVES, 4, 2, 3));
            factories.add(new TradeOffers.SellItemFactory(Items.ACACIA_LEAVES, 4, 2, 3));
            factories.add(new TradeOffers.SellItemFactory(Items.AZALEA_LEAVES, 4, 2, 3));
            factories.add(new TradeOffers.SellItemFactory(Items.DARK_OAK_LEAVES, 4, 2, 3));
            factories.add(new TradeOffers.SellItemFactory(Items.JUNGLE_LEAVES, 4, 2, 3));
            factories.add(new TradeOffers.SellItemFactory(Items.MANGROVE_LEAVES, 4, 2, 3));
            factories.add(new TradeOffers.SellItemFactory(Items.OAK_LEAVES, 4, 2, 3));
            factories.add(new TradeOffers.ProcessItemFactory(Items.STICK, 3, Items.DEAD_BUSH, 1, 8, 4));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.GRASS, 4, 12, 3));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.FERN, 2, 12, 4));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.HORTICULTURIST, 3, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.ORANGE_TULIP, 4, 1, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.RED_TULIP, 4, 1, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.WHITE_TULIP, 4, 1, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.PINK_TULIP, 4, 1, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.FLOWER_POT, 5, 1, 4));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.SPRUCE_SAPLING, 2, 12, 3));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.ACACIA_SAPLING, 2, 12, 3));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.BIRCH_SAPLING, 2, 12, 3));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.DARK_OAK_SAPLING, 2, 12, 3));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.JUNGLE_SAPLING, 2, 12, 3));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.OAK_SAPLING, 2, 12, 3));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.HORTICULTURIST, 4, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.SUNFLOWER, 5, 1, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.ROSE_BUSH, 5, 1, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.LILAC, 5, 1, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.PEONY, 5, 1, 4));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.ROOTED_DIRT, 2, 32, 2));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.PODZOL, 2, 32, 2));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.HORTICULTURIST, 5, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.BLUE_ORCHID, 4, 1, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.WITHER_ROSE, 8, 1, 5));
            factories.add(new TradeOffers.SellItemFactory(Items.FLOWERING_AZALEA_LEAVES, 4, 2, 2));
            factories.add(new TradeOffers.SellItemFactory(Items.SPORE_BLOSSOM, 12, 1, 4));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.MOSS_BLOCK, 2, 12, 2));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.BONE_MEAL, 4, 12, 2));
        });




        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCCULTIST, 1, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.SMALL_AMETHYST_BUD, 1, 1, 1));
            factories.add(new TradeOffers.SellItemFactory(Items.MEDIUM_AMETHYST_BUD, 2, 1, 1));
            factories.add(new TradeOffers.SellItemFactory(Items.LARGE_AMETHYST_BUD, 3, 1, 2));
            factories.add(new TradeOffers.SellItemFactory(Items.AMETHYST_CLUSTER, 5, 1, 3));
            factories.add(new TradeOffers.SellItemFactory(Items.TINTED_GLASS, 8, 4, 4));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.AMETHYST_SHARD, 3, 12, 4));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.GLOW_INK_SAC, 1, 12, 4));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCCULTIST, 2, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.SOUL_LANTERN, 4, 1, 3));
            factories.add(new TradeOffers.SellItemFactory(Items.SOUL_TORCH, 2, 1, 3));
            factories.add(new TradeOffers.SellItemFactory(Items.GLOWSTONE_DUST, 4, 2, 2));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.SOUL_SAND, 2, 12, 4));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.SOUL_SOIL, 2, 12, 4));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCCULTIST, 3, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.RED_CANDLE, 4, 2, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.ORANGE_CANDLE, 4, 2, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.YELLOW_CANDLE, 4, 2, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.LIME_CANDLE, 4, 2, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.GREEN_CANDLE, 4, 2, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.LIGHT_BLUE_CANDLE, 4, 2, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.BLUE_CANDLE, 4, 2, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.CYAN_CANDLE, 4, 2, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.MAGENTA_CANDLE, 4, 2, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.PINK_CANDLE, 4, 2, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.PURPLE_CANDLE, 4, 2, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.WHITE_CANDLE, 4, 2, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.GRAY_CANDLE, 4, 2, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.LIGHT_GRAY_CANDLE, 4, 2, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.BLACK_CANDLE, 4, 2, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.BROWN_CANDLE, 4, 2, 4));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCCULTIST, 4, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.QUARTZ, 4, 2, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.SPECTRAL_ARROW, 2, 2, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.CANDLE, 4, 2, 4));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCCULTIST, 5, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.EXPERIENCE_BOTTLE, 3, 1, 5));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.POISONOUS_POTATO, 1, 12, 10));
        });




        TradeOfferHelper.registerVillagerOffers(ModProfessions.ALCHEMIST, 1, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.SUGAR, 3, 1, 1));
            factories.add(new TradeOffers.SellItemFactory(Items.BROWN_MUSHROOM, 3, 1, 1));
            factories.add(new TradeOffers.SellItemFactory(Items.RED_MUSHROOM, 3, 1, 1));
            factories.add(new TradeOffers.SellItemFactory(Items.NETHER_WART, 5, 1, 2));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.GLASS_BOTTLE, 3, 12, 2));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.ALCHEMIST, 2, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.MAGMA_CREAM, 4, 1, 3));
            factories.add(new TradeOffers.SellItemFactory(Items.PUFFERFISH, 5, 1, 3));
            factories.add(new TradeOffers.SellItemFactory(Items.FERMENTED_SPIDER_EYE, 5, 1, 3));
            factories.add(new SellPotionItemFactory(Items.POTION, Items.POTION, 5, 4, 5));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.ALCHEMIST, 3, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.BLAZE_POWDER, 5, 1, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.GHAST_TEAR, 5, 1, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.GUNPOWDER, 3, 1, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.PHANTOM_MEMBRANE, 5, 1, 4));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.BLAZE_ROD, 1, 12, 5));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.ENDER_PEARL, 1, 12, 5));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.ALCHEMIST, 4, factories -> {
            factories.add(new SellPotionItemFactory(Items.POTION, Items.SPLASH_POTION, 8, 4, 6));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.ALCHEMIST, 5, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.DRAGON_BREATH, 8, 1, 7));
            factories.add(new TradeOffers.SellItemFactory(Items.GLISTERING_MELON_SLICE, 4, 1, 6));
            factories.add(new TradeOffers.SellItemFactory(Items.RABBIT_FOOT, 4, 1, 6));
        });



        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCEANOGRAPHER, 1, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.SEA_PICKLE, 3, 1, 1));
            factories.add(new TradeOffers.SellItemFactory(Items.WATER_BUCKET, 5, 1, 5));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.SEAGRASS, 4, 12, 1));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.KELP, 6, 12, 1));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCEANOGRAPHER, 2, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.BRAIN_CORAL, 3, 1, 3));
            factories.add(new TradeOffers.SellItemFactory(Items.BUBBLE_CORAL, 3, 1, 3));
            factories.add(new TradeOffers.SellItemFactory(Items.FIRE_CORAL, 3, 1, 3));
            factories.add(new TradeOffers.SellItemFactory(Items.HORN_CORAL, 3, 1, 3));
            factories.add(new TradeOffers.SellItemFactory(Items.TUBE_CORAL, 3, 1, 3));
            factories.add(new TradeOffers.ProcessItemFactory(Items.DEAD_TUBE_CORAL, 1, Items.TUBE_CORAL, 1, 8, 3));
            factories.add(new TradeOffers.ProcessItemFactory(Items.DEAD_BRAIN_CORAL, 1, Items.BRAIN_CORAL, 1, 8, 3));
            factories.add(new TradeOffers.ProcessItemFactory(Items.DEAD_BUBBLE_CORAL, 1, Items.BUBBLE_CORAL, 1, 8, 3));
            factories.add(new TradeOffers.ProcessItemFactory(Items.DEAD_HORN_CORAL, 1, Items.HORN_CORAL, 1, 8, 3));
            factories.add(new TradeOffers.ProcessItemFactory(Items.DEAD_FIRE_CORAL, 1, Items.FIRE_CORAL, 1, 8, 3));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCEANOGRAPHER, 3, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.BRAIN_CORAL_FAN, 3, 1, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.BUBBLE_CORAL_FAN, 3, 1, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.FIRE_CORAL_FAN, 3, 1, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.HORN_CORAL_FAN, 3, 1, 4));
            factories.add(new TradeOffers.SellItemFactory(Items.TUBE_CORAL_FAN, 3, 1, 4));
            factories.add(new TradeOffers.ProcessItemFactory(Items.DEAD_TUBE_CORAL_FAN, 1, Items.TUBE_CORAL_FAN, 1, 8, 5));
            factories.add(new TradeOffers.ProcessItemFactory(Items.DEAD_BRAIN_CORAL_FAN, 1, Items.BRAIN_CORAL_FAN, 1, 8, 5));
            factories.add(new TradeOffers.ProcessItemFactory(Items.DEAD_BUBBLE_CORAL_FAN, 1, Items.BUBBLE_CORAL_FAN, 1, 8, 5));
            factories.add(new TradeOffers.ProcessItemFactory(Items.DEAD_HORN_CORAL_FAN, 1, Items.HORN_CORAL_FAN, 1, 8, 5));
            factories.add(new TradeOffers.ProcessItemFactory(Items.DEAD_FIRE_CORAL_FAN, 1, Items.FIRE_CORAL_FAN, 1, 8, 5));

        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCEANOGRAPHER, 4, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.BRAIN_CORAL_BLOCK, 5, 1, 5));
            factories.add(new TradeOffers.SellItemFactory(Items.BUBBLE_CORAL_BLOCK, 5, 1, 5));
            factories.add(new TradeOffers.SellItemFactory(Items.FIRE_CORAL_BLOCK, 5, 1, 5));
            factories.add(new TradeOffers.SellItemFactory(Items.HORN_CORAL_BLOCK, 5, 1, 5));
            factories.add(new TradeOffers.SellItemFactory(Items.TUBE_CORAL_BLOCK, 5, 1, 5));
            factories.add(new TradeOffers.ProcessItemFactory(Items.DEAD_TUBE_CORAL_BLOCK, 1, Items.TUBE_CORAL_BLOCK, 1, 8, 5));
            factories.add(new TradeOffers.ProcessItemFactory(Items.DEAD_BRAIN_CORAL_BLOCK, 1, Items.BRAIN_CORAL_BLOCK, 1, 8, 5));
            factories.add(new TradeOffers.ProcessItemFactory(Items.DEAD_BUBBLE_CORAL_BLOCK, 1, Items.BUBBLE_CORAL_BLOCK, 1, 8, 5));
            factories.add(new TradeOffers.ProcessItemFactory(Items.DEAD_HORN_CORAL_BLOCK, 1, Items.HORN_CORAL_BLOCK, 1, 8, 5));
            factories.add(new TradeOffers.ProcessItemFactory(Items.DEAD_FIRE_CORAL_BLOCK, 1, Items.FIRE_CORAL_BLOCK, 1, 8, 5));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCEANOGRAPHER, 5, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.NAUTILUS_SHELL, 6, 1, 15));
            factories.add(new TradeOffers.SellItemFactory(Items.SPONGE, 8, 1, 10));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.SCUTE, 2, 8, 8));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.TROPICAL_FISH_BUCKET, 1, 8, 4));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.AXOLOTL_BUCKET, 1, 8, 4));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.PUFFERFISH_BUCKET, 1, 8, 4));
        });
    }
}
