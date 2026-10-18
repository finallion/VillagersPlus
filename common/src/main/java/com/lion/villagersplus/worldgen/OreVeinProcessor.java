package com.lion.villagersplus.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

/// Swaps a marker block for a randomly drawn ore while a template is placed.
///
/// The draw uses a seed from the block position, so the placement random stays untouched.
public class OreVeinProcessor implements StructureProcessor {

    public static final MapCodec<OreVeinProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("target").forGetter(processor -> processor.target),
            WeightedList.codec(BlockState.CODEC).fieldOf("ores").forGetter(processor -> processor.ores)
    ).apply(instance, OreVeinProcessor::new));

    private final Block target;
    private final WeightedList<BlockState> ores;

    public OreVeinProcessor(Block target, WeightedList<BlockState> ores) {
        this.target = target;
        this.ores = ores;
    }

    @Override
    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader world, BlockPos pos, BlockPos pivot,
                                                       BlockPos originalPos,
                                                       StructureTemplate.StructureBlockInfo currentBlockInfo,
                                                       StructurePlaceSettings data) {
        if (!currentBlockInfo.state().is(this.target)) {
            return currentBlockInfo;
        }

        RandomSource random = RandomSource.create(Mth.getSeed(currentBlockInfo.pos().getX(), currentBlockInfo.pos().getY(), currentBlockInfo.pos().getZ()));
        return this.ores.getRandom(random)
                .map(state -> new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), state, null))
                .orElse(currentBlockInfo);
    }

    @Override
    public MapCodec<OreVeinProcessor> codec() {
        return CODEC;
    }
}
