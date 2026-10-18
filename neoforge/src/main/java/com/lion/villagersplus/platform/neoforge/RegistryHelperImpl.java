package com.lion.villagersplus.platform.neoforge;

import com.mojang.serialization.MapCodec;
import com.lion.villagersplus.VillagersPlus;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class RegistryHelperImpl {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, VillagersPlus.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, VillagersPlus.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, VillagersPlus.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, VillagersPlus.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, VillagersPlus.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VillagersPlus.MOD_ID);
    public static final DeferredRegister<PoiType> POINT_OF_INTEREST_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, VillagersPlus.MOD_ID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(Registries.VILLAGER_PROFESSION, VillagersPlus.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends StructureProcessor>> STRUCTURE_PROCESSOR_TYPES = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, VillagersPlus.MOD_ID);

    public static final HashMap<ResourceKey<CreativeModeTab>, List<Item>> ITEMS_TO_ADD = new HashMap<>();

    public static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    public static <T extends BlockEntityType<?>> Supplier<T> registerBlockEntity(String name, Supplier<T> blockEntity) {
        return TILE_ENTITIES.register(name, blockEntity);
    }

    public static void registerItemGroup(ResourceKey<CreativeModeTab> registryKey, String name, String literalName, Supplier<Item> item) {
        CREATIVE_TABS.register(name, () -> new CreativeModeTab.Builder(CreativeModeTab.Row.TOP,0)
                .icon(() -> new ItemStack(item.get()))
                .title(Component.literal(literalName)).build());
    }

    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
    public static void addToItemGroup(ResourceKey<CreativeModeTab> itemGroup, Item item) {
        if (ITEMS_TO_ADD.containsKey(itemGroup)) {
            ITEMS_TO_ADD.get(itemGroup).add(item);
        } else {
            List<Item> items = new ArrayList<>();
            items.add(item);
            ITEMS_TO_ADD.put(itemGroup, items);
        }
    }

    public static void registerParticleType(String name, SimpleParticleType particleType) {
        PARTICLES.register(name, () -> particleType);
    }

    public static void registerScreenHandlerType(String name, MenuType<?> screenHandlerType) {
        MENUS.register(name, () -> screenHandlerType);
    }

    public static <T extends BlockEntity, S extends BlockEntityRenderState> void registerBlockEntityRenderer(Supplier<BlockEntityType<T>> type, BlockEntityRendererProvider<T, S> renderProvider) {
    }

    public static <T extends VillagerProfession> Supplier<T> registerVillagerProfession(String name, Supplier<T> villagerProfession) {
        return VILLAGER_PROFESSIONS.register(name, villagerProfession);
    }

    public static <T extends PoiType> Supplier<T> registerPointOfInterestType(String name, Supplier<T> pointOfInterestType) {
        return POINT_OF_INTEREST_TYPES.register(name, pointOfInterestType);
    }

    public static void registerStructureProcessorType(String name, MapCodec<? extends StructureProcessor> structureProcessorCodec) {
        STRUCTURE_PROCESSOR_TYPES.register(name, () -> structureProcessorCodec);
    }

}
