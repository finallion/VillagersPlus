package com.finallion.villagersplus.init;

import com.finallion.villagersplus.VillagersPlus;
import com.finallion.villagersplus.blockentities.AlchemistTableBlockEntity;
import com.finallion.villagersplus.blockentities.HorticulturistTableBlockEntity;
import com.finallion.villagersplus.blockentities.OccultistTableBlockEntity;
import com.finallion.villagersplus.blockentities.OceanographerTableBlockEntity;
import com.finallion.villagersplus.blocks.AlchemistTableBlock;
import com.finallion.villagersplus.blocks.HorticulturistTableBlock;
import com.finallion.villagersplus.blocks.OccultistTableBlock;
import com.finallion.villagersplus.blocks.OceanographerTableBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks {

    public static final Block ALCHEMIST_TABLE_BLOCK = new AlchemistTableBlock(FabricBlockSettings.of(Material.METAL).strength(0.5F).luminance((state) -> 1).nonOpaque());
    public static final Block OCEANOGRAPHER_TABLE_BLOCK = new OceanographerTableBlock(FabricBlockSettings.of(Material.GLASS).strength(0.5F).luminance((state) -> 12).nonOpaque().solidBlock((state, world, pos) -> false).suffocates((state, world, pos) -> false));
    public static final Block OAK_HORTICULTURIST_TABLE_BLOCK = new HorticulturistTableBlock(FabricBlockSettings.of(Material.WOOD).strength(0.5F).nonOpaque());
    public static final Block DARK_OAK_HORTICULTURIST_TABLE_BLOCK = new HorticulturistTableBlock(FabricBlockSettings.of(Material.WOOD).strength(0.5F).nonOpaque());
    public static final Block ACACIA_HORTICULTURIST_TABLE_BLOCK = new HorticulturistTableBlock(FabricBlockSettings.of(Material.WOOD).strength(0.5F).nonOpaque());
    public static final Block JUNGLE_HORTICULTURIST_TABLE_BLOCK = new HorticulturistTableBlock(FabricBlockSettings.of(Material.WOOD).strength(0.5F).nonOpaque());
    public static final Block SPRUCE_HORTICULTURIST_TABLE_BLOCK = new HorticulturistTableBlock(FabricBlockSettings.of(Material.WOOD).strength(0.5F).nonOpaque());
    public static final Block BIRCH_HORTICULTURIST_TABLE_BLOCK = new HorticulturistTableBlock(FabricBlockSettings.of(Material.WOOD).strength(0.5F).nonOpaque());
    //public static final Block MANGROVE_HORTICULTURIST_TABLE_BLOCK = new HorticulturistTableBlock(FabricBlockSettings.of(Material.WOOD).strength(0.5F).nonOpaque());
    public static final Block CRIMSON_HORTICULTURIST_TABLE_BLOCK = new HorticulturistTableBlock(FabricBlockSettings.of(Material.WOOD).strength(0.5F).nonOpaque());
    public static final Block WARPED_HORTICULTURIST_TABLE_BLOCK = new HorticulturistTableBlock(FabricBlockSettings.of(Material.WOOD).strength(0.5F).nonOpaque());
    public static final Block OCCULTIST_TABLE_BLOCK = new OccultistTableBlock(FabricBlockSettings.of(Material.STONE).strength(0.5F).luminance((state) -> state.get(OccultistTableBlock.FILLING) * 2).nonOpaque());

    public static final BlockEntityType<OceanographerTableBlockEntity> OCEANOGRAPHER_TABLE_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(VillagersPlus.MOD_ID, "oceanographer_table_block_entity"),
            FabricBlockEntityTypeBuilder.create(OceanographerTableBlockEntity::new, OCEANOGRAPHER_TABLE_BLOCK).build()
    );

    public static final BlockEntityType<AlchemistTableBlockEntity> ALCHEMIST_TABLE_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(VillagersPlus.MOD_ID, "alchemist_table_block_entity"),
            FabricBlockEntityTypeBuilder.create(AlchemistTableBlockEntity::new, ALCHEMIST_TABLE_BLOCK).build()
    );

    public static final BlockEntityType<OccultistTableBlockEntity> OCCULTIST_TABLE_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(VillagersPlus.MOD_ID, "occultist_table_block_entity"),
            FabricBlockEntityTypeBuilder.create(OccultistTableBlockEntity::new, OCCULTIST_TABLE_BLOCK).build()
    );

    public static final BlockEntityType<HorticulturistTableBlockEntity> HORTICULTURIST_TABLE_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(VillagersPlus.MOD_ID, "horticulturist_table_block_entity"),
            FabricBlockEntityTypeBuilder.create(HorticulturistTableBlockEntity::new,
                    OAK_HORTICULTURIST_TABLE_BLOCK,
                    DARK_OAK_HORTICULTURIST_TABLE_BLOCK,
                    ACACIA_HORTICULTURIST_TABLE_BLOCK,
                    JUNGLE_HORTICULTURIST_TABLE_BLOCK,
                    SPRUCE_HORTICULTURIST_TABLE_BLOCK,
                    BIRCH_HORTICULTURIST_TABLE_BLOCK,
                    //MANGROVE_HORTICULTURIST_TABLE_BLOCK,
                    CRIMSON_HORTICULTURIST_TABLE_BLOCK,
                    WARPED_HORTICULTURIST_TABLE_BLOCK).build()
    );




    public static void registerBlocks() {
        Registry.register(Registry.BLOCK, new Identifier(VillagersPlus.MOD_ID, "oceanographer_table"), OCEANOGRAPHER_TABLE_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(VillagersPlus.MOD_ID, "oceanographer_table"), new BlockItem(OCEANOGRAPHER_TABLE_BLOCK, new FabricItemSettings().group(VillagersPlus.ITEM_GROUP)));

        Registry.register(Registry.BLOCK, new Identifier(VillagersPlus.MOD_ID, "alchemist_table"), ALCHEMIST_TABLE_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(VillagersPlus.MOD_ID, "alchemist_table"), new BlockItem(ALCHEMIST_TABLE_BLOCK, new FabricItemSettings().group(VillagersPlus.ITEM_GROUP)));

        Registry.register(Registry.BLOCK, new Identifier(VillagersPlus.MOD_ID, "occultist_table"), OCCULTIST_TABLE_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(VillagersPlus.MOD_ID, "occultist_table"), new BlockItem(OCCULTIST_TABLE_BLOCK, new FabricItemSettings().group(VillagersPlus.ITEM_GROUP)));

        Registry.register(Registry.BLOCK, new Identifier(VillagersPlus.MOD_ID, "oak_horticulturist_table"), OAK_HORTICULTURIST_TABLE_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(VillagersPlus.MOD_ID, "oak_horticulturist_table"), new BlockItem(OAK_HORTICULTURIST_TABLE_BLOCK, new FabricItemSettings().group(VillagersPlus.ITEM_GROUP)));
        Registry.register(Registry.BLOCK, new Identifier(VillagersPlus.MOD_ID, "dark_oak_horticulturist_table"), DARK_OAK_HORTICULTURIST_TABLE_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(VillagersPlus.MOD_ID, "dark_oak_horticulturist_table"), new BlockItem(DARK_OAK_HORTICULTURIST_TABLE_BLOCK, new FabricItemSettings().group(VillagersPlus.ITEM_GROUP)));
        Registry.register(Registry.BLOCK, new Identifier(VillagersPlus.MOD_ID, "acacia_horticulturist_table"), ACACIA_HORTICULTURIST_TABLE_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(VillagersPlus.MOD_ID, "acacia_horticulturist_table"), new BlockItem(ACACIA_HORTICULTURIST_TABLE_BLOCK, new FabricItemSettings().group(VillagersPlus.ITEM_GROUP)));
        Registry.register(Registry.BLOCK, new Identifier(VillagersPlus.MOD_ID, "birch_horticulturist_table"), BIRCH_HORTICULTURIST_TABLE_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(VillagersPlus.MOD_ID, "birch_horticulturist_table"), new BlockItem(BIRCH_HORTICULTURIST_TABLE_BLOCK, new FabricItemSettings().group(VillagersPlus.ITEM_GROUP)));
        Registry.register(Registry.BLOCK, new Identifier(VillagersPlus.MOD_ID, "jungle_horticulturist_table"), JUNGLE_HORTICULTURIST_TABLE_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(VillagersPlus.MOD_ID, "jungle_horticulturist_table"), new BlockItem(JUNGLE_HORTICULTURIST_TABLE_BLOCK, new FabricItemSettings().group(VillagersPlus.ITEM_GROUP)));
        Registry.register(Registry.BLOCK, new Identifier(VillagersPlus.MOD_ID, "spruce_horticulturist_table"), SPRUCE_HORTICULTURIST_TABLE_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(VillagersPlus.MOD_ID, "spruce_horticulturist_table"), new BlockItem(SPRUCE_HORTICULTURIST_TABLE_BLOCK, new FabricItemSettings().group(VillagersPlus.ITEM_GROUP)));
        //Registry.register(Registry.BLOCK, new Identifier(VillagersPlus.MOD_ID, "mangrove_horticulturist_table"), MANGROVE_HORTICULTURIST_TABLE_BLOCK);
        //Registry.register(Registry.ITEM, new Identifier(VillagersPlus.MOD_ID, "mangrove_horticulturist_table"), new BlockItem(MANGROVE_HORTICULTURIST_TABLE_BLOCK, new FabricItemSettings().group(VillagersPlus.ITEM_GROUP)));
        Registry.register(Registry.BLOCK, new Identifier(VillagersPlus.MOD_ID, "crimson_horticulturist_table"), CRIMSON_HORTICULTURIST_TABLE_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(VillagersPlus.MOD_ID, "crimson_horticulturist_table"), new BlockItem(CRIMSON_HORTICULTURIST_TABLE_BLOCK, new FabricItemSettings().group(VillagersPlus.ITEM_GROUP)));
        Registry.register(Registry.BLOCK, new Identifier(VillagersPlus.MOD_ID, "warped_horticulturist_table"), WARPED_HORTICULTURIST_TABLE_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(VillagersPlus.MOD_ID, "warped_horticulturist_table"), new BlockItem(WARPED_HORTICULTURIST_TABLE_BLOCK, new FabricItemSettings().group(VillagersPlus.ITEM_GROUP)));
    }
}
