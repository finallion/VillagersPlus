package com.lion.villagersplus.fabric;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.init.VPPointOfInterestTypes;
import com.lion.villagersplus.platform.fabric.DefaultTradeOfferResourceListener;
import com.lion.villagersplus.platform.fabric.TradeOfferResourceListener;
import com.lion.villagersplus.util.StructurePoolAddition;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class VillagersPlusFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        VillagersPlus.init();
        VillagersPlus.postInit();
        VPPointOfInterestTypes.postInit();

        registerServerEvents();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DefaultTradeOfferResourceListener());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new TradeOfferResourceListener());
    }

    private void registerServerEvents() {
        ServerLifecycleEvents.SERVER_STARTING.register(StructurePoolAddition::registerJigsaws);
    }
}
