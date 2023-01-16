package com.finallion.villagersplus.init;

import com.finallion.villagersplus.VillagersPlus;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static final TagKey<Item> PLANTABLE_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier(VillagersPlus.MOD_ID, "plantable_blocks"));
    public static final TagKey<Item> TALL_PLANTABLE_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier(VillagersPlus.MOD_ID, "tall_plantable_blocks"));
    public static final TagKey<Item> SMALL_PLANTABLE_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier(VillagersPlus.MOD_ID, "small_plantable_blocks"));

    public static final TagKey<Item> AQUARIUM_PLANTABLE_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier(VillagersPlus.MOD_ID, "aquarium_plantable_blocks"));
    public static final TagKey<Item> ROTATIONAL_BUCKET_ENTITIES = TagKey.of(RegistryKeys.ITEM, new Identifier(VillagersPlus.MOD_ID, "rotationable_bucket_entities"));

    public static final TagKey<Block> SCALABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(VillagersPlus.MOD_ID, "scalable_blocks"));

    public static void init() {}
}
