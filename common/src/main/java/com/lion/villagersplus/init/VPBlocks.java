package com.lion.villagersplus.init;

import com.lion.villagersplus.blocks.AlchemistTableBlock;
import com.lion.villagersplus.blocks.HorticulturistTableBlock;
import com.lion.villagersplus.blocks.OccultistTableBlock;
import com.lion.villagersplus.blocks.OceanographerTableBlock;
import com.lion.villagersplus.blocks.OreGrinderBlock;
import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.platform.RegistryHelper;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class VPBlocks {

    /// Every block needs its own registry key in the settings, or registration throws "Block id not set".
    private static BlockBehaviour.Properties settings(String name) {
        return BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(VillagersPlus.MOD_ID, name)));
    }

    public static final Supplier<Block> ALCHEMIST_TABLE_BLOCK = RegistryHelper.registerBlock("alchemist_table", () -> new AlchemistTableBlock(settings("alchemist_table").strength(0.5F).lightLevel((state) -> 1).noOcclusion()));
    public static final Supplier<Block> OCEANOGRAPHER_TABLE_BLOCK = RegistryHelper.registerBlock("oceanographer_table", () -> new OceanographerTableBlock(settings("oceanographer_table").strength(0.5F).lightLevel((state) -> 12).noOcclusion().isRedstoneConductor((state, world, pos) -> false).isSuffocating((state, world, pos) -> false)));
    public static final Supplier<Block> OAK_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("oak_horticulturist_table", () -> new HorticulturistTableBlock(settings("oak_horticulturist_table").strength(0.5F).noOcclusion().ignitedByLava().lightLevel((state) -> state.getValue(HorticulturistTableBlock.LIGHT))));
    public static final Supplier<Block> DARK_OAK_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("dark_oak_horticulturist_table", () -> new HorticulturistTableBlock(settings("dark_oak_horticulturist_table").strength(0.5F).noOcclusion().ignitedByLava().lightLevel((state) -> state.getValue(HorticulturistTableBlock.LIGHT))));
    public static final Supplier<Block> ACACIA_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("acacia_horticulturist_table", () -> new HorticulturistTableBlock(settings("acacia_horticulturist_table").strength(0.5F).noOcclusion().ignitedByLava().lightLevel((state) -> state.getValue(HorticulturistTableBlock.LIGHT))));
    public static final Supplier<Block> JUNGLE_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("jungle_horticulturist_table", () -> new HorticulturistTableBlock(settings("jungle_horticulturist_table").strength(0.5F).noOcclusion().ignitedByLava().lightLevel((state) -> state.getValue(HorticulturistTableBlock.LIGHT))));
    public static final Supplier<Block> SPRUCE_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("spruce_horticulturist_table", () -> new HorticulturistTableBlock(settings("spruce_horticulturist_table").strength(0.5F).noOcclusion().ignitedByLava().lightLevel((state) -> state.getValue(HorticulturistTableBlock.LIGHT))));
    public static final Supplier<Block> BIRCH_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("birch_horticulturist_table", () -> new HorticulturistTableBlock(settings("birch_horticulturist_table").strength(0.5F).noOcclusion().ignitedByLava().lightLevel((state) -> state.getValue(HorticulturistTableBlock.LIGHT))));
    public static final Supplier<Block> MANGROVE_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("mangrove_horticulturist_table", () -> new HorticulturistTableBlock(settings("mangrove_horticulturist_table").strength(0.5F).noOcclusion().ignitedByLava().lightLevel((state) -> state.getValue(HorticulturistTableBlock.LIGHT))));
    public static final Supplier<Block> CRIMSON_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("crimson_horticulturist_table", () -> new HorticulturistTableBlock(settings("crimson_horticulturist_table").strength(0.5F).noOcclusion().ignitedByLava().lightLevel((state) -> state.getValue(HorticulturistTableBlock.LIGHT))));
    public static final Supplier<Block> WARPED_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("warped_horticulturist_table", () -> new HorticulturistTableBlock(settings("warped_horticulturist_table").strength(0.5F).noOcclusion().ignitedByLava().lightLevel((state) -> state.getValue(HorticulturistTableBlock.LIGHT))));
    public static final Supplier<Block> CHERRY_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("cherry_horticulturist_table", () -> new HorticulturistTableBlock(settings("cherry_horticulturist_table").strength(0.5F).noOcclusion().ignitedByLava().lightLevel((state) -> state.getValue(HorticulturistTableBlock.LIGHT))));
    public static final Supplier<Block> BAMBOO_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("bamboo_horticulturist_table", () -> new HorticulturistTableBlock(settings("bamboo_horticulturist_table").strength(0.5F).noOcclusion().ignitedByLava().lightLevel((state) -> state.getValue(HorticulturistTableBlock.LIGHT))));
    public static final Supplier<Block> PALE_OAK_HORTICULTURIST_TABLE_BLOCK = RegistryHelper.registerBlock("pale_oak_horticulturist_table", () -> new HorticulturistTableBlock(settings("pale_oak_horticulturist_table").strength(0.5F).noOcclusion().ignitedByLava().lightLevel((state) -> state.getValue(HorticulturistTableBlock.LIGHT))));
    public static final Supplier<Block> OCCULTIST_TABLE_BLOCK = RegistryHelper.registerBlock("occultist_table", () -> new OccultistTableBlock(settings("occultist_table").strength(0.5F).lightLevel((state) -> state.getValue(OccultistTableBlock.FILLING) * 2).noOcclusion()));
    public static final Supplier<Block> ORE_GRINDER_BLOCK = RegistryHelper.registerBlock("ore_grinder", () -> new OreGrinderBlock(settings("ore_grinder").strength(1.5F).requiresCorrectToolForDrops().lightLevel((state) -> state.getValue(OreGrinderBlock.LIT) ? 13 : 0).noOcclusion()));

    public static void init() {

    }

}
