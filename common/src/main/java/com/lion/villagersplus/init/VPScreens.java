package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.client.screen.AlchemistTableScreenHandler;
import com.lion.villagersplus.client.screen.OreGrinderScreenHandler;
import com.lion.villagersplus.platform.RegistryHelper;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class VPScreens {

    public static void init() {}

    public static final MenuType<AlchemistTableScreenHandler> ALCHEMIST_TABLE_SCREEN_HANDLER = new MenuType<>(AlchemistTableScreenHandler::new, FeatureFlags.VANILLA_SET);
    public static final MenuType<OreGrinderScreenHandler> ORE_GRINDER_SCREEN_HANDLER = new MenuType<>(OreGrinderScreenHandler::new, FeatureFlags.VANILLA_SET);

    static {
        RegistryHelper.registerScreenHandlerType("alchemist_table_screen_handler", ALCHEMIST_TABLE_SCREEN_HANDLER);
        RegistryHelper.registerScreenHandlerType("ore_grinder_screen_handler", ORE_GRINDER_SCREEN_HANDLER);
    }

}
