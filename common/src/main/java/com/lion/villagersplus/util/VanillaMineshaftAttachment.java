package com.lion.villagersplus.util;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.mixin.SinglePoolElementAccessor;
import com.lion.villagersplus.mixin.StructurePiecesCollectorAccessor;
import net.minecraft.block.JigsawBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureLiquidSettings;
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

/**
 * Digs the miner's shaft below the miner's house and grows a vanilla mineshaft out of its bottom.
 * <p>
 * Both halves have to happen outside the jigsaw system, because nothing below the street surface can
 * be reached by a jigsaw at all. Every vanilla street piece points its {@code building_entrance}
 * jigsaw at a block that is still inside its own bounding box, so {@code StructurePoolBasedGenerator}
 * validates the house against {@code cuboid(street piece box)} rather than the structure-wide free
 * space - and hands that very same shape down to the house's own children. That box only ever grows
 * upwards (by the houses pool's {@code getHighestY}), so a downward jigsaw is rejected on the first
 * block it tries to claim. Silently: the generator just moves on to the next candidate.
 * <p>
 * Plain structure pieces appended to the collector after the jigsaw run are subject to neither the
 * overlap test nor {@code max_distance_from_center}, so that is where the shaft is built. The
 * {@code villagersplus:mineshaft_start} jigsaw block in the house stays as the marker that tells us
 * where to start digging (and turns into its {@code final_state} cave air, opening the floor).
 */
public final class VanillaMineshaftAttachment {

    /** Jigsaw name in the miner's house that marks the top of the shaft. */
    private static final String SHAFT_START_JIGSAW = "villagersplus:mineshaft_start";
    /** Jigsaw name carried by both ends of a shaft segment. */
    private static final String SHAFT_JIGSAW = "villagersplus:mineshaft";

    /** How far below the house floor the shaft digs before the closing piece goes in. */
    private static final int SHAFT_DEPTH = 30;
    /** Backstop so a template without a downward jigsaw cannot spin this loop. */
    private static final int MAX_SEGMENTS = 16;

    /** The shaft templates belonging to one miner's house variant. */
    private record Shaft(Identifier segment, Identifier end) { }

    private static final Map<Identifier, Shaft> SHAFTS = Map.of(
            id("village/plains/plains_miner"), new Shaft(id("mine/mine_pool/plains_mineshaft"), id("mine/mine_pool/plains_mineshaft_end")),
            id("village/savanna/savanna_miner"), new Shaft(id("mine/mine_pool/savanna_mineshaft"), id("mine/mine_pool/savanna_mineshaft_end")),
            id("village/taiga/taiga_miner"), new Shaft(id("mine/mine_pool/taiga_mineshaft"), id("mine/mine_pool/taiga_mineshaft_end")));

    /** Every template this class places itself, for {@link #isBeardExempt}. */
    private static final Set<Identifier> SHAFT_TEMPLATES = SHAFTS.values().stream()
            .flatMap(shaft -> Stream.of(shaft.segment(), shaft.end()))
            .collect(Collectors.toUnmodifiableSet());

    private VanillaMineshaftAttachment() {
    }

    private static Identifier id(String path) {
        return Identifier.of(VillagersPlus.MOD_ID, path);
    }

    /**
     * Called for every jigsaw structure in the game, so it has to be free for anything that is not
     * ours: without one of our miner houses among the pieces this returns after a single list walk
     * and nothing is added.
     */
    public static void append(StructurePiecesCollector collector, Structure.Context context) {
        for (PoolStructurePiece hut : findMinerHouses(collector)) {
            appendShaft(collector, context, hut);
        }
    }

    /**
     * Whether a piece must be left out of the structure's terrain adaptation, asked by
     * {@link com.lion.villagersplus.mixin.StructurePieceMixin} for every piece of every bearded
     * structure start near a chunk being shaped.
     * <p>
     * Everything this class appends sits deep underground inside a village start, and a village is
     * {@code beard_thin}: terrain gets carved above each piece and packed below it. Up there that is
     * what makes a house sit flush on a hill; down here it dissolves the rock the shaft and the
     * mineshaft are supposed to be cut into. Vanilla builds its own mineshafts with no adaptation at
     * all, and that is the look we want.
     * <p>
     * Both tests are stateless on purpose. A flag set at generation time would be lost as soon as the
     * structure start is written to disk and read back - which happens routinely, because a chunk's
     * noise is shaped long after the neighbouring chunk that created the start - and bearding would
     * quietly return for part of the shaft.
     */
    public static boolean isBeardExempt(StructurePiece piece) {
        // No structure with a terrain adaptation places mineshaft pieces, so any that reach this
        // point are ours: vanilla's own mineshaft declares none and never enters the sampler.
        StructurePieceType type = piece.getType();
        if (type == StructurePieceType.MINESHAFT_CORRIDOR || type == StructurePieceType.MINESHAFT_CROSSING
                || type == StructurePieceType.MINESHAFT_STAIRS || type == StructurePieceType.MINESHAFT_ROOM) {
            return true;
        }
        // The shaft itself is made of ordinary pool pieces, so those have to be named.
        return piece instanceof PoolStructurePiece pool
                && locationOf(pool).filter(SHAFT_TEMPLATES::contains).isPresent();
    }

