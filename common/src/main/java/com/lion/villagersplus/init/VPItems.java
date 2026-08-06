package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.platform.RegistryHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class VPItems {

    /**
     * Items need their RegistryKey in the settings since 1.21.2, same reason as in VPBlocks.
     */
    private static Item.Settings settings(String name) {
        return new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(VillagersPlus.MOD_ID, name)));
    }

    /**
     * A BlockItem no longer derives its translation key from its block on its own - it is decided by
     * the settings now. Without this the lang keys would have to be duplicated under item.*, and the
     * items would show up untranslated against the existing block.villagersplus.* entries.
     */
    private static Item.Settings blockItemSettings(String name) {
        return settings(name).useBlockPrefixedTranslationKey();
    }

    public static final Supplier<Item> ALCHEMIST_TABLE_BLOCK = RegistryHelper.registerItem("alchemist_table", () -> new BlockItem(VPBlocks.ALCHEMIST_TABLE_BLOCK.get(), blockItemSettings("alchemist_table")));
    public static final Supplier<Item> OCEANOGRAPHER_TABLE_BLOCK = RegistryHelper.registerItem("oceanographer_table", () -> new BlockItem(VPBlocks.OCEANOGRAPHER_TABLE_BLOCK.get(), blockItemSettings("oceanographer_table")));
    public static final Supplier<Item> OAK_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("oak_horticulturist_table", () -> new BlockItem(VPBlocks.OAK_HORTICULTURIST_TABLE_BLOCK.get(), blockItemSettings("oak_horticulturist_table")));
    public static final Supplier<Item> DARK_OAK_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("dark_oak_horticulturist_table", () -> new BlockItem(VPBlocks.DARK_OAK_HORTICULTURIST_TABLE_BLOCK.get(), blockItemSettings("dark_oak_horticulturist_table")));
    public static final Supplier<Item> ACACIA_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("acacia_horticulturist_table", () -> new BlockItem(VPBlocks.ACACIA_HORTICULTURIST_TABLE_BLOCK.get(), blockItemSettings("acacia_horticulturist_table")));
    public static final Supplier<Item> JUNGLE_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("jungle_horticulturist_table", () -> new BlockItem(VPBlocks.JUNGLE_HORTICULTURIST_TABLE_BLOCK.get(), blockItemSettings("jungle_horticulturist_table")));
    public static final Supplier<Item> SPRUCE_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("spruce_horticulturist_table", () -> new BlockItem(VPBlocks.SPRUCE_HORTICULTURIST_TABLE_BLOCK.get(), blockItemSettings("spruce_horticulturist_table")));
    public static final Supplier<Item> BIRCH_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("birch_horticulturist_table", () -> new BlockItem(VPBlocks.BIRCH_HORTICULTURIST_TABLE_BLOCK.get(), blockItemSettings("birch_horticulturist_table")));
    public static final Supplier<Item> MANGROVE_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("mangrove_horticulturist_table", () -> new BlockItem(VPBlocks.MANGROVE_HORTICULTURIST_TABLE_BLOCK.get(), blockItemSettings("mangrove_horticulturist_table")));
    public static final Supplier<Item> CRIMSON_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("crimson_horticulturist_table", () -> new BlockItem(VPBlocks.CRIMSON_HORTICULTURIST_TABLE_BLOCK.get(), blockItemSettings("crimson_horticulturist_table")));
    public static final Supplier<Item> WARPED_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("warped_horticulturist_table", () -> new BlockItem(VPBlocks.WARPED_HORTICULTURIST_TABLE_BLOCK.get(), blockItemSettings("warped_horticulturist_table")));
    public static final Supplier<Item> CHERRY_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("cherry_horticulturist_table", () -> new BlockItem(VPBlocks.CHERRY_HORTICULTURIST_TABLE_BLOCK.get(), blockItemSettings("cherry_horticulturist_table")));
    public static final Supplier<Item> BAMBOO_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("bamboo_horticulturist_table", () -> new BlockItem(VPBlocks.BAMBOO_HORTICULTURIST_TABLE_BLOCK.get(), blockItemSettings("bamboo_horticulturist_table")));
    public static final Supplier<Item> PALE_OAK_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerItem("pale_oak_horticulturist_table", () -> new BlockItem(VPBlocks.PALE_OAK_HORTICULTURIST_TABLE_BLOCK.get(), blockItemSettings("pale_oak_horticulturist_table")));

    public static final Supplier<Item> OCCULTIST_TABLE_BLOCK = RegistryHelper.registerItem("occultist_table", () -> new BlockItem(VPBlocks.OCCULTIST_TABLE_BLOCK.get(), blockItemSettings("occultist_table")));

    public static final Supplier<Item> ORE_GRINDER_BLOCK = RegistryHelper.registerItem("ore_grinder", () -> new BlockItem(VPBlocks.ORE_GRINDER_BLOCK.get(), blockItemSettings("ore_grinder")));

    // Aquarium fish-size items: fish food grows the aquarium's animal, diet food shrinks it.
    public static final Supplier<Item> FISH_FOOD = RegistryHelper.registerItem("fish_food", () -> new Item(settings("fish_food")));
    public static final Supplier<Item> DIET_FOOD = RegistryHelper.registerItem("diet_food", () -> new Item(settings("diet_food")));
    // Calming food: toggles the aquarium animal between free swimming and hovering animated in the centre.
    public static final Supplier<Item> CALM_FOOD = RegistryHelper.registerItem("calm_food", () -> new Item(settings("calm_food")));


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
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, PALE_OAK_HORTICULTURIST_TABLE_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, OCEANOGRAPHER_TABLE_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, ALCHEMIST_TABLE_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, OCCULTIST_TABLE_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, ORE_GRINDER_BLOCK.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, FISH_FOOD.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, DIET_FOOD.get());
        RegistryHelper.addToItemGroup(VPItemGroups.ITEM_GROUP, CALM_FOOD.get());
    }
}
