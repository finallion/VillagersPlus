package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;


public class VPTags {
    public static final TagKey<Item> TALL_PLANTABLE_ITEMS = TagKey.of(RegistryKeys.ITEM, new Identifier(VillagersPlus.MOD_ID, "flower_tub_tall_plantable_items"));
    public static final TagKey<Item> SMALL_PLANTABLE_ITEMS = TagKey.of(RegistryKeys.ITEM, new Identifier(VillagersPlus.MOD_ID, "flower_tub_small_plantable_items"));

    public static final TagKey<Item> AQUARIUM_PLANTABLE_ITEMS = TagKey.of(RegistryKeys.ITEM, new Identifier(VillagersPlus.MOD_ID, "aquarium_plantable_items"));
}
