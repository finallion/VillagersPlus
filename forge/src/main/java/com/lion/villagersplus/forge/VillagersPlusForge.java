package com.lion.villagersplus.forge;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.VillagersPlusClient;
import com.lion.villagersplus.platform.forge.DefaultTradeOfferResourceListener;
import com.lion.villagersplus.platform.forge.RegistryHelperImpl;
import com.lion.villagersplus.platform.forge.TradeOfferResourceListener;
import com.lion.villagersplus.util.StructurePoolAddition;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(VillagersPlus.MOD_ID)
public class VillagersPlusForge {

    public VillagersPlusForge() {
        VillagersPlus.init();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            VillagersPlusClient.init();
        }

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        RegistryHelperImpl.BLOCKS.register(bus);
        RegistryHelperImpl.ITEMS.register(bus);
        RegistryHelperImpl.TILE_ENTITIES.register(bus);
        RegistryHelperImpl.PARTICLES.register(bus);
        RegistryHelperImpl.CREATIVE_TABS.register(bus);
        RegistryHelperImpl.MENUS.register(bus);
        RegistryHelperImpl.POINT_OF_INTEREST_TYPES.register(bus);
        RegistryHelperImpl.VILLAGER_PROFESSIONS.register(bus);

        bus.addListener(VillagersPlusForge::init);
        bus.addListener(VillagersPlusForge::addItemsToTabs);

        forgeBus.register(this);
        forgeBus.addListener(VillagersPlusForge::registerResourceReloader);
    }

    private static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(VillagersPlus::postInit);
    }

    @SubscribeEvent
    public void onServerAboutToStartEvent(ServerAboutToStartEvent event) {
        StructurePoolAddition.registerJigsaws(event.getServer());
    }

    @SubscribeEvent
    public static void registerResourceReloader(AddReloadListenerEvent event) {
        event.addListener(new DefaultTradeOfferResourceListener());
        event.addListener(new TradeOfferResourceListener());
    }

    @SubscribeEvent
    public static void addItemsToTabs(BuildCreativeModeTabContentsEvent event) {
        RegistryHelperImpl.ITEMS_TO_ADD.forEach((itemGroup, itemPairs) -> {
            if (event.getTabKey() == itemGroup) {
                itemPairs.forEach(item -> {
                    event.getEntries().put(item.getDefaultStack(), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
                });
            }
        });
    }
}
