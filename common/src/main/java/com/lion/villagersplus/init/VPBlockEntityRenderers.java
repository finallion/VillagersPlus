package com.lion.villagersplus.init;

import com.lion.villagersplus.client.renderer.HorticulturistTableBlockEntityRenderer;
import com.lion.villagersplus.client.renderer.OceanographerTableBlockEntityRenderer;
import com.lion.villagersplus.platform.RegistryHelper;

public class VPBlockEntityRenderers {

    public static void postInit() {
        RegistryHelper.registerBlockEntityRenderer(VPBlockEntities.HORTICULTURIST_TABLE_BLOCK_ENTITY, HorticulturistTableBlockEntityRenderer::new);
        RegistryHelper.registerBlockEntityRenderer(VPBlockEntities.OCEANOGRAPHER_TABLE_BLOCK_ENTITY, OceanographerTableBlockEntityRenderer::new);
    }
}
