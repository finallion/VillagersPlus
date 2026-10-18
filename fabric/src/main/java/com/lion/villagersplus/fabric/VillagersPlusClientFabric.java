package com.lion.villagersplus.fabric;

import com.lion.villagersplus.VillagersPlusClient;
import com.lion.villagersplus.client.screen.AlchemistTableScreen;
import com.lion.villagersplus.client.screen.OreGrinderScreen;
import com.lion.villagersplus.init.VPBlockEntities;
import com.lion.villagersplus.init.VPParticles;
import com.lion.villagersplus.init.VPScreens;
import com.lion.villagersplus.particles.BubbleParticle;
import com.lion.villagersplus.particles.ExperienceParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

public class VillagersPlusClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        VillagersPlusClient.init();
        VillagersPlusClient.postInit();

        MenuScreens.register(VPScreens.ALCHEMIST_TABLE_SCREEN_HANDLER, AlchemistTableScreen::new);
        MenuScreens.register(VPScreens.ORE_GRINDER_SCREEN_HANDLER, OreGrinderScreen::new);

        ParticleProviderRegistry.getInstance().register(VPParticles.EXPERIENCE_PARTICLE, ExperienceParticle.ExperienceParticleFactory::new);
        ParticleProviderRegistry.getInstance().register(VPParticles.BUBBLE_PARTICLE, BubbleParticle.Factory::new);
    }
}
