package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.platform.RegistryHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public class VPItemGroups {

    public static final ResourceKey<CreativeModeTab> ITEM_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(VillagersPlus.MOD_ID, "group"));

    public static void init() {
        RegistryHelper.registerItemGroup(ITEM_GROUP, "group", "VillagersPlus", VPItems.MANGROVE_HORTICULTURIST_TABLE_BLOCK);
    }
}
