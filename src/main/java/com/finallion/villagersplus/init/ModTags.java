package com.finallion.villagersplus.init;

import com.finallion.villagersplus.VillagersPlus;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModTags {
    public static final TagKey<Item> TALL_PLANTABLE_ITEMS = TagKey.of(Registry.ITEM_KEY, new Identifier(VillagersPlus.MOD_ID, "flower_tub_tall_plantable_items"));
    public static final TagKey<Item> SMALL_PLANTABLE_ITEMS = TagKey.of(Registry.ITEM_KEY, new Identifier(VillagersPlus.MOD_ID, "flower_tub_small_plantable_items"));

    public static final TagKey<Item> AQUARIUM_PLANTABLE_ITEMS = TagKey.of(Registry.ITEM_KEY, new Identifier(VillagersPlus.MOD_ID, "aquarium_plantable_items"));

    public static void init() {}
}
