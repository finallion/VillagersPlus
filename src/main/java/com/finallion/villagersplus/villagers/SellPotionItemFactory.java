package com.finallion.villagersplus.villagers;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;

import java.util.List;
import java.util.stream.Collectors;

public class SellPotionItemFactory implements TradeOffers.Factory {
    private final ItemStack sell;
    private final int price;
    private final int maxUses;
    private final int experience;
    private final Item secondBuy;
    private final float priceMultiplier;

    public SellPotionItemFactory(Item potionItem, Item potionEffectItem, int price, int maxUses, int experience) {
        this.sell = new ItemStack(potionEffectItem);
        this.price = price;
        this.maxUses = maxUses;
        this.experience = experience;
        this.secondBuy = potionItem;
        this.priceMultiplier = 0.05F;
    }

    public TradeOffer create(Entity entity, Random random) {
        ItemStack itemStack = new ItemStack(Items.EMERALD, this.price);
        List<Potion> list = (List) Registries.POTION.stream().filter((potionx) -> {
            return !potionx.getEffects().isEmpty() && BrewingRecipeRegistry.isBrewable(potionx);
        }).collect(Collectors.toList());


        Potion potion = (Potion)list.get(random.nextInt(list.size()));
        ItemStack itemStack2 = PotionUtil.setPotion(new ItemStack(this.sell.getItem(), 1), potion);
        return new TradeOffer(itemStack, PotionUtil.setPotion(new ItemStack(this.secondBuy, 1), Potions.WATER), itemStack2, this.maxUses, this.experience, this.priceMultiplier);
    }
}
