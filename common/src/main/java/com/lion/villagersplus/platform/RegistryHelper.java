package com.lion.villagersplus.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import java.util.function.Supplier;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class RegistryHelper {

    @ExpectPlatform
    public static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> block) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends BlockEntityType<?>> Supplier<T> registerBlockEntity(String name, Supplier<T> blockEntity) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerItemGroup(ResourceKey<CreativeModeTab> registryKey, String name, String literalName, Supplier<Item> item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void addToItemGroup(ResourceKey<CreativeModeTab> itemGroup, Item item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerParticleType(String name, SimpleParticleType particleType) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerScreenHandlerType(String name, MenuType<?> screenHandlerType) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends BlockEntity, S extends BlockEntityRenderState> void registerBlockEntityRenderer(Supplier<BlockEntityType<T>> type, BlockEntityRendererProvider<T, S> renderProvider) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends PoiType> Supplier<T> registerPointOfInterestType(String name, Supplier<T> pointOfInterestType) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends VillagerProfession> Supplier<T> registerVillagerProfession(String name, Supplier<T> villagerProfession) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerStructureProcessorType(String name, StructureProcessorType<?> structureProcessorType) {
        throw new AssertionError();
    }
}
