package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class VPTags {
    public static final TagKey<Item> TALL_PLANTABLE_ITEMS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(VillagersPlus.MOD_ID, "flower_tub_tall_plantable_items"));
    public static final TagKey<Item> SMALL_PLANTABLE_ITEMS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(VillagersPlus.MOD_ID, "flower_tub_small_plantable_items"));

    public static final TagKey<Item> AQUARIUM_PLANTABLE_ITEMS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(VillagersPlus.MOD_ID, "aquarium_plantable_items"));
}
