package com.lion.villagersplus.init;

import com.lion.villagersplus.platform.RegistryHelper;
import com.lion.villagersplus.worldgen.OreVeinProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class VPStructures {

    /// `OreVeinProcessor#getType` returns this registered instance.
    public static final StructureProcessorType<OreVeinProcessor> ORE_VEIN = () -> OreVeinProcessor.CODEC;

    static {
        RegistryHelper.registerStructureProcessorType("ore_vein", ORE_VEIN);
    }

    public static void init() {

    }
}
