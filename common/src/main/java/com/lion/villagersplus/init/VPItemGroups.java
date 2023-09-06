package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.platform.RegistryHelper;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class VPItemGroups {

    public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(VillagersPlus.MOD_ID, "group"));

    public static void init() {
        RegistryHelper.registerItemGroup(ITEM_GROUP, "group", "VillagersPlus", VPItems.MANGROVE_HORTICULTURIST_TABLE_BLOCK);
    }
}
