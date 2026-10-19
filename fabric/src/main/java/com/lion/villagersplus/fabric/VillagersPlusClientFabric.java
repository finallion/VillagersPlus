package com.lion.villagersplus.fabric;

import com.lion.villagersplus.VillagersPlusClient;
import com.lion.villagersplus.client.screen.AlchemistTableScreen;
import com.lion.villagersplus.client.screen.OreGrinderScreen;
import com.lion.villagersplus.init.VPBlockEntities;
import com.lion.villagersplus.init.VPBlocks;
import com.lion.villagersplus.init.VPParticles;
import com.lion.villagersplus.init.VPScreens;
import com.lion.villagersplus.particles.BubbleParticle;
import com.lion.villagersplus.particles.ExperienceParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BlockRenderLayers;

public class VillagersPlusClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        VillagersPlusClient.init();
        VillagersPlusClient.postInit();

        HandledScreens.register(VPScreens.ALCHEMIST_TABLE_SCREEN_HANDLER, AlchemistTableScreen::new);
        HandledScreens.register(VPScreens.ORE_GRINDER_SCREEN_HANDLER, OreGrinderScreen::new);

        ParticleFactoryRegistry.getInstance().register(VPParticles.EXPERIENCE_PARTICLE, ExperienceParticle.ExperienceParticleFactory::new);
        ParticleFactoryRegistry.getInstance().register(VPParticles.BUBBLE_PARTICLE, BubbleParticle.Factory::new);

        // Fabric API has no render layer map. Write to the vanilla map.
        BlockRenderLayers.BLOCKS.put(VPBlocks.OCEANOGRAPHER_TABLE_BLOCK.get(), BlockRenderLayer.CUTOUT);
        BlockRenderLayers.BLOCKS.put(VPBlocks.ALCHEMIST_TABLE_BLOCK.get(), BlockRenderLayer.CUTOUT);
        BlockRenderLayers.BLOCKS.put(VPBlocks.ORE_GRINDER_BLOCK.get(), BlockRenderLayer.CUTOUT);
    }
}
