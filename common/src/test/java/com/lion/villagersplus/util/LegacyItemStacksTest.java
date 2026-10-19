package com.lion.villagersplus.util;

import net.minecraft.SharedConstants;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.TagParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Tests the conversion on a hand-written chunk in the stored format.
class LegacyItemStacksTest {

    private static final int MC_1_20_1 = 3465;

    private static final int MC_1_21_5 = 4325;

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
    }

    @Test
    void aStackKeepsItsSize() {
        CompoundTag coal = migrateSingleStack("{Slot:1b,id:\"minecraft:coal\",Count:32b}");

        assertEquals(32, coal.getIntOr("count", -1), "count after migration");
        assertEquals(1, coal.getIntOr("Slot", -1), "the slot has to survive the fixer");
    }

    @Test
    void aPickaxeKeepsItsEnchantments() {
        CompoundTag pickaxe = migrateSingleStack(
                "{Slot:3b,id:\"minecraft:diamond_pickaxe\",Count:1b,"
                        + "tag:{Enchantments:[{id:\"minecraft:efficiency\",lvl:5s}],Damage:12}}");

        CompoundTag enchantments = pickaxe.getCompoundOrEmpty("components")
                .getCompoundOrEmpty("minecraft:enchantments");
        assertTrue(
                enchantments.toString().contains("minecraft:efficiency"),
                "enchantments after migration: " + pickaxe
        );
    }

    /// The variant shape depends on the target version, not on the stored form.
    @Test
    void aBucketKeepsItsFishVariant() {
        CompoundTag bucket = migrateSingleStack(
                "{Slot:0b,id:\"minecraft:tropical_fish_bucket\",Count:1b,tag:{BucketVariantTag:117506305}}");

        CompoundTag components = bucket.getCompoundOrEmpty("components");
        assertTrue(
                components.contains("minecraft:tropical_fish/base_color"),
                "fish variant after migration: " + bucket
        );
        assertTrue(components.contains("minecraft:tropical_fish/pattern"), "fish variant after migration: " + bucket);
    }

    @Test
    void aBucketKeepsItsAxolotlVariant() {
        CompoundTag bucket = migrateSingleStack(
                "{Slot:0b,id:\"minecraft:axolotl_bucket\",Count:1b,tag:{Variant:2,Age:0,Health:14.0f}}");

        CompoundTag components = bucket.getCompoundOrEmpty("components");
        assertTrue(components.contains("minecraft:axolotl/variant"), "axolotl variant after migration: " + bucket);
    }

    @Test
    void aCurrentChunkIsLeftAlone() {
        String entry = "{Slot:1b,id:\"minecraft:coal\",count:32}";
        CompoundTag chunk = chunkWith(entry);
        int current = SharedConstants.getCurrentVersion().dataVersion().version();

        LegacyItemStacks.migrateChunk(chunk, current);

        assertEquals(parse(entry), firstStack(chunk), "a current chunk must come back untouched");
    }

    /// The vanilla fixer leaves the migrated entries as they are.
    @Test
    void vanillasChunkFixerLeavesTheMigratedEntriesAlone() {
        CompoundTag chunk = chunkWith("{Slot:1b,id:\"minecraft:coal\",Count:32b}");
        chunk.putInt("DataVersion", MC_1_20_1);
        int current = SharedConstants.getCurrentVersion().dataVersion().version();

        LegacyItemStacks.migrateChunk(chunk, MC_1_20_1);
        CompoundTag afterVanilla = DataFixTypes.CHUNK.update(DataFixers.getDataFixer(), chunk, MC_1_20_1, current);

        assertEquals(
                32,
                firstStack(afterVanilla).getIntOr("count", -1),
                "vanilla's fixer must not touch the entries again: " + afterVanilla
        );
    }

    @Test
    void migratingInTwoStepsEqualsMigratingAtOnce() {
        String pickaxe = "{Slot:3b,id:\"minecraft:diamond_pickaxe\",Count:1b,"
                + "tag:{Enchantments:[{id:\"minecraft:efficiency\",lvl:5s}],Damage:12}}";
        String bucket = "{Slot:0b,id:\"minecraft:tropical_fish_bucket\",Count:1b,tag:{BucketVariantTag:117506305}}";
        int current = SharedConstants.getCurrentVersion().dataVersion().version();
        CompoundTag atOnce = chunkWith(pickaxe, bucket);
        CompoundTag inSteps = chunkWith(pickaxe, bucket);

        LegacyItemStacks.migrateChunk(atOnce, MC_1_20_1, current);
        LegacyItemStacks.migrateChunk(inSteps, MC_1_20_1, MC_1_21_5);
        LegacyItemStacks.migrateChunk(inSteps, MC_1_21_5, current);

        assertEquals(atOnce, inSteps, "two steps must give the same stacks as one");
    }

    @Test
    void aBlockEntityOfAnotherModIsLeftAlone() {
        CompoundTag chunk = parse("{block_entities:[{id:\"someothermod:crate\",x:1,y:2,z:3,"
                + "Items:[{Slot:0b,id:\"minecraft:coal\",Count:32b}]}]}");
        CompoundTag before = firstStack(chunk).copy();

        LegacyItemStacks.migrateChunk(chunk, MC_1_20_1);

        assertEquals(before, firstStack(chunk), "only this mod's block entities may be rewritten");
    }

    private static CompoundTag migrateSingleStack(String stackSnbt) {
        CompoundTag chunk = chunkWith(stackSnbt);
        LegacyItemStacks.migrateChunk(chunk, MC_1_20_1);
        return firstStack(chunk);
    }

    private static CompoundTag chunkWith(String... stackSnbt) {
        return parse("{block_entities:[{id:\"villagersplus:ore_grinder_block_entity\",x:12,y:-40,z:98,"
                + "Items:[" + String.join(",", stackSnbt) + "]}]}");
    }

    private static CompoundTag firstStack(CompoundTag chunk) {
        ListTag blockEntities = chunk.getListOrEmpty("block_entities");
        return blockEntities.getCompoundOrEmpty(0).getListOrEmpty("Items").getCompoundOrEmpty(0);
    }

    private static CompoundTag parse(String snbt) {
        try {
            return TagParser.parseCompoundFully(snbt);
        } catch (Exception e) {
            throw new AssertionError("bad test data: " + snbt, e);
        }
    }
}
