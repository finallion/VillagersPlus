package com.lion.villagersplus.init;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.platform.RegistryHelper;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BedPart;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Supplier;

public class VPPointOfInterestTypes {
    private static final HashMap<String, Supplier<PointOfInterestType>> REGISTERED_POINT_OF_INTEREST_TYPES;

    public final static Supplier<PointOfInterestType> HORTICULTURIST_WARPED_POI;
    public final static Supplier<PointOfInterestType> HORTICULTURIST_CRIMSON_POI;
    public final static Supplier<PointOfInterestType> HORTICULTURIST_OAK_POI;
    public final static Supplier<PointOfInterestType> HORTICULTURIST_DARK_OAK_POI;
    public final static Supplier<PointOfInterestType> HORTICULTURIST_BIRCH_POI;
    public final static Supplier<PointOfInterestType> HORTICULTURIST_JUNGLE_POI;
    public final static Supplier<PointOfInterestType> HORTICULTURIST_ACACIA_POI;
    public final static Supplier<PointOfInterestType> HORTICULTURIST_SPRUCE_POI;
    public final static Supplier<PointOfInterestType> HORTICULTURIST_CHERRY_POI;
    public final static Supplier<PointOfInterestType> HORTICULTURIST_BAMBOO_POI;
    public final static Supplier<PointOfInterestType> HORTICULTURIST_MANGROVE_POI;
    public final static Supplier<PointOfInterestType> OCCULTIST_POI;
    public final static Supplier<PointOfInterestType> OCEANOGRAPHER_POI;
    public final static Supplier<PointOfInterestType> ALCHEMIST_POI;

    static {
        REGISTERED_POINT_OF_INTEREST_TYPES = new HashMap<>();


        HORTICULTURIST_WARPED_POI = registerPointOfInterest("horticulturist_warped", () -> new PointOfInterestType(PointOfInterestTypes.getStatesOfBlock(VPBlocks.WARPED_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_CRIMSON_POI = registerPointOfInterest("horticulturist_crimson", () -> new PointOfInterestType(PointOfInterestTypes.getStatesOfBlock(VPBlocks.CRIMSON_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_OAK_POI = registerPointOfInterest("horticulturist_oak", () -> new PointOfInterestType(PointOfInterestTypes.getStatesOfBlock(VPBlocks.OAK_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_DARK_OAK_POI = registerPointOfInterest("horticulturist_dark_oak", () -> new PointOfInterestType(PointOfInterestTypes.getStatesOfBlock(VPBlocks.DARK_OAK_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_BIRCH_POI = registerPointOfInterest("horticulturist_birch", () -> new PointOfInterestType(PointOfInterestTypes.getStatesOfBlock(VPBlocks.BIRCH_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_JUNGLE_POI = registerPointOfInterest("horticulturist_jungle", () -> new PointOfInterestType(PointOfInterestTypes.getStatesOfBlock(VPBlocks.JUNGLE_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_ACACIA_POI = registerPointOfInterest("horticulturist_acacia", () -> new PointOfInterestType(PointOfInterestTypes.getStatesOfBlock(VPBlocks.ACACIA_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_SPRUCE_POI = registerPointOfInterest("horticulturist_spruce", () -> new PointOfInterestType(PointOfInterestTypes.getStatesOfBlock(VPBlocks.SPRUCE_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_CHERRY_POI = registerPointOfInterest("horticulturist_cherry", () -> new PointOfInterestType(PointOfInterestTypes.getStatesOfBlock(VPBlocks.CHERRY_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_BAMBOO_POI = registerPointOfInterest("horticulturist_bamboo", () -> new PointOfInterestType(PointOfInterestTypes.getStatesOfBlock(VPBlocks.BAMBOO_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));
        HORTICULTURIST_MANGROVE_POI = registerPointOfInterest("horticulturist_mangrove", () -> new PointOfInterestType(PointOfInterestTypes.getStatesOfBlock(VPBlocks.MANGROVE_HORTICULTURIST_TABLE_BLOCK.get()), 1, 1));

        OCCULTIST_POI = registerPointOfInterest("occultist", () -> new PointOfInterestType(PointOfInterestTypes.getStatesOfBlock(VPBlocks.OCCULTIST_TABLE_BLOCK.get()), 1, 1));
        OCEANOGRAPHER_POI = registerPointOfInterest("oceanographer", () -> new PointOfInterestType(PointOfInterestTypes.getStatesOfBlock(VPBlocks.OCEANOGRAPHER_TABLE_BLOCK.get()), 1, 1));
        ALCHEMIST_POI = registerPointOfInterest("alchemist", () -> new PointOfInterestType(PointOfInterestTypes.getStatesOfBlock(VPBlocks.ALCHEMIST_TABLE_BLOCK.get()), 1, 1));
    }

    public static void init() {
    }

    public static void postInit() {
        fillMissingPointOfInterestMapValues();
    }

    private static Supplier<PointOfInterestType> registerPointOfInterest(String name, Supplier<PointOfInterestType> pointOfInterestType) {
        REGISTERED_POINT_OF_INTEREST_TYPES.put(name, pointOfInterestType);
        return RegistryHelper.registerPointOfInterestType(name, pointOfInterestType);
    }

    private static void fillMissingPointOfInterestMapValues() {
        REGISTERED_POINT_OF_INTEREST_TYPES.forEach((name, pointOfInterestType) -> fillMissingPointOfInterestMapValueForBlock(name, pointOfInterestType.get().blockStates().iterator().next().getBlock()));
    }

    private static void fillMissingPointOfInterestMapValueForBlock(String name, Block pointOfInterestBlock) {
        var blockStates = PointOfInterestTypes.getStatesOfBlock(pointOfInterestBlock);
        blockStates.forEach((state) -> PointOfInterestTypes.POI_STATES_TO_TYPE.put(state, Registries.POINT_OF_INTEREST_TYPE.getEntry(RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, new Identifier(VillagersPlus.MOD_ID, name))).get()));
    }
}
