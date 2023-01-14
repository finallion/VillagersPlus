package com.finallion.villagersplus.client;

import com.finallion.villagersplus.VillagersPlus;
import com.finallion.villagersplus.client.renderer.HorticulturistTableBlockEntityRenderer;
import com.finallion.villagersplus.client.renderer.OceanographerTableBlockEntityRenderer;
import com.finallion.villagersplus.client.screen.AlchemistTableScreen;
import com.finallion.villagersplus.init.ModBlocks;
import com.finallion.villagersplus.init.ModParticles;
import com.finallion.villagersplus.init.ModScreen;
import com.finallion.villagersplus.particles.BubbleParticle;
import com.finallion.villagersplus.particles.ExperienceParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.client.particle.WaterSuspendParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VillagersPlusClient implements ClientModInitializer {
    private static final RenderLayer CUTOUT_MIPPED = RenderLayer.getCutoutMipped();
    private static final RenderLayer TRANSLUCENT = RenderLayer.getTranslucent();

    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance().register(ModParticles.EXPERIENCE_PARTICLE, ExperienceParticle.ExperienceParticleFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.BUBBLE_PARTICLE, BubbleParticle.Factory::new);

        HandledScreens.register(ModScreen.ALCHEMIST_TABLE_SCREEN_HANDLER, AlchemistTableScreen::new);

        BlockRenderLayerMap.INSTANCE.putBlocks(CUTOUT_MIPPED,
                ModBlocks.OCEANOGRAPHER_TABLE_BLOCK,
                ModBlocks.ALCHEMIST_TABLE_BLOCK,
                ModBlocks.OCCULTIST_TABLE_BLOCK,
                ModBlocks.OAK_HORTICULTURIST_TABLE_BLOCK,
                ModBlocks.DARK_OAK_HORTICULTURIST_TABLE_BLOCK,
                ModBlocks.BIRCH_HORTICULTURIST_TABLE_BLOCK,
                ModBlocks.ACACIA_HORTICULTURIST_TABLE_BLOCK,
                ModBlocks.SPRUCE_HORTICULTURIST_TABLE_BLOCK,
                ModBlocks.JUNGLE_HORTICULTURIST_TABLE_BLOCK,
                ModBlocks.MANGROVE_HORTICULTURIST_TABLE_BLOCK,
                ModBlocks.CRIMSON_HORTICULTURIST_TABLE_BLOCK,
                ModBlocks.WARPED_HORTICULTURIST_TABLE_BLOCK
        );


        BlockEntityRendererRegistry.register(ModBlocks.HORTICULTURIST_TABLE_BLOCK_ENTITY, HorticulturistTableBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlocks.OCEANOGRAPHER_TABLE_BLOCK_ENTITY, OceanographerTableBlockEntityRenderer::new);
    }
}
