package com.lion.villagersplus.init;

import com.lion.villagersplus.blockentities.AlchemistTableBlockEntity;
import com.lion.villagersplus.blockentities.HorticulturistTableBlockEntity;
import com.lion.villagersplus.blockentities.OccultistTableBlockEntity;
import com.lion.villagersplus.blockentities.OceanographerTableBlockEntity;
import com.lion.villagersplus.platform.RegistryHelper;
import net.minecraft.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class VPBlockEntities {

    public static final Supplier<BlockEntityType<OceanographerTableBlockEntity>> OCEANOGRAPHER_TABLE_BLOCK_ENTITY = RegistryHelper.registerBlockEntity("oceanographer_table_block_entity", () -> BlockEntityType.Builder.create(OceanographerTableBlockEntity::new,
            VPBlocks.OCEANOGRAPHER_TABLE_BLOCK.get()).build(null)
    );

    public static final Supplier<BlockEntityType<AlchemistTableBlockEntity>> ALCHEMIST_TABLE_BLOCK_ENTITY = RegistryHelper.registerBlockEntity("alchemist_table_block_entity", () -> BlockEntityType.Builder.create(AlchemistTableBlockEntity::new,
            VPBlocks.ALCHEMIST_TABLE_BLOCK.get()).build(null)
    );

    public static final Supplier<BlockEntityType<OccultistTableBlockEntity>> OCCULTIST_TABLE_BLOCK_ENTITY = RegistryHelper.registerBlockEntity("occultist_table_block_entity", () -> BlockEntityType.Builder.create(OccultistTableBlockEntity::new,
            VPBlocks.OCCULTIST_TABLE_BLOCK.get()).build(null)
    );

    public static final Supplier<BlockEntityType<HorticulturistTableBlockEntity>> HORTICULTURIST_TABLE_BLOCK_ENTITY = RegistryHelper.registerBlockEntity("horticulturist_table_block_entity", () -> BlockEntityType.Builder.create(HorticulturistTableBlockEntity::new,
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
            ).build(null)
    );

    public static void init() {

    }

}
