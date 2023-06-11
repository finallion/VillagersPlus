package com.finallion.villagersplus;

import com.finallion.villagersplus.init.*;
import com.finallion.villagersplus.villagers.ModPointOfInterestType;
import com.finallion.villagersplus.villagers.ModProfessions;
import com.finallion.villagersplus.villagers.ModTrades;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VillagersPlus implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("villagersplus");
	public static final String MOD_ID = "villagersplus";

	public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(MOD_ID, "group"));

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, FabricItemGroup.builder()
				.displayName(Text.literal("VillagersPlus"))
				.icon(() -> new ItemStack(ModBlocks.OAK_HORTICULTURIST_TABLE_BLOCK))
				.entries((enabledFeatures, entries) -> {
					entries.add(ModBlocks.OAK_HORTICULTURIST_TABLE_BLOCK);
					entries.add(ModBlocks.BIRCH_HORTICULTURIST_TABLE_BLOCK);
					entries.add(ModBlocks.SPRUCE_HORTICULTURIST_TABLE_BLOCK);
					entries.add(ModBlocks.DARK_OAK_HORTICULTURIST_TABLE_BLOCK);
					entries.add(ModBlocks.JUNGLE_HORTICULTURIST_TABLE_BLOCK);
					entries.add(ModBlocks.ACACIA_HORTICULTURIST_TABLE_BLOCK);
					entries.add(ModBlocks.MANGROVE_HORTICULTURIST_TABLE_BLOCK);
					entries.add(ModBlocks.CRIMSON_HORTICULTURIST_TABLE_BLOCK);
					entries.add(ModBlocks.WARPED_HORTICULTURIST_TABLE_BLOCK);
					entries.add(ModBlocks.OCEANOGRAPHER_TABLE_BLOCK);
					entries.add(ModBlocks.ALCHEMIST_TABLE_BLOCK);
					entries.add(ModBlocks.OCCULTIST_TABLE_BLOCK);
				})
				.build());

		ModTags.init();
		ModScreen.init();
		ModParticles.init();
		ModBlocks.registerBlocks();
		ModPointOfInterestType.registerPOIs();
		ModProfessions.registerProfessions();
		ModTrades.registerTradeOffers();

		ServerLifecycleEvents.SERVER_STARTING.register(ModStructures::registerJigsaws);

	}
}
