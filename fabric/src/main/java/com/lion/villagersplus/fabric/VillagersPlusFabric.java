package com.lion.villagersplus.fabric;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.init.VPPointOfInterestTypes;
import com.lion.villagersplus.util.StructurePoolAddition;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class VillagersPlusFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        VillagersPlus.init();
        VillagersPlus.postInit();
        VPPointOfInterestTypes.postInit();

        registerServerEvents();
    }

    private void registerServerEvents() {
        ServerLifecycleEvents.SERVER_STARTING.register(StructurePoolAddition::registerJigsaws);
    }
}
