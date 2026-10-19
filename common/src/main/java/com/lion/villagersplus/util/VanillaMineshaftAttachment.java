package com.lion.villagersplus.util;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.mixin.SinglePoolElementAccessor;
import com.lion.villagersplus.mixin.StructurePiecesCollectorAccessor;
import net.minecraft.block.JigsawBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.structure.MineshaftStructure;
import net.minecraft.world.gen.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// Digs the miner's shaft below the miner's house and grows a vanilla mineshaft from its bottom.
///
/// Jigsaw cannot reach below the street. Plain pieces follow the jigsaw run.
/// The `villagersplus:mineshaft_start` jigsaw marks where digging starts.
public final class VanillaMineshaftAttachment {

    private static final String SHAFT_START_JIGSAW = "villagersplus:mineshaft_start";
    private static final String SHAFT_JIGSAW = "villagersplus:mineshaft";

    private static final int SHAFT_DEPTH = 30;
    /// Stops a template without a downward jigsaw from looping.
    private static final int MAX_SEGMENTS = 16;

    private record Shaft(Identifier segment, Identifier end) { }

    private static final Map<Identifier, Shaft> SHAFTS = Map.of(
            id("village/plains/plains_miner"), new Shaft(id("mine/mine_pool/plains_mineshaft"), id("mine/mine_pool/plains_mineshaft_end")),
            id("village/savanna/savanna_miner"), new Shaft(id("mine/mine_pool/savanna_mineshaft"), id("mine/mine_pool/savanna_mineshaft_end")),
            id("village/taiga/taiga_miner"), new Shaft(id("mine/mine_pool/taiga_mineshaft"), id("mine/mine_pool/taiga_mineshaft_end"))
    );

    private static final Set<Identifier> SHAFT_TEMPLATES = SHAFTS.values().stream()
            .flatMap(shaft -> Stream.of(shaft.segment(), shaft.end()))
            .collect(Collectors.toUnmodifiableSet());

    private VanillaMineshaftAttachment() {}

    private static Identifier id(String path) {
        return new Identifier(VillagersPlus.MOD_ID, path);
    }

    /// Returns early without a miner house.
    public static void append(StructurePiecesCollector collector, Structure.Context context) {
        for (PoolStructurePiece hut : findMinerHouses(collector)) {
            appendShaft(collector, context, hut);
        }
    }

    /// The checks use no state, so they work after a structure start reloads.
    public static boolean isBeardExempt(StructurePiece piece) {
        StructurePieceType type = piece.getType();

        if (type == StructurePieceType.MINESHAFT_CORRIDOR || type == StructurePieceType.MINESHAFT_CROSSING
                || type == StructurePieceType.MINESHAFT_STAIRS || type == StructurePieceType.MINESHAFT_ROOM) {
            return true;
        }

        return piece instanceof PoolStructurePiece pool
                && locationOf(pool).filter(SHAFT_TEMPLATES::contains).isPresent();
    }

    private static void appendShaft(
            StructurePiecesCollector collector,
            Structure.Context context,
            PoolStructurePiece hut
    ) {
        Shaft shaft = locationOf(hut).map(SHAFTS::get).orElse(null);

        if (shaft == null) {
            return;
        }

        StructureTemplateManager templates = context.structureTemplateManager();
        BlockRotation rotation = hut.getRotation();
        BlockBox houseBox = hut.getBoundingBox();
        Random random = randomFor(context, houseBox);

        BlockPos marker = jigsawPos(hut.getPoolElement(), templates, hut.getPos(), rotation, random, SHAFT_START_JIGSAW, Direction.DOWN);

        if (marker == null) {
            return;
        }

        SinglePoolElement segment = element(context, shaft.segment());
        SinglePoolElement end = element(context, shaft.end());

        int target = Math.max(houseBox.getMinY() - SHAFT_DEPTH, context.world().getBottomY() + 16);

        BlockPos connect = marker.down();

        List<PoolStructurePiece> pieces = new ArrayList<>();
        for (int i = 0; i < MAX_SEGMENTS && connect.getY() > target; i++) {
            PoolStructurePiece piece = place(segment, templates, rotation, connect, random);

            if (piece == null) {
                return;
            }

            pieces.add(piece);

            BlockPos bottom = jigsawPos(segment, templates, piece.getPos(), rotation, random, SHAFT_JIGSAW, Direction.DOWN);

            if (bottom == null) {
                break;
            }

            connect = bottom.down();
        }

        PoolStructurePiece closing = place(end, templates, rotation, connect, random);

        if (closing != null) {
            pieces.add(closing);
        }

        if (pieces.isEmpty()) {
            return;
        }

        for (PoolStructurePiece piece : pieces) {
            collector.addPiece(piece);
        }

        if (VillagersPlus.CONFIG.mineshaft_connects_to_vanilla_mineshaft) {
            appendVanillaMineshaft(collector, context, pieces);
        }
    }

