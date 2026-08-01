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
    private static final RegistryKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = RegistryKey.of(RegistryKeys.PROCESSOR_LIST, Identifier.of("minecraft", "empty"));
    /** Turns the miner house's chiseled stone into ores - see {@link com.lion.villagersplus.worldgen.OreVeinProcessor}. */
    private static final RegistryKey<StructureProcessorList> MINER_PROCESSOR_LIST_KEY = RegistryKey.of(RegistryKeys.PROCESSOR_LIST, Identifier.of(VillagersPlus.MOD_ID, "miner_ores"));
    private static final Identifier plainsPoolLocation = Identifier.of("minecraft:village/plains/houses");
    private static final Identifier desertPoolLocation = Identifier.of("minecraft:village/desert/houses");
    private static final Identifier savannaPoolLocation = Identifier.of("minecraft:village/savanna/houses");
    private static final Identifier snowyPoolLocation = Identifier.of("minecraft:village/snowy/houses");
    private static final Identifier taigaPoolLocation = Identifier.of("minecraft:village/taiga/houses");

    public static void registerJigsaws(MinecraftServer server) {
        Registry<StructurePool> templatePoolRegistry = server.getRegistryManager().getOrThrow(RegistryKeys.TEMPLATE_POOL);
        Registry<StructureProcessorList> processorListRegistry = server.getRegistryManager().getOrThrow(RegistryKeys.PROCESSOR_LIST);
        RegistryEntry<StructureProcessorList> minerProcessors = processorList(processorListRegistry, MINER_PROCESSOR_LIST_KEY);

        addBuildingToPool(templatePoolRegistry, processorListRegistry, plainsPoolLocation, "villagersplus:village/plains/plains_alchemist", VillagersPlus.CONFIG.plains_alchemist_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, plainsPoolLocation, "villagersplus:village/plains/plains_oceanographer", VillagersPlus.CONFIG.plains_oceanographer_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, plainsPoolLocation, "villagersplus:village/plains/plains_horticulturist", VillagersPlus.CONFIG.plains_horticulturist_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, plainsPoolLocation, "villagersplus:village/plains/plains_occultist", VillagersPlus.CONFIG.plains_occultist_weight);
        addBuildingToPool(templatePoolRegistry, plainsPoolLocation, "villagersplus:village/plains/plains_miner", VillagersPlus.CONFIG.plains_miner_weight, minerProcessors);

        addBuildingToPool(templatePoolRegistry, processorListRegistry, taigaPoolLocation, "villagersplus:village/taiga/taiga_alchemist", VillagersPlus.CONFIG.taiga_alchemist_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, taigaPoolLocation, "villagersplus:village/taiga/taiga_oceanographer", VillagersPlus.CONFIG.taiga_oceanographer_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, taigaPoolLocation, "villagersplus:village/taiga/taiga_horticulturist", VillagersPlus.CONFIG.taiga_horticulturist_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, taigaPoolLocation, "villagersplus:village/taiga/taiga_occultist", VillagersPlus.CONFIG.taiga_occultist_weight);
        addBuildingToPool(templatePoolRegistry, taigaPoolLocation, "villagersplus:village/taiga/taiga_miner", VillagersPlus.CONFIG.taiga_miner_weight, minerProcessors);

        addBuildingToPool(templatePoolRegistry, processorListRegistry, savannaPoolLocation, "villagersplus:village/savanna/savanna_alchemist", VillagersPlus.CONFIG.savanna_alchemist_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, savannaPoolLocation, "villagersplus:village/savanna/savanna_oceanographer", VillagersPlus.CONFIG.savanna_oceanographer_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, savannaPoolLocation, "villagersplus:village/savanna/savanna_horticulturist", VillagersPlus.CONFIG.savanna_horticulturist_weight);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, savannaPoolLocation, "villagersplus:village/savanna/savanna_occultist", VillagersPlus.CONFIG.savanna_occultist_weight);
        addBuildingToPool(templatePoolRegistry, savannaPoolLocation, "villagersplus:village/savanna/savanna_miner", VillagersPlus.CONFIG.savanna_miner_weight, minerProcessors);

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
        addBuildingToPool(templatePoolRegistry, poolRL, nbtPieceRL, weight, processorListRegistry.getOrThrow(EMPTY_PROCESSOR_LIST_KEY));
    }

    /**
     * Looks up a processor list by key, falling back to the empty one. A datapack is free to drop our
     * lists, and a village without ore markers is a far better outcome than a server that will not
     * start.
     */
    private static RegistryEntry<StructureProcessorList> processorList(Registry<StructureProcessorList> registry, RegistryKey<StructureProcessorList> key) {
        return registry.getOptional(key).map(entry -> (RegistryEntry<StructureProcessorList>) entry).orElseGet(() -> {
            VillagersPlus.LOGGER.warn("Processor list {} is missing, placing that building unprocessed", key.getValue());
            return registry.getOrThrow(EMPTY_PROCESSOR_LIST_KEY);
        });
    }

    public static void addBuildingToPool(Registry<StructurePool> templatePoolRegistry, Identifier poolRL, String nbtPieceRL, int weight, RegistryEntry<StructureProcessorList> processorList) {
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
