package com.lion.villagersplus.neoforge;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.VillagersPlusClient;
import com.lion.villagersplus.client.renderer.HorticulturistTableBlockEntityRenderer;
import com.lion.villagersplus.client.renderer.OceanographerTableBlockEntityRenderer;
import com.lion.villagersplus.client.screen.AlchemistTableScreen;
import com.lion.villagersplus.client.screen.OreGrinderScreen;
import com.lion.villagersplus.init.VPBlockEntities;
import com.lion.villagersplus.init.VPParticles;
import com.lion.villagersplus.init.VPScreens;
import com.lion.villagersplus.particles.BubbleParticle;
import com.lion.villagersplus.particles.ExperienceParticle;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/** {@code @Mod.EventBusSubscriber} became a top-level {@code @EventBusSubscriber} in NeoForge. */
@EventBusSubscriber(modid = VillagersPlus.MOD_ID, value = Dist.CLIENT)
public class VillagersPlusClientNeoForge {

    @SubscribeEvent
    public static void clientInit(final FMLClientSetupEvent event) {
        event.enqueueWork(VillagersPlusClient::postInit);
    }

    /**
     * {@code HandledScreens.register} is private in vanilla; Fabric API widens it, NeoForge instead
     * hands out this event. Registering here rather than in client setup is also the supported order.
     */
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(VPScreens.ALCHEMIST_TABLE_SCREEN_HANDLER, AlchemistTableScreen::new);
        event.register(VPScreens.ORE_GRINDER_SCREEN_HANDLER, OreGrinderScreen::new);
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(VPBlockEntities.HORTICULTURIST_TABLE_BLOCK_ENTITY.get(), HorticulturistTableBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(VPBlockEntities.OCEANOGRAPHER_TABLE_BLOCK_ENTITY.get(), OceanographerTableBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(VPParticles.EXPERIENCE_PARTICLE, ExperienceParticle.ExperienceParticleFactory::new);
        event.registerSpriteSet(VPParticles.BUBBLE_PARTICLE, BubbleParticle.Factory::new);
    }
}
