package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.client.screen.AlchemistTableScreenHandler;
import com.lion.villagersplus.platform.RegistryHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class VPScreens {

    public static void init() {}

    public static final ScreenHandlerType<AlchemistTableScreenHandler> ALCHEMIST_TABLE_SCREEN_HANDLER = new ScreenHandlerType<>(AlchemistTableScreenHandler::new, FeatureFlags.VANILLA_FEATURES);

    static {
        RegistryHelper.registerScreenHandlerType("alchemist_table_screen_handler", ALCHEMIST_TABLE_SCREEN_HANDLER);
    }

}