    /// Hangs mineshaft pieces off a side wall of the closing piece, as `MineshaftStructure` does off its start room.
    private static void appendVanillaMineshaft(
            StructurePiecesCollector collector,
            Structure.Context context,
            List<PoolStructurePiece> shaft
    ) {
        BlockBox anchor = shaft.get(shaft.size() - 1).getBoundingBox();
        BlockPos center = anchor.getCenter();

        Random random = randomFor(context, anchor);

        Direction direction = Direction.fromHorizontal(random.nextInt(4));
        int y = anchor.getMinY() + 1;

        // The corridor starts at the corner it grows away from. The -1 centres the doorway.
        int startX = switch (direction) {
            case WEST -> anchor.getMinX();
            case EAST -> anchor.getMaxX();
            default -> center.getX() - 1;
        };
        int startZ = switch (direction) {
            case NORTH -> anchor.getMinZ();
            case SOUTH -> anchor.getMaxZ();
            default -> center.getZ() - 1;
        };

        // Measure with an empty holder. The real collector rejects every length.
        BlockBox box = MineshaftGenerator.MineshaftCorridor.getBoundingBox(new StructurePiecesCollector(), random, startX, y, startZ, direction);

        if (box == null) {
            return;
        }

        // Stage the pieces apart from the real collector, so out-of-range ones stay out.
        StructurePiecesCollector staging = new StructurePiecesCollector();
        for (PoolStructurePiece piece : shaft) {
            staging.addPiece(piece);
        }

        MineshaftGenerator.MineshaftCorridor root = new MineshaftGenerator.MineshaftCorridor(0, random, box, direction, MineshaftStructure.Type.NORMAL);
        staging.addPiece(root);
        // The generator caps the chain at 8 pieces and 80 blocks.
        root.fillOpenings(root, staging, random);

        // addPiece only appends. Pieces past the shaft are new.
        List<StructurePiece> staged = ((StructurePiecesCollectorAccessor) staging).getPieces();
        for (StructurePiece piece : staged.subList(shaft.size(), staged.size())) {
            if (isReachable(context.chunkPos(), piece.getBoundingBox())) {
                collector.addPiece(piece);
            }
        }
    }

    /// Structure references reach eight chunks around the village chunk.
    private static boolean isReachable(ChunkPos origin, BlockBox box) {
        int reach = 8 * 16;
        return box.getMinX() >= origin.getStartX() - reach && box.getMaxX() <= origin.getEndX() + reach
                && box.getMinZ() >= origin.getStartZ() - reach && box.getMaxZ() <= origin.getEndZ() + reach;
    }

    private static PoolStructurePiece place(
            SinglePoolElement element,
            StructureTemplateManager templates,
            BlockRotation rotation,
            BlockPos connect,
            Random random
    ) {
        BlockPos top = jigsawPos(element, templates, BlockPos.ORIGIN, rotation, random, SHAFT_JIGSAW, Direction.UP);

        if (top == null) {
            return null;
        }

        BlockPos pos = connect.subtract(top);
        BlockBox box = element.getBoundingBox(templates, pos, rotation);
        return new PoolStructurePiece(templates, element, pos, 0, rotation, box);
    }

    /// Returns null when no such jigsaw exists.
    private static BlockPos jigsawPos(
            StructurePoolElement element,
            StructureTemplateManager templates,
            BlockPos pos,
            BlockRotation rotation,
            Random random,
            String name,
            Direction facing
    ) {
        for (StructureTemplate.StructureBlockInfo info : element.getStructureBlockInfos(templates, pos, rotation, random)) {
            if (info.nbt() == null || !name.equals(info.nbt().getString("name"))) {
                continue;
            }

            if (JigsawBlock.getFacing(info.state()) == facing) {
                return info.pos();
            }
        }
        return null;
    }

    private static SinglePoolElement element(Structure.Context context, Identifier location) {
        RegistryEntry<StructureProcessorList> processors = context.dynamicRegistryManager()
                .get(RegistryKeys.PROCESSOR_LIST)
                .entryOf(RegistryKey.of(RegistryKeys.PROCESSOR_LIST, new Identifier("minecraft", "empty")));

        return StructurePoolElement.ofProcessedSingle(location.toString(), processors).apply(StructurePool.Projection.RIGID);
    }

    private static Random randomFor(Structure.Context context, BlockBox box) {
        return Random.create(context.seed()
                ^ (long) box.getCenter().getX() * 341873128712L
                ^ (long) box.getCenter().getZ() * 132897987541L
                ^ (long) box.getMinY());
    }

    private static List<PoolStructurePiece> findMinerHouses(StructurePiecesCollector collector) {
        List<PoolStructurePiece> found = new ArrayList<>();
        for (StructurePiece piece : ((StructurePiecesCollectorAccessor) collector).getPieces()) {
            if (piece instanceof PoolStructurePiece poolPiece && locationOf(poolPiece).filter(SHAFTS::containsKey).isPresent()) {
                found.add(poolPiece);
            }
        }
        return found;
    }

    private static Optional<Identifier> locationOf(PoolStructurePiece piece) {
        if (piece.getPoolElement() instanceof SinglePoolElement single) {
            return ((SinglePoolElementAccessor) single).getLocation().left();
        }

        return Optional.empty();
    }
}
