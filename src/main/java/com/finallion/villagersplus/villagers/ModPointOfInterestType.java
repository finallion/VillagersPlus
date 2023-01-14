package com.finallion.villagersplus.villagers;

import com.finallion.villagersplus.VillagersPlus;
import com.finallion.villagersplus.init.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;


public class ModPointOfInterestType {

    public static PointOfInterestType HORTICULTURIST_POI;
    public static PointOfInterestType OCCULTIST_POI;
    public static PointOfInterestType OCEANOGRAPHER_POI;
    public static PointOfInterestType ALCHEMIST_POI;

    public static void registerPOIs() {
        HORTICULTURIST_POI = PointOfInterestHelper.register(new Identifier(VillagersPlus.MOD_ID,"horticulturist"), 1, 1,
                ModBlocks.WARPED_HORTICULTURIST_TABLE_BLOCK,
                ModBlocks.CRIMSON_HORTICULTURIST_TABLE_BLOCK,
                ModBlocks.OAK_HORTICULTURIST_TABLE_BLOCK,
                ModBlocks.DARK_OAK_HORTICULTURIST_TABLE_BLOCK,
                ModBlocks.BIRCH_HORTICULTURIST_TABLE_BLOCK,
                ModBlocks.JUNGLE_HORTICULTURIST_TABLE_BLOCK,
                ModBlocks.ACACIA_HORTICULTURIST_TABLE_BLOCK,
                ModBlocks.SPRUCE_HORTICULTURIST_TABLE_BLOCK,
                ModBlocks.MANGROVE_HORTICULTURIST_TABLE_BLOCK);
        OCCULTIST_POI = PointOfInterestHelper.register(new Identifier(VillagersPlus.MOD_ID,"occultist"), 1, 1, ModBlocks.OCCULTIST_TABLE_BLOCK);
        OCEANOGRAPHER_POI = PointOfInterestHelper.register(new Identifier(VillagersPlus.MOD_ID,"oceanographer"), 1, 1, ModBlocks.OCEANOGRAPHER_TABLE_BLOCK);
        ALCHEMIST_POI = PointOfInterestHelper.register(new Identifier(VillagersPlus.MOD_ID,"alchemist"), 1, 1, ModBlocks.ALCHEMIST_TABLE_BLOCK);
    }


}
