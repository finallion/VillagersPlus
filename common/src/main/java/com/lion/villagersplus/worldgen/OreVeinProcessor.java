package com.lion.villagersplus.worldgen;

import com.lion.villagersplus.init.VPStructures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

/// The draw seeds from the block position.
public class OreVeinProcessor extends StructureProcessor {

    public static final MapCodec<OreVeinProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Registries.BLOCK.getCodec().fieldOf("target").forGetter(processor -> processor.target),
            DataPool.createCodec(BlockState.CODEC).fieldOf("ores").forGetter(processor -> processor.ores)
    ).apply(instance, OreVeinProcessor::new));

    private final Block target;
    private final DataPool<BlockState> ores;

    public OreVeinProcessor(Block target, DataPool<BlockState> ores) {
        this.target = target;
        this.ores = ores;
    }

    @Override
    @Nullable
    public StructureTemplate.StructureBlockInfo process(
            WorldView world,
            BlockPos pos,
            BlockPos pivot,
            StructureTemplate.StructureBlockInfo originalBlockInfo,
            StructureTemplate.StructureBlockInfo currentBlockInfo,
            StructurePlacementData data
    ) {
        if (!currentBlockInfo.state().isOf(this.target)) {
            return currentBlockInfo;
        }

        Random random = Random.create(MathHelper.hashCode(currentBlockInfo.pos()));
        return this.ores.getDataOrEmpty(random)
                .map(state -> new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), state, null))
                .orElse(currentBlockInfo);
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return VPStructures.ORE_VEIN;
    }
}
