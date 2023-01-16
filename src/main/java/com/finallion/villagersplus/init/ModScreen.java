package com.finallion.villagersplus.init;

import com.finallion.villagersplus.VillagersPlus;
import com.finallion.villagersplus.client.screen.AlchemistTableScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreen {

    public static void init() {

    }

    public final static ScreenHandlerType<AlchemistTableScreenHandler> ALCHEMIST_TABLE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(VillagersPlus.MOD_ID, "alchemist_table_screen_handler"), new ScreenHandlerType<>(AlchemistTableScreenHandler::new));

}
