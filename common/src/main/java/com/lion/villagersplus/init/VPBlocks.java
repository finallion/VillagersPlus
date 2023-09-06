package com.lion.villagersplus.init;

import com.lion.villagersplus.blocks.AlchemistTableBlock;
import com.lion.villagersplus.blocks.HorticulturistTableBlock;
import com.lion.villagersplus.blocks.OccultistTableBlock;
import com.lion.villagersplus.blocks.OceanographerTableBlock;
import com.lion.villagersplus.platform.RegistryHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

import java.util.function.Supplier;

public class VPBlocks {

    public static final Supplier<Block> ALCHEMIST_TABLE_BLOCK = RegistryHelper.registerBlock("alchemist_table", () -> new AlchemistTableBlock(AbstractBlock.Settings.create().strength(0.5F).luminance((state) -> 1).nonOpaque()));
    public static final Supplier<Block> OCEANOGRAPHER_TABLE_BLOCK = RegistryHelper.registerBlock("oceanographer_table", () -> new OceanographerTableBlock(AbstractBlock.Settings.create().strength(0.5F).luminance((state) -> 12).nonOpaque().solidBlock((state, world, pos) -> false).suffocates((state, world, pos) -> false)));
    public static final Supplier<Block> OAK_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("oak_horticulturist_table", () -> new HorticulturistTableBlock(AbstractBlock.Settings.create().strength(0.5F).nonOpaque().burnable()));
    public static final Supplier<Block> DARK_OAK_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("dark_oak_horticulturist_table", () -> new HorticulturistTableBlock(AbstractBlock.Settings.create().strength(0.5F).nonOpaque().burnable()));
    public static final Supplier<Block> ACACIA_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("acacia_horticulturist_table", () -> new HorticulturistTableBlock(AbstractBlock.Settings.create().strength(0.5F).nonOpaque().burnable()));
    public static final Supplier<Block> JUNGLE_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("jungle_horticulturist_table", () -> new HorticulturistTableBlock(AbstractBlock.Settings.create().strength(0.5F).nonOpaque().burnable()));
    public static final Supplier<Block> SPRUCE_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("spruce_horticulturist_table", () -> new HorticulturistTableBlock(AbstractBlock.Settings.create().strength(0.5F).nonOpaque().burnable()));
    public static final Supplier<Block> BIRCH_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("birch_horticulturist_table", () -> new HorticulturistTableBlock(AbstractBlock.Settings.create().strength(0.5F).nonOpaque().burnable()));
    public static final Supplier<Block> MANGROVE_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("mangrove_horticulturist_table", () -> new HorticulturistTableBlock(AbstractBlock.Settings.create().strength(0.5F).nonOpaque().burnable()));
    public static final Supplier<Block> CRIMSON_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("crimson_horticulturist_table", () -> new HorticulturistTableBlock(AbstractBlock.Settings.create().strength(0.5F).nonOpaque().burnable()));
    public static final Supplier<Block> WARPED_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("warped_horticulturist_table", () -> new HorticulturistTableBlock(AbstractBlock.Settings.create().strength(0.5F).nonOpaque().burnable()));
    public static final Supplier<Block> CHERRY_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("cherry_horticulturist_table", () -> new HorticulturistTableBlock(AbstractBlock.Settings.create().strength(0.5F).nonOpaque().burnable()));
    public static final Supplier<Block> BAMBOO_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("bamboo_horticulturist_table", () -> new HorticulturistTableBlock(AbstractBlock.Settings.create().strength(0.5F).nonOpaque().burnable()));
    public static final Supplier<Block> OCCULTIST_TABLE_BLOCK = RegistryHelper.registerBlock("occultist_table", () -> new OccultistTableBlock(AbstractBlock.Settings.create().strength(0.5F).luminance((state) -> state.get(OccultistTableBlock.FILLING) * 2).nonOpaque()));

    public static void init() {

    }

}
