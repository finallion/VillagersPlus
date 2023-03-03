package com.finallion.villagersplus.init;

import com.finallion.villagersplus.VillagersPlus;
import com.finallion.villagersplus.client.screen.AlchemistTableScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModScreen {

    public static void init() {

    }

    public final static ScreenHandlerType<AlchemistTableScreenHandler> ALCHEMIST_TABLE_SCREEN_HANDLER =
            Registry.register(Registry.SCREEN_HANDLER, new Identifier(VillagersPlus.MOD_ID, "alchemist_table_screen_handler"), new ScreenHandlerType<>(AlchemistTableScreenHandler::new));

}