    private static void appendShaft(StructurePiecesCollector collector, Structure.Context context, PoolStructurePiece hut) {
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

        // Stop before the world floor even if SHAFT_DEPTH would reach past it.
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

    /**
     * Hangs real {@link MineshaftGenerator} pieces off the side of the shaft's closing piece, the way
     * {@code MineshaftStructure} grows a mineshaft off its own starting room.
     * <p>
     * The corridor leaves through one of the four walls instead of starting in the middle of the
     * shaft: it begins exactly on the wall column, so its air fill punches a doorway through that one
     * layer and everything beyond it is dug fresh out of the rock.
     */
    private static void appendVanillaMineshaft(StructurePiecesCollector collector, Structure.Context context, List<PoolStructurePiece> shaft) {
        BlockBox anchor = shaft.get(shaft.size() - 1).getBoundingBox();
        BlockPos center = anchor.getCenter();

        // Derived from the anchor rather than reusing the shaft's random, so the mineshaft layout stays
        // put even if the number of shaft segments above it ever changes.
        Random random = randomFor(context, anchor);

        Direction direction = Direction.fromHorizontal(random.nextInt(4));
        int y = anchor.getMinY() + 1;

        // MineshaftCorridor#getBoundingBox reads the position as the corner the corridor grows away
        // from: the near wall on the axis it travels along, and the low edge of the three-wide span on
        // the other one. Centring that span on the shaft puts the doorway in the middle of the wall.
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

        // Measured against an empty holder because the first corridor is meant to bite into the shaft
        // wall. Against the real collector every length it tries would be rejected, it would return
        // null, and the mineshaft would silently never appear.
        BlockBox box = MineshaftGenerator.MineshaftCorridor.getBoundingBox(new StructurePiecesCollector(), random, startX, y, startZ, direction);
        if (box == null) {
            return;
        }

        // Everything after the first corridor is laid out in a collector of its own, seeded with the
        // shaft so the branches keep avoiding it. Handing them straight to the real collector would
        // mean taking every piece the generator produces, and the ones too far out cannot be built.
        StructurePiecesCollector staging = new StructurePiecesCollector();
        for (PoolStructurePiece piece : shaft) {
            staging.addPiece(piece);
        }

        MineshaftGenerator.MineshaftCorridor root = new MineshaftGenerator.MineshaftCorridor(0, random, box, direction, MineshaftStructure.Type.NORMAL);
        staging.addPiece(root);
        // Recurses through MineshaftGenerator#pieceGenerator, which caps itself at chain length 8 and 80 blocks from the root.
        root.fillOpenings(root, staging, random);

        // addPiece only ever appends, so everything past the seeded shaft is what was generated here.
        List<StructurePiece> staged = ((StructurePiecesCollectorAccessor) staging).getPieces();
        for (StructurePiece piece : staged.subList(shaft.size(), staged.size())) {
            if (isReachable(context.chunkPos(), piece.getBoundingBox())) {
                collector.addPiece(piece);
            }
        }
    }

    /**
     * Whether a piece is close enough to the village's own chunk to ever be placed.
     * <p>
     * A chunk only builds the pieces of structure starts it holds a reference to, and
     * {@code ChunkGenerator#addStructureReferences} hands those out no further than eight chunks from
     * the chunk that owns the start. The village stays well inside that on its own, but a mineshaft
     * reaching its full 80 blocks from a miner's house that already sits at the edge of the village
     * does not - and a piece that only half of its chunks know about is built as far as those chunks
     * and then stops dead in mid-air, cut along the chunk border. Dropping it whole leaves an honest
     * dead end instead.
     */
    private static boolean isReachable(ChunkPos origin, BlockBox box) {
        int reach = 8 * 16;
        return box.getMinX() >= origin.getStartX() - reach && box.getMaxX() <= origin.getEndX() + reach
                && box.getMinZ() >= origin.getStartZ() - reach && box.getMaxZ() <= origin.getEndZ() + reach;
    }

    /**
     * Positions a shaft template so that its upward jigsaw lands exactly on {@code connect}. Both the
     * jigsaw offsets and the bounding box are read in the element's own origin frame and shifted by the
     * same delta, which is how {@code StructurePoolBasedGenerator} places a piece too.
     */
    private static PoolStructurePiece place(SinglePoolElement element, StructureTemplateManager templates, BlockRotation rotation, BlockPos connect, Random random) {
        BlockPos top = jigsawPos(element, templates, BlockPos.ORIGIN, rotation, random, SHAFT_JIGSAW, Direction.UP);
        if (top == null) {
            return null;
        }

        BlockPos pos = connect.subtract(top);
        BlockBox box = element.getBoundingBox(templates, pos, rotation);
        // Jigsaw pieces carry their liquid handling explicitly since 1.21. APPLY_WATERLOGGING is what
        // JigsawStructure defaults to, so this keeps the pre-1.21 behaviour.
        return new PoolStructurePiece(templates, element, pos, 0, rotation, box, StructureLiquidSettings.APPLY_WATERLOGGING);
    }

    /** World position of the jigsaw block called {@code name} that points in {@code facing}, or null. */
    private static BlockPos jigsawPos(StructurePoolElement element, StructureTemplateManager templates, BlockPos pos, BlockRotation rotation, Random random, String name, Direction facing) {
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
                .entryOf(RegistryKey.of(RegistryKeys.PROCESSOR_LIST, Identifier.of("minecraft", "empty")));

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
