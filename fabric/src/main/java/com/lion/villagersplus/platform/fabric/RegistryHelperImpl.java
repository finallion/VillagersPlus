package com.lion.villagersplus.platform.fabric;

import com.lion.villagersplus.VillagersPlus;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.function.Supplier;

public class RegistryHelperImpl {

    public static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> block) {
        var registry = Registry.register(Registries.BLOCK, new Identifier(VillagersPlus.MOD_ID, name), block.get());
        return () -> registry;
    }

    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> item) {
        var registry = Registry.register(Registries.ITEM, new Identifier(VillagersPlus.MOD_ID, name), item.get());
        return () -> registry;
    }

    public static void registerItemGroup(RegistryKey<ItemGroup> registryKey, String name, String literalName, Supplier<Item> item) {
        Registry.register(Registries.ITEM_GROUP, registryKey,
                FabricItemGroup.builder()
                        .displayName(Text.literal(literalName))
                        .icon(() -> new ItemStack(item.get())).build());
    }

    public static void addToItemGroup(RegistryKey<ItemGroup> itemGroup, Item item) {
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register((content) -> content.add(item.getDefaultStack()));
    }

    public static <T extends BlockEntityType<?>> Supplier<T> registerBlockEntity(String name, Supplier<T> blockEntity) {
        var registry = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(VillagersPlus.MOD_ID, name), blockEntity.get());
        return () -> registry;
    }

    public static void registerParticleType(String name, DefaultParticleType particleType) {
        Registry.register(Registries.PARTICLE_TYPE, VillagersPlus.createStringID(name), particleType);
    }


    public static void registerScreenHandlerType(String name, ScreenHandlerType<?> screenHandlerType) {
        Registry.register(Registries.SCREEN_HANDLER, new Identifier(VillagersPlus.MOD_ID, name), screenHandlerType);
    }

    public static void registerRenderType(RenderLayer type, Block... blocks) {
        BlockRenderLayerMap.INSTANCE.putBlocks(type, blocks);
    }

    public static <T extends SoundEvent> Supplier<T> registerSoundEvent(String name, Supplier<T> soundEvent) {
        var registry = Registry.register(Registries.SOUND_EVENT, new Identifier(VillagersPlus.MOD_ID, name), soundEvent.get());
        return () -> registry;
    }

    public static <T extends BlockEntity> void registerBlockEntityRenderer(Supplier<BlockEntityType<T>> type, BlockEntityRendererFactory<T> renderProvider) {
        BlockEntityRendererRegistry.register(type.get(), renderProvider);
    }

    public static <T extends PointOfInterestType> Supplier<T> registerPointOfInterestType(String name, Supplier<T> pointOfInterestType) {
        var registry = Registry.register(Registries.POINT_OF_INTEREST_TYPE, new Identifier(VillagersPlus.MOD_ID, name), pointOfInterestType.get());
        return () -> registry;
    }

    public static <T extends VillagerProfession> Supplier<T> registerVillagerProfession(String name, Supplier<T> villagerProfession) {
        var registry = Registry.register(Registries.VILLAGER_PROFESSION, new Identifier(VillagersPlus.MOD_ID, name), villagerProfession.get());
        return () -> registry;
    }



}
