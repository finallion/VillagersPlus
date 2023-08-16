package com.finallion.villagersplus;

import com.finallion.villagersplus.config.VPConfig;
import com.finallion.villagersplus.init.*;
import com.finallion.villagersplus.tradeoffers.DefaultTradeOfferResourceListener;
import com.finallion.villagersplus.tradeoffers.TradeOfferManager;
import com.finallion.villagersplus.tradeoffers.TradeOfferResourceListener;
import com.finallion.villagersplus.villagers.ModPointOfInterestType;
import com.finallion.villagersplus.villagers.ModProfessions;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VillagersPlus implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("villagersplus");
	public static final String MOD_ID = "villagersplus";
	public static VPConfig CONFIG = new VPConfig();

	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.create(new Identifier(MOD_ID, "group"))
			.icon(() -> new ItemStack(ModBlocks.OAK_HORTICULTURIST_TABLE_BLOCK))
			.build();


	@Override
	public void onInitialize() {
		AutoConfig.register(VPConfig.class, JanksonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(VPConfig.class).getConfig();


		ModTags.init();
		ModScreen.init();
		ModParticles.init();
		ModBlocks.registerBlocks();
		ModPointOfInterestType.registerPOIs();
		ModProfessions.registerProfessions();

		TradeOfferManager.registerTradeOffers();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DefaultTradeOfferResourceListener());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new TradeOfferResourceListener());


		ServerLifecycleEvents.SERVER_STARTING.register(ModStructures::registerJigsaws);

	}
}
