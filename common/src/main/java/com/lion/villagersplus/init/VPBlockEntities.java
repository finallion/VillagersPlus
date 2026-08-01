package com.lion.villagersplus.init;

import com.lion.villagersplus.blockentities.AlchemistTableBlockEntity;
import com.lion.villagersplus.blockentities.HorticulturistTableBlockEntity;
import com.lion.villagersplus.blockentities.OccultistTableBlockEntity;
import com.lion.villagersplus.blockentities.OceanographerTableBlockEntity;
import com.lion.villagersplus.blockentities.OreGrinderBlockEntity;
import com.lion.villagersplus.platform.RegistryHelper;
import net.minecraft.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class VPBlockEntities {

    public static final Supplier<BlockEntityType<OceanographerTableBlockEntity>> OCEANOGRAPHER_TABLE_BLOCK_ENTITY = RegistryHelper.registerBlockEntity("oceanographer_table_block_entity", () -> new BlockEntityType<>(OceanographerTableBlockEntity::new, java.util.Set.of(VPBlocks.OCEANOGRAPHER_TABLE_BLOCK.get()))
    );

    public static final Supplier<BlockEntityType<AlchemistTableBlockEntity>> ALCHEMIST_TABLE_BLOCK_ENTITY = RegistryHelper.registerBlockEntity("alchemist_table_block_entity", () -> new BlockEntityType<>(AlchemistTableBlockEntity::new, java.util.Set.of(VPBlocks.ALCHEMIST_TABLE_BLOCK.get()))
    );

    public static final Supplier<BlockEntityType<OccultistTableBlockEntity>> OCCULTIST_TABLE_BLOCK_ENTITY = RegistryHelper.registerBlockEntity("occultist_table_block_entity", () -> new BlockEntityType<>(OccultistTableBlockEntity::new, java.util.Set.of(VPBlocks.OCCULTIST_TABLE_BLOCK.get()))
    );

    public static final Supplier<BlockEntityType<OreGrinderBlockEntity>> ORE_GRINDER_BLOCK_ENTITY = RegistryHelper.registerBlockEntity("ore_grinder_block_entity", () -> new BlockEntityType<>(OreGrinderBlockEntity::new, java.util.Set.of(VPBlocks.ORE_GRINDER_BLOCK.get()))
    );

    public static final Supplier<BlockEntityType<HorticulturistTableBlockEntity>> HORTICULTURIST_TABLE_BLOCK_ENTITY = RegistryHelper.registerBlockEntity("horticulturist_table_block_entity", () -> new BlockEntityType<>(HorticulturistTableBlockEntity::new, java.util.Set.of(
            VPBlocks.OAK_HORTICULTURIST_TABLE_BLOCK.get(),
            VPBlocks.DARK_OAK_HORTICULTURIST_TABLE_BLOCK.get(),
            VPBlocks.ACACIA_HORTICULTURIST_TABLE_BLOCK.get(),
            VPBlocks.JUNGLE_HORTICULTURIST_TABLE_BLOCK.get(),
            VPBlocks.SPRUCE_HORTICULTURIST_TABLE_BLOCK.get(),
            VPBlocks.BIRCH_HORTICULTURIST_TABLE_BLOCK.get(),
            VPBlocks.MANGROVE_HORTICULTURIST_TABLE_BLOCK.get(),
            VPBlocks.CRIMSON_HORTICULTURIST_TABLE_BLOCK.get(),
            VPBlocks.WARPED_HORTICULTURIST_TABLE_BLOCK.get(),
            VPBlocks.CHERRY_HORTICULTURIST_TABLE_BLOCK.get(),
            VPBlocks.BAMBOO_HORTICULTURIST_TABLE_BLOCK.get()
            ))
    );

    public static void init() {

    }

}
