package com.lion.villagersplus.forge;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.VillagersPlusClient;
import com.lion.villagersplus.client.renderer.HorticulturistTableBlockEntityRenderer;
import com.lion.villagersplus.client.renderer.OceanographerTableBlockEntityRenderer;
import com.lion.villagersplus.client.screen.AlchemistTableScreen;
import com.lion.villagersplus.init.VPBlockEntities;
import com.lion.villagersplus.init.VPParticles;
import com.lion.villagersplus.init.VPScreens;
import com.lion.villagersplus.particles.BubbleParticle;
import com.lion.villagersplus.particles.ExperienceParticle;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = VillagersPlus.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VillagersPlusClientForge {


    @SubscribeEvent
    public static void clientInit(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            VillagersPlusClient.postInit();

            HandledScreens.register(VPScreens.ALCHEMIST_TABLE_SCREEN_HANDLER, AlchemistTableScreen::new);
        });
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(VPBlockEntities.HORTICULTURIST_TABLE_BLOCK_ENTITY.get(), HorticulturistTableBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(VPBlockEntities.OCEANOGRAPHER_TABLE_BLOCK_ENTITY.get(), OceanographerTableBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event){
        event.registerSpriteSet(VPParticles.EXPERIENCE_PARTICLE, ExperienceParticle.ExperienceParticleFactory::new);
        event.registerSpriteSet(VPParticles.BUBBLE_PARTICLE, BubbleParticle.Factory::new);
    }


}



