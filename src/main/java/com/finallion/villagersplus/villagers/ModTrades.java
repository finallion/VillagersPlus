package com.finallion.villagersplus.villagers;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ModTrades {
    // Swamp: frog, lily pads, mangrove stuff, mud, mud bricks,

    public static void registerTradeOffers() {
        TradeOfferHelper.registerVillagerOffers(ModProfessions.HORTICULTURIST, 1, factories -> {
            factories.add(new SellItemFactory(Items.OXEYE_DAISY, 2, 1, 1));
            factories.add(new SellItemFactory(Items.POPPY, 2, 1, 1));
            factories.add(new SellItemFactory(Items.ALLIUM, 2, 1, 1));
            factories.add(new SellItemFactory(Items.AZURE_BLUET, 2, 1, 1));
            factories.add(new SellItemFactory(Items.CORNFLOWER, 2, 1, 1));
            factories.add(new SellItemFactory(Items.LILY_OF_THE_VALLEY, 2, 1, 1));
            factories.add(new SellItemFactory(Items.DANDELION, 2, 1, 1));
            factories.add(new BuyForOneEmeraldFactory(Items.WHEAT_SEEDS, 1, 8, 1));
            factories.add(new BuyForOneEmeraldFactory(Items.BEETROOT_SEEDS, 1, 12, 1));
            factories.add(new BuyForOneEmeraldFactory(Items.MELON_SEEDS, 1, 12, 2));
            factories.add(new BuyForOneEmeraldFactory(Items.PUMPKIN_SEEDS, 1, 12, 2));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.HORTICULTURIST, 2, factories -> {
            factories.add(new SellItemFactory(Items.SPRUCE_LEAVES, 4, 2, 3));
            factories.add(new SellItemFactory(Items.BIRCH_LEAVES, 4, 2, 3));
            factories.add(new SellItemFactory(Items.ACACIA_LEAVES, 4, 2, 3));
            factories.add(new SellItemFactory(Items.AZALEA_LEAVES, 4, 2, 3));
            factories.add(new SellItemFactory(Items.DARK_OAK_LEAVES, 4, 2, 3));
            factories.add(new SellItemFactory(Items.JUNGLE_LEAVES, 4, 2, 3));
            //factories.add(new SellItemFactory(Items.MANGROVE_LEAVES, 4, 2, 3));
            factories.add(new SellItemFactory(Items.OAK_LEAVES, 4, 2, 3));
            factories.add(new ProcessItemFactory(Items.STICK, 3, Items.DEAD_BUSH, 1, 8, 4));
            factories.add(new BuyForOneEmeraldFactory(Items.GRASS, 4, 12, 3));
            factories.add(new BuyForOneEmeraldFactory(Items.FERN, 2, 12, 4));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.HORTICULTURIST, 3, factories -> {
            factories.add(new SellItemFactory(Items.ORANGE_TULIP, 4, 1, 4));
            factories.add(new SellItemFactory(Items.RED_TULIP, 4, 1, 4));
            factories.add(new SellItemFactory(Items.WHITE_TULIP, 4, 1, 4));
            factories.add(new SellItemFactory(Items.PINK_TULIP, 4, 1, 4));
            factories.add(new SellItemFactory(Items.FLOWER_POT, 5, 1, 4));
            factories.add(new BuyForOneEmeraldFactory(Items.SPRUCE_SAPLING, 2, 12, 3));
            factories.add(new BuyForOneEmeraldFactory(Items.ACACIA_SAPLING, 2, 12, 3));
            factories.add(new BuyForOneEmeraldFactory(Items.BIRCH_SAPLING, 2, 12, 3));
            factories.add(new BuyForOneEmeraldFactory(Items.DARK_OAK_SAPLING, 2, 12, 3));
            factories.add(new BuyForOneEmeraldFactory(Items.JUNGLE_SAPLING, 2, 12, 3));
            factories.add(new BuyForOneEmeraldFactory(Items.OAK_SAPLING, 2, 12, 3));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.HORTICULTURIST, 4, factories -> {
            factories.add(new SellItemFactory(Items.SUNFLOWER, 5, 1, 4));
            factories.add(new SellItemFactory(Items.ROSE_BUSH, 5, 1, 4));
            factories.add(new SellItemFactory(Items.LILAC, 5, 1, 4));
            factories.add(new SellItemFactory(Items.PEONY, 5, 1, 4));
            factories.add(new BuyForOneEmeraldFactory(Items.ROOTED_DIRT, 2, 32, 2));
            factories.add(new BuyForOneEmeraldFactory(Items.PODZOL, 2, 32, 2));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.HORTICULTURIST, 5, factories -> {
            factories.add(new SellItemFactory(Items.BLUE_ORCHID, 4, 1, 4));
            factories.add(new SellItemFactory(Items.WITHER_ROSE, 8, 1, 5));
            factories.add(new SellItemFactory(Items.FLOWERING_AZALEA_LEAVES, 4, 2, 2));
            factories.add(new SellItemFactory(Items.SPORE_BLOSSOM, 12, 1, 4));
            factories.add(new BuyForOneEmeraldFactory(Items.MOSS_BLOCK, 2, 12, 2));
            factories.add(new BuyForOneEmeraldFactory(Items.BONE_MEAL, 4, 12, 2));
        });




        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCCULTIST, 1, factories -> {
            factories.add(new SellItemFactory(Items.SMALL_AMETHYST_BUD, 1, 1, 1));
            factories.add(new SellItemFactory(Items.MEDIUM_AMETHYST_BUD, 2, 1, 1));
            factories.add(new SellItemFactory(Items.LARGE_AMETHYST_BUD, 3, 1, 2));
            factories.add(new SellItemFactory(Items.AMETHYST_CLUSTER, 5, 1, 3));
            factories.add(new SellItemFactory(Items.TINTED_GLASS, 8, 4, 4));
            factories.add(new BuyForOneEmeraldFactory(Items.AMETHYST_SHARD, 3, 12, 4));
            factories.add(new BuyForOneEmeraldFactory(Items.GLOW_INK_SAC, 1, 12, 4));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCCULTIST, 2, factories -> {
            factories.add(new SellItemFactory(Items.SOUL_LANTERN, 4, 1, 3));
            factories.add(new SellItemFactory(Items.SOUL_TORCH, 2, 1, 3));
            factories.add(new SellItemFactory(Items.GLOWSTONE_DUST, 4, 2, 2));
            factories.add(new BuyForOneEmeraldFactory(Items.SOUL_SAND, 2, 12, 4));
            factories.add(new BuyForOneEmeraldFactory(Items.SOUL_SOIL, 2, 12, 4));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCCULTIST, 3, factories -> {
            factories.add(new SellItemFactory(Items.RED_CANDLE, 4, 2, 4));
            factories.add(new SellItemFactory(Items.ORANGE_CANDLE, 4, 2, 4));
            factories.add(new SellItemFactory(Items.YELLOW_CANDLE, 4, 2, 4));
            factories.add(new SellItemFactory(Items.LIME_CANDLE, 4, 2, 4));
            factories.add(new SellItemFactory(Items.GREEN_CANDLE, 4, 2, 4));
            factories.add(new SellItemFactory(Items.LIGHT_BLUE_CANDLE, 4, 2, 4));
            factories.add(new SellItemFactory(Items.BLUE_CANDLE, 4, 2, 4));
            factories.add(new SellItemFactory(Items.CYAN_CANDLE, 4, 2, 4));
            factories.add(new SellItemFactory(Items.MAGENTA_CANDLE, 4, 2, 4));
            factories.add(new SellItemFactory(Items.PINK_CANDLE, 4, 2, 4));
            factories.add(new SellItemFactory(Items.PURPLE_CANDLE, 4, 2, 4));
            factories.add(new SellItemFactory(Items.WHITE_CANDLE, 4, 2, 4));
            factories.add(new SellItemFactory(Items.GRAY_CANDLE, 4, 2, 4));
            factories.add(new SellItemFactory(Items.LIGHT_GRAY_CANDLE, 4, 2, 4));
            factories.add(new SellItemFactory(Items.BLACK_CANDLE, 4, 2, 4));
            factories.add(new SellItemFactory(Items.BROWN_CANDLE, 4, 2, 4));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCCULTIST, 4, factories -> {
            factories.add(new SellItemFactory(Items.QUARTZ, 4, 2, 4));
            factories.add(new SellItemFactory(Items.SPECTRAL_ARROW, 2, 2, 4));
            factories.add(new SellItemFactory(Items.CANDLE, 4, 2, 4));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCCULTIST, 5, factories -> {
            factories.add(new SellItemFactory(Items.EXPERIENCE_BOTTLE, 3, 1, 5));
            factories.add(new BuyForOneEmeraldFactory(Items.POISONOUS_POTATO, 1, 12, 10));
        });




        TradeOfferHelper.registerVillagerOffers(ModProfessions.ALCHEMIST, 1, factories -> {
            factories.add(new SellItemFactory(Items.SUGAR, 3, 1, 1));
            factories.add(new SellItemFactory(Items.BROWN_MUSHROOM, 3, 1, 1));
            factories.add(new SellItemFactory(Items.RED_MUSHROOM, 3, 1, 1));
            factories.add(new SellItemFactory(Items.NETHER_WART, 5, 1, 2));
            factories.add(new BuyForOneEmeraldFactory(Items.GLASS_BOTTLE, 3, 12, 2));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.ALCHEMIST, 2, factories -> {
            factories.add(new SellItemFactory(Items.MAGMA_CREAM, 4, 1, 3));
            factories.add(new SellItemFactory(Items.PUFFERFISH, 5, 1, 3));
            factories.add(new SellItemFactory(Items.FERMENTED_SPIDER_EYE, 5, 1, 3));
            factories.add(new SellPotionItemFactory(Items.POTION, Items.POTION, 5, 4, 5));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.ALCHEMIST, 3, factories -> {
            factories.add(new SellItemFactory(Items.BLAZE_POWDER, 5, 1, 4));
            factories.add(new SellItemFactory(Items.GHAST_TEAR, 5, 1, 4));
            factories.add(new SellItemFactory(Items.GUNPOWDER, 3, 1, 4));
            factories.add(new SellItemFactory(Items.PHANTOM_MEMBRANE, 5, 1, 4));
            factories.add(new BuyForOneEmeraldFactory(Items.BLAZE_ROD, 1, 12, 5));
            factories.add(new BuyForOneEmeraldFactory(Items.ENDER_PEARL, 1, 12, 5));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.ALCHEMIST, 4, factories -> {
            factories.add(new SellPotionItemFactory(Items.POTION, Items.SPLASH_POTION, 8, 4, 6));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.ALCHEMIST, 5, factories -> {
            factories.add(new SellItemFactory(Items.DRAGON_BREATH, 8, 1, 7));
            factories.add(new SellItemFactory(Items.GLISTERING_MELON_SLICE, 4, 1, 6));
            factories.add(new SellItemFactory(Items.RABBIT_FOOT, 4, 1, 6));
        });



        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCEANOGRAPHER, 1, factories -> {
            factories.add(new SellItemFactory(Items.SEA_PICKLE, 3, 1, 1));
            factories.add(new SellItemFactory(Items.WATER_BUCKET, 5, 1, 5));
            factories.add(new BuyForOneEmeraldFactory(Items.SEAGRASS, 4, 12, 1));
            factories.add(new BuyForOneEmeraldFactory(Items.KELP, 6, 12, 1));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCEANOGRAPHER, 2, factories -> {
            factories.add(new SellItemFactory(Items.BRAIN_CORAL, 3, 1, 3));
            factories.add(new SellItemFactory(Items.BUBBLE_CORAL, 3, 1, 3));
            factories.add(new SellItemFactory(Items.FIRE_CORAL, 3, 1, 3));
            factories.add(new SellItemFactory(Items.HORN_CORAL, 3, 1, 3));
            factories.add(new SellItemFactory(Items.TUBE_CORAL, 3, 1, 3));
            factories.add(new ProcessItemFactory(Items.DEAD_TUBE_CORAL, 1, Items.TUBE_CORAL, 1, 8, 3));
            factories.add(new ProcessItemFactory(Items.DEAD_BRAIN_CORAL, 1, Items.BRAIN_CORAL, 1, 8, 3));
            factories.add(new ProcessItemFactory(Items.DEAD_BUBBLE_CORAL, 1, Items.BUBBLE_CORAL, 1, 8, 3));
            factories.add(new ProcessItemFactory(Items.DEAD_HORN_CORAL, 1, Items.HORN_CORAL, 1, 8, 3));
            factories.add(new ProcessItemFactory(Items.DEAD_FIRE_CORAL, 1, Items.FIRE_CORAL, 1, 8, 3));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCEANOGRAPHER, 3, factories -> {
            factories.add(new SellItemFactory(Items.BRAIN_CORAL_FAN, 3, 1, 4));
            factories.add(new SellItemFactory(Items.BUBBLE_CORAL_FAN, 3, 1, 4));
            factories.add(new SellItemFactory(Items.FIRE_CORAL_FAN, 3, 1, 4));
            factories.add(new SellItemFactory(Items.HORN_CORAL_FAN, 3, 1, 4));
            factories.add(new SellItemFactory(Items.TUBE_CORAL_FAN, 3, 1, 4));
            factories.add(new ProcessItemFactory(Items.DEAD_TUBE_CORAL_FAN, 1, Items.TUBE_CORAL_FAN, 1, 8, 5));
            factories.add(new ProcessItemFactory(Items.DEAD_BRAIN_CORAL_FAN, 1, Items.BRAIN_CORAL_FAN, 1, 8, 5));
            factories.add(new ProcessItemFactory(Items.DEAD_BUBBLE_CORAL_FAN, 1, Items.BUBBLE_CORAL_FAN, 1, 8, 5));
            factories.add(new ProcessItemFactory(Items.DEAD_HORN_CORAL_FAN, 1, Items.HORN_CORAL_FAN, 1, 8, 5));
            factories.add(new ProcessItemFactory(Items.DEAD_FIRE_CORAL_FAN, 1, Items.FIRE_CORAL_FAN, 1, 8, 5));

        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCEANOGRAPHER, 4, factories -> {
            factories.add(new SellItemFactory(Items.BRAIN_CORAL_BLOCK, 5, 1, 5));
            factories.add(new SellItemFactory(Items.BUBBLE_CORAL_BLOCK, 5, 1, 5));
            factories.add(new SellItemFactory(Items.FIRE_CORAL_BLOCK, 5, 1, 5));
            factories.add(new SellItemFactory(Items.HORN_CORAL_BLOCK, 5, 1, 5));
            factories.add(new SellItemFactory(Items.TUBE_CORAL_BLOCK, 5, 1, 5));
            factories.add(new ProcessItemFactory(Items.DEAD_TUBE_CORAL_BLOCK, 1, Items.TUBE_CORAL_BLOCK, 1, 8, 5));
            factories.add(new ProcessItemFactory(Items.DEAD_BRAIN_CORAL_BLOCK, 1, Items.BRAIN_CORAL_BLOCK, 1, 8, 5));
            factories.add(new ProcessItemFactory(Items.DEAD_BUBBLE_CORAL_BLOCK, 1, Items.BUBBLE_CORAL_BLOCK, 1, 8, 5));
            factories.add(new ProcessItemFactory(Items.DEAD_HORN_CORAL_BLOCK, 1, Items.HORN_CORAL_BLOCK, 1, 8, 5));
            factories.add(new ProcessItemFactory(Items.DEAD_FIRE_CORAL_BLOCK, 1, Items.FIRE_CORAL_BLOCK, 1, 8, 5));
        });

        TradeOfferHelper.registerVillagerOffers(ModProfessions.OCEANOGRAPHER, 5, factories -> {
            factories.add(new SellItemFactory(Items.NAUTILUS_SHELL, 6, 1, 15));
            factories.add(new SellItemFactory(Items.SPONGE, 8, 1, 10));
            factories.add(new BuyForOneEmeraldFactory(Items.SCUTE, 2, 8, 8));
            factories.add(new BuyForOneEmeraldFactory(Items.TROPICAL_FISH_BUCKET, 1, 8, 4));
            factories.add(new BuyForOneEmeraldFactory(Items.AXOLOTL_BUCKET, 1, 8, 4));
            factories.add(new BuyForOneEmeraldFactory(Items.PUFFERFISH_BUCKET, 1, 8, 4));
        });
    }

    private static class BuyForOneEmeraldFactory implements TradeOffers.Factory {
        private final Item buy;
        private final int price;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public BuyForOneEmeraldFactory(ItemConvertible item, int price, int maxUses, int experience) {
            this.buy = item.asItem();
            this.price = price;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = 0.05F;
        }

        public TradeOffer create(Entity entity, Random random) {
            ItemStack itemStack = new ItemStack(this.buy, this.price);
            return new TradeOffer(itemStack, new ItemStack(Items.EMERALD), this.maxUses, this.experience, this.multiplier);
        }
    }

    static class SellItemFactory implements TradeOffers.Factory {
        private final ItemStack sell;
        private final int price;
        private final int count;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public SellItemFactory(Block block, int price, int count, int maxUses, int experience) {
            this(new ItemStack(block), price, count, maxUses, experience);
        }

        public SellItemFactory(Item item, int price, int count, int experience) {
            this((ItemStack)(new ItemStack(item)), price, count, 12, experience);
        }

        public SellItemFactory(Item item, int price, int count, int maxUses, int experience) {
            this(new ItemStack(item), price, count, maxUses, experience);
        }

        public SellItemFactory(ItemStack stack, int price, int count, int maxUses, int experience) {
            this(stack, price, count, maxUses, experience, 0.05F);
        }

        public SellItemFactory(ItemStack stack, int price, int count, int maxUses, int experience, float multiplier) {
            this.sell = stack;
            this.price = price;
            this.count = count;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        public TradeOffer create(Entity entity, Random random) {
            return new TradeOffer(new ItemStack(Items.EMERALD, this.price), new ItemStack(this.sell.getItem(), this.count), this.maxUses, this.experience, this.multiplier);
        }
    }

    static class ProcessItemFactory implements TradeOffers.Factory {
        private final ItemStack secondBuy;
        private final int secondCount;
        private final int price;
        private final ItemStack sell;
        private final int sellCount;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public ProcessItemFactory(ItemConvertible item, int secondCount, Item sellItem, int sellCount, int maxUses, int experience) {
            this(item, secondCount, 1, sellItem, sellCount, maxUses, experience);
        }

        public ProcessItemFactory(ItemConvertible item, int secondCount, int price, Item sellItem, int sellCount, int maxUses, int experience) {
            this.secondBuy = new ItemStack(item);
            this.secondCount = secondCount;
            this.price = price;
            this.sell = new ItemStack(sellItem);
            this.sellCount = sellCount;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = 0.05F;
        }

        @Nullable
        public TradeOffer create(Entity entity, Random random) {
            return new TradeOffer(new ItemStack(Items.EMERALD, this.price), new ItemStack(this.secondBuy.getItem(), this.secondCount), new ItemStack(this.sell.getItem(), this.sellCount), this.maxUses, this.experience, this.multiplier);
        }
    }
}
