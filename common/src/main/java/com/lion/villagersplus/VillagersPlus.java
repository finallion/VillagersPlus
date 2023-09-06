package com.lion.villagersplus;

import com.lion.villagersplus.config.OmegaConfig;
import com.lion.villagersplus.config.VPConfig;
import com.lion.villagersplus.init.*;
import com.lion.villagersplus.tradeoffers.TradeOfferManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VillagersPlus {
	public static final String MOD_ID = "villagersplus";
	public static final VPConfig CONFIG = OmegaConfig.register(VPConfig.class);
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static VPConfig getConfig() {
		return CONFIG;
	}

	public static void init() {
		VPItems.init();
		VPBlocks.init();
		VPItemGroups.init();
		VPBlockEntities.init();
		VPParticles.init();
		VPScreens.init();
		VPPointOfInterestTypes.init();
		VPVillagerProfessions.init();

		TradeOfferManager.registerTradeOffers();
	}

	public static void postInit() {
		VPItems.addItemsToGroup();
	}

	public static String createStringID(String name) {
		return MOD_ID + ":" + name;
	}
}
