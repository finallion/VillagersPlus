package com.lion.villagersplus;

import com.lion.villagersplus.init.VPBlockEntityRenderers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;



public class VillagersPlusClient {

    @Environment(EnvType.CLIENT)
    public static void init() {

    }

    @Environment(EnvType.CLIENT)
    public static void postInit() {
        VPBlockEntityRenderers.postInit();

    }
}
