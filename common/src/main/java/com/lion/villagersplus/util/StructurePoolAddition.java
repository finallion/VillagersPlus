package com.lion.villagersplus.util;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.mixin.StructurePoolAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import java.util.ArrayList;
import java.util.List;

public class StructurePoolAddition {
    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(Registries.PROCESSOR_LIST, Identifier.fromNamespaceAndPath("minecraft", "empty"));
    /// Replaces the chiseled stone of the miner house with ores. See [OreVeinProcessor].
    private static final ResourceKey<StructureProcessorList> MINER_PROCESSOR_LIST_KEY = ResourceKey.create(Registries.PROCESSOR_LIST, Identifier.fromNamespaceAndPath(VillagersPlus.MOD_ID, "miner_ores"));
    private static final Identifier plainsPoolLocation = Identifier.parse("minecraft:village/plains/houses");
    private static final Identifier desertPoolLocation = Identifier.parse("minecraft:village/desert/houses");
    private static final Identifier savannaPoolLocation = Identifier.parse("minecraft:village/savanna/houses");
    private static final Identifier snowyPoolLocation = Identifier.parse("minecraft:village/snowy/houses");
    private static final Identifier taigaPoolLocation = Identifier.parse("minecraft:village/taiga/houses");

    public static void registerJigsaws(MinecraftServer server) {
        Registry<StructureTemplatePool> templatePoolRegistry = server.registryAccess().lookupOrThrow(Registries.TEMPLATE_POOL);
        Registry<StructureProcessorList> processorListRegistry = server.registryAccess().lookupOrThrow(Registries.PROCESSOR_LIST);
        Holder<StructureProcessorList> minerProcessors = processorList(processorListRegistry, MINER_PROCESSOR_LIST_KEY);

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

    public static void addBuildingToPool(
            Registry<StructureTemplatePool> templatePoolRegistry,
            Registry<StructureProcessorList> processorListRegistry,
            Identifier poolRL,
            String nbtPieceRL,
            int weight
    ) {
        addBuildingToPool(templatePoolRegistry, poolRL, nbtPieceRL, weight, processorListRegistry.getOrThrow(EMPTY_PROCESSOR_LIST_KEY));
    }

    /// Looks up a processor list, falling back to the empty one when a datapack removed ours.
    private static Holder<StructureProcessorList> processorList(
            Registry<StructureProcessorList> registry,
            ResourceKey<StructureProcessorList> key
    ) {
        return registry.get(key).map(entry -> (Holder<StructureProcessorList>) entry).orElseGet(() -> {
            VillagersPlus.LOGGER.warn("Processor list {} is missing, placing that building unprocessed", key.identifier());
            return registry.getOrThrow(EMPTY_PROCESSOR_LIST_KEY);
        });
    }

    public static void addBuildingToPool(
            Registry<StructureTemplatePool> templatePoolRegistry,
            Identifier poolRL,
            String nbtPieceRL,
            int weight,
            Holder<StructureProcessorList> processorList
    ) {
        StructureTemplatePool pool = templatePoolRegistry.getValue(poolRL);

        if (pool == null) {
            return;
        }

        SinglePoolElement piece = SinglePoolElement.single(nbtPieceRL, processorList).apply(StructureTemplatePool.Projection.RIGID);

        for (int i = 0; i < weight; i++) {
            ((StructurePoolAccessor) pool).getTemplates().add(piece);
        }

        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(((StructurePoolAccessor) pool).getRawTemplates());
        listOfPieceEntries.add(new Pair<>(piece, weight));
        ((StructurePoolAccessor) pool).setRawTemplates(listOfPieceEntries);
    }
}
