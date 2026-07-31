package com.lion.villagersplus.neoforge;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.VillagersPlusClient;
import com.lion.villagersplus.platform.neoforge.RegistryHelperImpl;
import com.lion.villagersplus.util.StructurePoolAddition;
import net.minecraft.item.ItemGroup;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@Mod(VillagersPlus.MOD_ID)
public class VillagersPlusNeoForge {

    /**
     * NeoForge hands the mod bus to the constructor; there is no FMLJavaModLoadingContext lookup any
     * more. The two buses stay distinct: registries and setup are mod-bus, server lifecycle is game-bus.
     */
    public VillagersPlusNeoForge(IEventBus modEventBus) {
        VillagersPlus.init();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            VillagersPlusClient.init();
        }

        RegistryHelperImpl.BLOCKS.register(modEventBus);
        RegistryHelperImpl.ITEMS.register(modEventBus);
        RegistryHelperImpl.TILE_ENTITIES.register(modEventBus);
        RegistryHelperImpl.PARTICLES.register(modEventBus);
        RegistryHelperImpl.CREATIVE_TABS.register(modEventBus);
        RegistryHelperImpl.MENUS.register(modEventBus);
        RegistryHelperImpl.POINT_OF_INTEREST_TYPES.register(modEventBus);
        RegistryHelperImpl.VILLAGER_PROFESSIONS.register(modEventBus);
        RegistryHelperImpl.STRUCTURE_PROCESSOR_TYPES.register(modEventBus);

        modEventBus.addListener(VillagersPlusNeoForge::init);
        modEventBus.addListener(VillagersPlusNeoForge::addItemsToTabs);

        NeoForge.EVENT_BUS.register(this);
    }

    private static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(VillagersPlus::postInit);
    }

    @SubscribeEvent
    public void onServerAboutToStartEvent(ServerAboutToStartEvent event) {
        StructurePoolAddition.registerJigsaws(event.getServer());
    }

    /** Registered explicitly via addListener above, so it needs no annotation. */
    private static void addItemsToTabs(BuildCreativeModeTabContentsEvent event) {
        RegistryHelperImpl.ITEMS_TO_ADD.forEach((itemGroup, itemPairs) -> {
            if (event.getTabKey() == itemGroup) {
                // The event is an ItemGroup.Entries itself now, so entries go in directly.
                itemPairs.forEach(item ->
                        event.add(item.getDefaultStack(), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS));
            }
        });
    }
}
