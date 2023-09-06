package com.lion.villagersplus.platform.forge;

import com.lion.villagersplus.VillagersPlus;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class RegistryHelperImpl {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, VillagersPlus.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, VillagersPlus.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VillagersPlus.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, VillagersPlus.MOD_ID);
    public static final DeferredRegister<ScreenHandlerType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, VillagersPlus.MOD_ID);
    public static final DeferredRegister<ItemGroup> CREATIVE_TABS = DeferredRegister.create(RegistryKeys.ITEM_GROUP, VillagersPlus.MOD_ID);
    public static final DeferredRegister<PointOfInterestType> POINT_OF_INTEREST_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, VillagersPlus.MOD_ID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, VillagersPlus.MOD_ID);

    public static final HashMap<RegistryKey<ItemGroup>, List<Item>> ITEMS_TO_ADD = new HashMap<>();

    public static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    public static <T extends BlockEntityType<?>> Supplier<T> registerBlockEntity(String name, Supplier<T> blockEntity) {
        return TILE_ENTITIES.register(name, blockEntity);
    }

    public static void registerItemGroup(RegistryKey<ItemGroup> registryKey, String name, String literalName, Supplier<Item> item) {
        CREATIVE_TABS.register(name, () -> new ItemGroup.Builder(ItemGroup.Row.TOP,0)
                .icon(() -> new ItemStack(item.get()))
                .displayName(Text.literal(literalName)).build());
    }

    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
    public static void addToItemGroup(RegistryKey<ItemGroup> itemGroup, Item item) {
        if (ITEMS_TO_ADD.containsKey(itemGroup)) {
            ITEMS_TO_ADD.get(itemGroup).add(item);
        } else {
            List<Item> items = new ArrayList<>();
            items.add(item);
            ITEMS_TO_ADD.put(itemGroup, items);
        }
    }

    public static void registerParticleType(String name, DefaultParticleType particleType) {
        PARTICLES.register(name, () -> particleType);
    }

    public static void registerScreenHandlerType(String name, ScreenHandlerType<?> screenHandlerType) {
        MENUS.register(name, () -> screenHandlerType);
    }

    public static void registerRenderType(RenderLayer type, Block... blocks) {
        // done block model JSON
    }


    public static <T extends BlockEntity> void registerBlockEntityRenderer(Supplier<BlockEntityType<T>> type, BlockEntityRendererFactory<T> renderProvider) {
        // registered via event
    }


    public static <T extends VillagerProfession> Supplier<T> registerVillagerProfession(String name, Supplier<T> villagerProfession) {
        return VILLAGER_PROFESSIONS.register(name, villagerProfession);
    }

    public static <T extends PointOfInterestType> Supplier<T> registerPointOfInterestType(String name, Supplier<T> pointOfInterestType) {
        return POINT_OF_INTEREST_TYPES.register(name, pointOfInterestType);
    }

}
