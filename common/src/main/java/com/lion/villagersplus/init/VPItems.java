package com.lion.villagersplus.init;

import com.lion.villagersplus.platform.RegistryHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import java.util.function.Supplier;

public class VPItems {

    public static final Supplier<Item> ALCHEMIST_TABLE_BLOCK = RegistryHelper.registerItem("alchemist_table", () -> new BlockItem(VPBlocks.ALCHEMIST_TABLE_BLOCK.get(), new Item.Settings()));
    public static final Supplier<Item> OCEANOGRAPHER_TABLE_BLOCK = RegistryHelper.registerItem("oceanographer_table", () -> new BlockItem(VPBlocks.OCEANOGRAPHER_TABLE_BLOCK.get(), new Item.Settings()));
    public static final Supplier<Item> OAK_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("oak_horticulturist_table", () -> new BlockItem(VPBlocks.OAK_HORTICULTURIST_TABLE_BLOCK.get(), new Item.Settings()));
    public static final Supplier<Item> DARK_OAK_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("dark_oak_horticulturist_table", () -> new BlockItem(VPBlocks.DARK_OAK_HORTICULTURIST_TABLE_BLOCK.get(), new Item.Settings()));
    public static final Supplier<Item> ACACIA_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("acacia_horticulturist_table", () -> new BlockItem(VPBlocks.ACACIA_HORTICULTURIST_TABLE_BLOCK.get(), new Item.Settings()));
    public static final Supplier<Item> JUNGLE_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("jungle_horticulturist_table", () -> new BlockItem(VPBlocks.JUNGLE_HORTICULTURIST_TABLE_BLOCK.get(), new Item.Settings()));
    public static final Supplier<Item> SPRUCE_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("spruce_horticulturist_table", () -> new BlockItem(VPBlocks.SPRUCE_HORTICULTURIST_TABLE_BLOCK.get(), new Item.Settings()));
    public static final Supplier<Item> BIRCH_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("birch_horticulturist_table", () -> new BlockItem(VPBlocks.BIRCH_HORTICULTURIST_TABLE_BLOCK.get(), new Item.Settings()));
    public static final Supplier<Item> MANGROVE_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("mangrove_horticulturist_table", () -> new BlockItem(VPBlocks.MANGROVE_HORTICULTURIST_TABLE_BLOCK.get(), new Item.Settings()));
    public static final Supplier<Item> CRIMSON_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("crimson_horticulturist_table", () -> new BlockItem(VPBlocks.CRIMSON_HORTICULTURIST_TABLE_BLOCK.get(), new Item.Settings()));
    public static final Supplier<Item> WARPED_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("warped_horticulturist_table", () -> new BlockItem(VPBlocks.WARPED_HORTICULTURIST_TABLE_BLOCK.get(), new Item.Settings()));
    public static final Supplier<Item> CHERRY_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("cherry_horticulturist_table", () -> new BlockItem(VPBlocks.CHERRY_HORTICULTURIST_TABLE_BLOCK.get(), new Item.Settings()));
    public static final Supplier<Item> BAMBOO_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("bamboo_horticulturist_table", () -> new BlockItem(VPBlocks.BAMBOO_HORTICULTURIST_TABLE_BLOCK.get(), new Item.Settings()));

    public static final Supplier<Item> OCCULTIST_TABLE_BLOCK = RegistryHelper.registerItem("occultist_table", () -> new BlockItem(VPBlocks.OCCULTIST_TABLE_BLOCK.get(), new Item.Settings()));


    public static void init() {}

    public static void addItemsToGroup() {
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, OAK_HORTICULTURIST_TABLE_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, BIRCH_HORTICULTURIST_TABLE_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, SPRUCE_HORTICULTURIST_TABLE_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, DARK_OAK_HORTICULTURIST_TABLE_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, JUNGLE_HORTICULTURIST_TABLE_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, ACACIA_HORTICULTURIST_TABLE_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, MANGROVE_HORTICULTURIST_TABLE_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, CRIMSON_HORTICULTURIST_TABLE_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, WARPED_HORTICULTURIST_TABLE_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, CHERRY_HORTICULTURIST_TABLE_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, BAMBOO_HORTICULTURIST_TABLE_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, OCEANOGRAPHER_TABLE_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, ALCHEMIST_TABLE_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, OCCULTIST_TABLE_BLOCK.get());
    }
}
