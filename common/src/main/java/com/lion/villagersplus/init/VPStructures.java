package com.lion.villagersplus.init;

import com.lion.villagersplus.platform.RegistryHelper;
import com.lion.villagersplus.worldgen.OreVeinProcessor;

public class VPStructures {

    static {
        RegistryHelper.registerStructureProcessorType("ore_vein", OreVeinProcessor.CODEC);
    }

    public static void init() {

    }
}
