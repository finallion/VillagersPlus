package com.lion.villagersplus.util;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.mixin.StructurePoolAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class StructurePoolAddition {
    private static final RegistryKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = RegistryKey.of(RegistryKeys.PROCESSOR_LIST, new Identifier("minecraft", "empty"));
    private static final Identifier plainsPoolLocation = new Identifier("minecraft:village/plains/houses");
    private static final Identifier desertPoolLocation = new Identifier("minecraft:village/desert/houses");
    private static final Identifier savannaPoolLocation = new Identifier("minecraft:village/savanna/houses");
    private static final Identifier snowyPoolLocation = new Identifier("minecraft:village/snowy/houses");
    private static final Identifier taigaPoolLocation = new Identifier("minecraft:village/taiga/houses");

    public static void registerJigsaws(MinecraftServer server) {
        Registry<StructurePool> templatePoolRegistry = server.getRegistryManager().get(RegistryKeys.TEMPLATE_POOL);
        Registry<StructureProcessorList> processorListRegistry = server.getRegistryManager().get(RegistryKeys.PROCESSOR_LIST);

        addBuildingToPool(templatePoolRegistry, processorListRegistry, plainsPoolLocation, "villagersplus:village/plains/plains_alchemist", VillagersPlus.CONFIG.plains_alchemist_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, plainsPoolLocation, "villagersplus:village/plains/plains_oceanographer", VillagersPlus.CONFIG.plains_oceanographer_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, plainsPoolLocation, "villagersplus:village/plains/plains_horticulturist", VillagersPlus.CONFIG.plains_horticulturist_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, plainsPoolLocation, "villagersplus:village/plains/plains_occultist", VillagersPlus.CONFIG.plains_occultist_weight);

        addBuildingToPool(templatePoolRegistry, processorListRegistry, taigaPoolLocation, "villagersplus:village/taiga/taiga_alchemist", VillagersPlus.CONFIG.taiga_alchemist_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, taigaPoolLocation, "villagersplus:village/taiga/taiga_oceanographer", VillagersPlus.CONFIG.taiga_oceanographer_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, taigaPoolLocation, "villagersplus:village/taiga/taiga_horticulturist", VillagersPlus.CONFIG.taiga_horticulturist_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, taigaPoolLocation, "villagersplus:village/taiga/taiga_occultist", VillagersPlus.CONFIG.taiga_occultist_weight);

        addBuildingToPool(templatePoolRegistry, processorListRegistry, savannaPoolLocation, "villagersplus:village/savanna/savanna_alchemist", VillagersPlus.CONFIG.savanna_alchemist_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, savannaPoolLocation, "villagersplus:village/savanna/savanna_oceanographer", VillagersPlus.CONFIG.savanna_oceanographer_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, savannaPoolLocation, "villagersplus:village/savanna/savanna_horticulturist", VillagersPlus.CONFIG.savanna_horticulturist_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, savannaPoolLocation, "villagersplus:village/savanna/savanna_occultist", VillagersPlus.CONFIG.savanna_occultist_weight);

        addBuildingToPool(templatePoolRegistry, processorListRegistry, snowyPoolLocation, "villagersplus:village/snowy/snowy_alchemist", VillagersPlus.CONFIG.snowy_alchemist_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, snowyPoolLocation, "villagersplus:village/snowy/snowy_oceanographer", VillagersPlus.CONFIG.snowy_oceanographer_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, snowyPoolLocation, "villagersplus:village/snowy/snowy_horticulturist", VillagersPlus.CONFIG.snowy_horticulturist_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, snowyPoolLocation, "villagersplus:village/snowy/snowy_occultist", VillagersPlus.CONFIG.snowy_occultist_weight);

        addBuildingToPool(templatePoolRegistry, processorListRegistry, desertPoolLocation, "villagersplus:village/desert/desert_alchemist", VillagersPlus.CONFIG.desert_alchemist_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, desertPoolLocation, "villagersplus:village/desert/desert_oceanographer", VillagersPlus.CONFIG.desert_oceanographer_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, desertPoolLocation, "villagersplus:village/desert/desert_horticulturist", VillagersPlus.CONFIG.desert_horticulturist_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, desertPoolLocation, "villagersplus:village/desert/desert_occultist", VillagersPlus.CONFIG.desert_occultist_weight);
    }

    public static void addBuildingToPool(Registry<StructurePool> templatePoolRegistry, Registry<StructureProcessorList> processorListRegistry, Identifier poolRL, String nbtPieceRL, int weight) {
        RegistryEntry<StructureProcessorList> processorList = processorListRegistry.entryOf(EMPTY_PROCESSOR_LIST_KEY);

        StructurePool pool = templatePoolRegistry.get(poolRL);
        if (pool == null) return;

        SinglePoolElement piece = SinglePoolElement.ofProcessedSingle(nbtPieceRL, processorList).apply(StructurePool.Projection.RIGID);

        for (int i = 0; i < weight; i++) {
            ((StructurePoolAccessor) pool).getTemplates().add(piece);
        }

        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(((StructurePoolAccessor) pool).getRawTemplates());
        listOfPieceEntries.add(new Pair<>(piece, weight));
        ((StructurePoolAccessor) pool).setRawTemplates(listOfPieceEntries);
    }
}
