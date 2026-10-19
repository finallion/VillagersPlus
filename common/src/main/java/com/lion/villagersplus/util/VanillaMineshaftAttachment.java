package com.lion.villagersplus.util;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.mixin.SinglePoolElementAccessor;
import com.lion.villagersplus.mixin.StructurePiecesCollectorAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftPieces;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

/// Digs the miner's shaft below the miner's house and grows a vanilla mineshaft from its bottom.
///
/// Jigsaw cannot reach below the street. Plain pieces follow the jigsaw run.
/// The `villagersplus:mineshaft_start` jigsaw marks where digging starts.
public final class VanillaMineshaftAttachment {

    private static final String SHAFT_START_JIGSAW = "villagersplus:mineshaft_start";
    private static final String SHAFT_JIGSAW = "villagersplus:mineshaft";

    private static final int SHAFT_DEPTH = 30;
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
        return Identifier.fromNamespaceAndPath(VillagersPlus.MOD_ID, path);
    }

    /// Runs for every jigsaw structure. Returns early without a miner house.
    public static void append(StructurePiecesBuilder collector, Structure.GenerationContext context) {
        for (PoolElementStructurePiece hut : findMinerHouses(collector)) {
            appendShaft(collector, context, hut);
        }
    }

    /// Both tests keep no state, so they survive a reload of the structure start.
    public static boolean isBeardExempt(StructurePiece piece) {
        // Only our pieces reach here, because vanilla mineshafts declare no adaptation.
        StructurePieceType type = piece.getType();

        if (type == StructurePieceType.MINE_SHAFT_CORRIDOR || type == StructurePieceType.MINE_SHAFT_CROSSING
                || type == StructurePieceType.MINE_SHAFT_STAIRS || type == StructurePieceType.MINE_SHAFT_ROOM) {
            return true;
        }

        // The shaft pieces are ordinary pool pieces, so they are matched by name.
        return piece instanceof PoolElementStructurePiece pool
                && locationOf(pool).filter(SHAFT_TEMPLATES::contains).isPresent();
    }

    private static void appendShaft(
            StructurePiecesBuilder collector,
            Structure.GenerationContext context,
            PoolElementStructurePiece hut
    ) {
        Shaft shaft = locationOf(hut).map(SHAFTS::get).orElse(null);

        if (shaft == null) {
            return;
        }

        StructureTemplateManager templates = context.structureTemplateManager();
        Rotation rotation = hut.getRotation();
        BoundingBox houseBox = hut.getBoundingBox();
        RandomSource random = randomFor(context, houseBox);

        BlockPos marker = jigsawPos(hut.getElement(), templates, hut.getPosition(), rotation, random, SHAFT_START_JIGSAW, Direction.DOWN);

        if (marker == null) {
            return;
        }

        SinglePoolElement segment = element(context, shaft.segment());
        SinglePoolElement end = element(context, shaft.end());

        int target = Math.max(houseBox.minY() - SHAFT_DEPTH, context.heightAccessor().getMinY() + 16);

        BlockPos connect = marker.below();

        List<PoolElementStructurePiece> pieces = new ArrayList<>();
        for (int i = 0; i < MAX_SEGMENTS && connect.getY() > target; i++) {
            PoolElementStructurePiece piece = place(segment, templates, rotation, connect, random);

            if (piece == null) {
                return;
            }

            pieces.add(piece);

            BlockPos bottom = jigsawPos(segment, templates, piece.getPosition(), rotation, random, SHAFT_JIGSAW, Direction.DOWN);

            if (bottom == null) {
                break;
            }

            connect = bottom.below();
        }

        PoolElementStructurePiece closing = place(end, templates, rotation, connect, random);

        if (closing != null) {
            pieces.add(closing);
        }

        if (pieces.isEmpty()) {
            return;
        }

        for (PoolElementStructurePiece piece : pieces) {
            collector.addPiece(piece);
        }

        if (VillagersPlus.CONFIG.mineshaft_connects_to_vanilla_mineshaft) {
            appendVanillaMineshaft(collector, context, pieces);
        }
    }

    /// Hangs mineshaft pieces off a side wall of the closing piece, as `MineshaftStructure` does off its start room.
    private static void appendVanillaMineshaft(
            StructurePiecesBuilder collector,
            Structure.GenerationContext context,
            List<PoolElementStructurePiece> shaft
    ) {
        BoundingBox anchor = shaft.get(shaft.size() - 1).getBoundingBox();
        BlockPos center = anchor.getCenter();

        // Seeded from the anchor, so the layout is independent of the shaft segment count.
        RandomSource random = randomFor(context, anchor);

        Direction direction = Direction.from2DDataValue(random.nextInt(4));
        int y = anchor.minY() + 1;

        // The corridor position is its near corner, so the span is centred on the shaft.
        int startX = switch (direction) {
            case WEST -> anchor.minX();
            case EAST -> anchor.maxX();
            default -> center.getX() - 1;
        };
        int startZ = switch (direction) {
            case NORTH -> anchor.minZ();
            case SOUTH -> anchor.maxZ();
            default -> center.getZ() - 1;
        };

        // Measure against an empty holder. The first corridor must overlap the shaft wall.
        BoundingBox box = MineshaftPieces.MineShaftCorridor.findCorridorSize(new StructurePiecesBuilder(), random, startX, y, startZ, direction);

        if (box == null) {
            return;
        }

        // Later corridors use their own collector seeded with the shaft, so out-of-reach pieces are not taken.
        StructurePiecesBuilder staging = new StructurePiecesBuilder();
        for (PoolElementStructurePiece piece : shaft) {
            staging.addPiece(piece);
        }

        MineshaftPieces.MineShaftCorridor root = new MineshaftPieces.MineShaftCorridor(0, random, box, direction, MineshaftStructure.Type.NORMAL);
        staging.addPiece(root);
        // Recursion is capped by the generator at chain length 8 and 80 blocks.
        root.addChildren(root, staging, random);

        List<StructurePiece> staged = ((StructurePiecesCollectorAccessor) staging).getPieces();
        for (StructurePiece piece : staged.subList(shaft.size(), staged.size())) {
            if (isReachable(context.chunkPos(), piece.getBoundingBox())) {
                collector.addPiece(piece);
            }
        }
    }

    /// Whether a piece is close enough to the village chunk to be placed. Structure references reach eight chunks.
    private static boolean isReachable(ChunkPos origin, BoundingBox box) {
        int reach = 8 * 16;
        return box.minX() >= origin.getMinBlockX() - reach && box.maxX() <= origin.getMaxBlockX() + reach
                && box.minZ() >= origin.getMinBlockZ() - reach && box.maxZ() <= origin.getMaxBlockZ() + reach;
    }

    /// Positions a shaft template so its upward jigsaw lands on `connect`.
    private static PoolElementStructurePiece place(
            SinglePoolElement element,
            StructureTemplateManager templates,
            Rotation rotation,
            BlockPos connect,
            RandomSource random
    ) {
        BlockPos top = jigsawPos(element, templates, BlockPos.ZERO, rotation, random, SHAFT_JIGSAW, Direction.UP);

        if (top == null) {
            return null;
        }

        BlockPos pos = connect.subtract(top);
        BoundingBox box = element.getBoundingBox(templates, pos, rotation);
        // Waterlogging follows the jigsaw structure default.
        return new PoolElementStructurePiece(templates, element, pos, 0, rotation, box, LiquidSettings.APPLY_WATERLOGGING);
    }

    /// World position of the jigsaw called `name` that points in `facing`, or null.
    private static BlockPos jigsawPos(
            StructurePoolElement element,
            StructureTemplateManager templates,
            BlockPos pos,
            Rotation rotation,
            RandomSource random,
            String name,
            Direction facing
    ) {
        for (StructureTemplate.JigsawBlockInfo jigsaw : element.getShuffledJigsawBlocks(templates, pos, rotation, random)) {
            if (jigsaw.name() == null || !name.equals(jigsaw.name().toString())) {
                continue;
            }

            if (JigsawBlock.getFrontFacing(jigsaw.state()) == facing) {
                return jigsaw.pos();
            }
        }
        return null;
    }

    private static SinglePoolElement element(Structure.GenerationContext context, Identifier location) {
        Holder<StructureProcessorList> processors = context.registryAccess()
                .lookupOrThrow(Registries.PROCESSOR_LIST)
                .getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, Identifier.fromNamespaceAndPath("minecraft", "empty")));

        return StructurePoolElement.single(location.toString(), processors).apply(StructureTemplatePool.Projection.RIGID);
    }

    private static RandomSource randomFor(Structure.GenerationContext context, BoundingBox box) {
        return RandomSource.create(context.seed()
                ^ (long) box.getCenter().getX() * 341873128712L
                ^ (long) box.getCenter().getZ() * 132897987541L
                ^ (long) box.minY());
    }

    private static List<PoolElementStructurePiece> findMinerHouses(StructurePiecesBuilder collector) {
        List<PoolElementStructurePiece> found = new ArrayList<>();
        for (StructurePiece piece : ((StructurePiecesCollectorAccessor) collector).getPieces()) {
            if (piece instanceof PoolElementStructurePiece poolPiece && locationOf(poolPiece).filter(SHAFTS::containsKey).isPresent()) {
                found.add(poolPiece);
            }
        }
        return found;
    }

    private static Optional<Identifier> locationOf(PoolElementStructurePiece piece) {
        if (piece.getElement() instanceof SinglePoolElement single) {
            return ((SinglePoolElementAccessor) single).getLocation().left();
        }

        return Optional.empty();
    }
}
