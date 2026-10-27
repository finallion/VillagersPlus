package com.lion.villagersplus.init;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.platform.RegistryHelper;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.Block;

public class VPPointOfInterestTypes {
    private static final HashMap<String, Supplier<PoiType>> REGISTERED_POINT_OF_INTEREST_TYPES;

    public final static Supplier<PoiType> HORTICULTURIST_WARPED_POI;
    public final static Supplier<PoiType> HORTICULTURIST_CRIMSON_POI;
    public final static Supplier<PoiType> HORTICULTURIST_OAK_POI;
    public final static Supplier<PoiType> HORTICULTURIST_DARK_OAK_POI;
    public final static Supplier<PoiType> HORTICULTURIST_BIRCH_POI;
    public final static Supplier<PoiType> HORTICULTURIST_JUNGLE_POI;
    public final static Supplier<PoiType> HORTICULTURIST_ACACIA_POI;
    public final static Supplier<PoiType> HORTICULTURIST_SPRUCE_POI;
    public final static Supplier<PoiType> HORTICULTURIST_CHERRY_POI;
    public final static Supplier<PoiType> HORTICULTURIST_BAMBOO_POI;
    public final static Supplier<PoiType> HORTICULTURIST_MANGROVE_POI;
    public final static Supplier<PoiType> HORTICULTURIST_PALE_OAK_POI;
    public final static Supplier<PoiType> HORTICULTURIST_POPLAR_POI;
    public final static Supplier<PoiType> OCCULTIST_POI;
    public final static Supplier<PoiType> OCEANOGRAPHER_POI;
    public final static Supplier<PoiType> ALCHEMIST_POI;
    public final static Supplier<PoiType> MINER_POI;

    static {
        REGISTERED_POINT_OF_INTEREST_TYPES = new HashMap<>();

        HORTICULTURIST_WARPED_POI = registerPointOfInterest("horticulturist_warped", () -> new PoiType(PoiTypes.getBlockStates(VPBlocks.WARPED_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_CRIMSON_POI = registerPointOfInterest("horticulturist_crimson", () -> new PoiType(PoiTypes.getBlockStates(VPBlocks.CRIMSON_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_OAK_POI = registerPointOfInterest("horticulturist_oak", () -> new PoiType(PoiTypes.getBlockStates(VPBlocks.OAK_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_DARK_OAK_POI = registerPointOfInterest("horticulturist_dark_oak", () -> new PoiType(PoiTypes.getBlockStates(VPBlocks.DARK_OAK_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_BIRCH_POI = registerPointOfInterest("horticulturist_birch", () -> new PoiType(PoiTypes.getBlockStates(VPBlocks.BIRCH_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_JUNGLE_POI = registerPointOfInterest("horticulturist_jungle", () -> new PoiType(PoiTypes.getBlockStates(VPBlocks.JUNGLE_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_ACACIA_POI = registerPointOfInterest("horticulturist_acacia", () -> new PoiType(PoiTypes.getBlockStates(VPBlocks.ACACIA_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_SPRUCE_POI = registerPointOfInterest("horticulturist_spruce", () -> new PoiType(PoiTypes.getBlockStates(VPBlocks.SPRUCE_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_CHERRY_POI = registerPointOfInterest("horticulturist_cherry", () -> new PoiType(PoiTypes.getBlockStates(VPBlocks.CHERRY_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_BAMBOO_POI = registerPointOfInterest("horticulturist_bamboo", () -> new PoiType(PoiTypes.getBlockStates(VPBlocks.BAMBOO_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_MANGROVE_POI = registerPointOfInterest("horticulturist_mangrove", () -> new PoiType(PoiTypes.getBlockStates(VPBlocks.MANGROVE_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_PALE_OAK_POI = registerPointOfInterest("horticulturist_pale_oak", () -> new PoiType(PoiTypes.getBlockStates(VPBlocks.PALE_OAK_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_POPLAR_POI = registerPointOfInterest("horticulturist_poplar", () -> new PoiType(PoiTypes.getBlockStates(VPBlocks.POPLAR_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));

        OCCULTIST_POI = registerPointOfInterest("occultist", () -> new PoiType(PoiTypes.getBlockStates(VPBlocks.OCCULTIST_TABLE_BLOCK.get()), 1, 1));
        OCEANOGRAPHER_POI = registerPointOfInterest("oceanographer", () -> new PoiType(PoiTypes.getBlockStates(VPBlocks.OCEANOGRAPHER_TABLE_BLOCK.get()), 1, 1));
        ALCHEMIST_POI = registerPointOfInterest("alchemist", () -> new PoiType(PoiTypes.getBlockStates(VPBlocks.ALCHEMIST_TABLE_BLOCK.get()), 1, 1));
        MINER_POI = registerPointOfInterest("miner", () -> new PoiType(PoiTypes.getBlockStates(VPBlocks.ORE_GRINDER_BLOCK.get()), 1, 1));
    }

    public static void init() {
    }

    public static void postInit() {
        fillMissingPointOfInterestMapValues();
    }

    private static Supplier<PoiType> registerPointOfInterest(String name, Supplier<PoiType> pointOfInterestType) {
        REGISTERED_POINT_OF_INTEREST_TYPES.put(name, pointOfInterestType);
        return RegistryHelper.registerPointOfInterestType(name, pointOfInterestType);
    }

    private static void fillMissingPointOfInterestMapValues() {
        REGISTERED_POINT_OF_INTEREST_TYPES.forEach((name, pointOfInterestType) -> fillMissingPointOfInterestMapValueForBlock(name, pointOfInterestType.get().matchingStates().iterator().next().getBlock()));
    }

    private static void fillMissingPointOfInterestMapValueForBlock(String name, Block pointOfInterestBlock) {
        var blockStates = PoiTypes.getBlockStates(pointOfInterestBlock);
        blockStates.forEach((state) -> PoiTypes.TYPE_BY_STATE.put(state, BuiltInRegistries.POINT_OF_INTEREST_TYPE.get(ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, Identifier.fromNamespaceAndPath(VillagersPlus.MOD_ID, name))).get()));
    }
}
