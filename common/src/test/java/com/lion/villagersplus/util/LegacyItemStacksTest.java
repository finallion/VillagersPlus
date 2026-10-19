package com.lion.villagersplus.util;

import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.StringNbtReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Tests the conversion on a hand-written chunk, without a world.
class LegacyItemStacksTest {

    private static final int MC_1_20_1 = 3465;

    @BeforeAll
    static void bootstrap() {
        SharedConstants.createGameVersion();
    }

    @Test
    void aStackKeepsItsSize() {
        NbtCompound coal = migrateSingleStack("{Slot:1b,id:\"minecraft:coal\",Count:32b}");

        assertEquals(32, coal.getInt("count", -1), "count after migration");
        assertEquals(1, coal.getInt("Slot", -1), "the slot has to survive the fixer");
    }

    @Test
    void aPickaxeKeepsItsEnchantments() {
        NbtCompound pickaxe = migrateSingleStack(
                "{Slot:3b,id:\"minecraft:diamond_pickaxe\",Count:1b,"
                        + "tag:{Enchantments:[{id:\"minecraft:efficiency\",lvl:5s}],Damage:12}}");

        NbtCompound enchantments = pickaxe.getCompoundOrEmpty("components")
                .getCompoundOrEmpty("minecraft:enchantments");
        assertTrue(
                enchantments.toString().contains("minecraft:efficiency"),
                "enchantments after migration: " + pickaxe
        );
    }

    /// The shape of the fish variant depends on the target version, not on how it was stored.
    @Test
    void aBucketKeepsItsFishVariant() {
        NbtCompound bucket = migrateSingleStack(
                "{Slot:0b,id:\"minecraft:tropical_fish_bucket\",Count:1b,tag:{BucketVariantTag:117506305}}");

        NbtCompound components = bucket.getCompoundOrEmpty("components");
        assertTrue(
                components.contains("minecraft:tropical_fish/base_color"),
                "fish variant after migration: " + bucket
        );
        assertTrue(components.contains("minecraft:tropical_fish/pattern"), "fish variant after migration: " + bucket);
    }

    @Test
    void aBucketKeepsItsAxolotlVariant() {
        NbtCompound bucket = migrateSingleStack(
                "{Slot:0b,id:\"minecraft:axolotl_bucket\",Count:1b,tag:{Variant:2,Age:0,Health:14.0f}}");

        NbtCompound components = bucket.getCompoundOrEmpty("components");
        assertTrue(components.contains("minecraft:axolotl/variant"), "axolotl variant after migration: " + bucket);
    }

    @Test
    void aCurrentChunkIsLeftAlone() {
        String entry = "{Slot:1b,id:\"minecraft:coal\",count:32}";
        NbtCompound chunk = chunkWith(entry);
        int current = SharedConstants.getGameVersion().dataVersion().id();

        LegacyItemStacks.migrateChunk(chunk, current);

        assertEquals(parse(entry), firstStack(chunk), "a current chunk must come back untouched");
    }

    /// Vanilla's fixer skips unknown block entities.
    @Test
    void vanillasChunkFixerLeavesTheMigratedEntriesAlone() {
        NbtCompound chunk = chunkWith("{Slot:1b,id:\"minecraft:coal\",Count:32b}");
        chunk.putInt("DataVersion", MC_1_20_1);
        int current = SharedConstants.getGameVersion().dataVersion().id();

        LegacyItemStacks.migrateChunk(chunk, MC_1_20_1);
        NbtCompound afterVanilla = DataFixTypes.CHUNK.update(Schemas.getFixer(), chunk, MC_1_20_1, current);

        assertEquals(
                32,
                firstStack(afterVanilla).getInt("count", -1),
                "vanilla's fixer must not touch the entries again: " + afterVanilla
        );
    }

    @Test
    void aBlockEntityOfAnotherModIsLeftAlone() {
        NbtCompound chunk = parse("{block_entities:[{id:\"someothermod:crate\",x:1,y:2,z:3,"
                + "Items:[{Slot:0b,id:\"minecraft:coal\",Count:32b}]}]}");
        NbtCompound before = firstStack(chunk).copy();

        LegacyItemStacks.migrateChunk(chunk, MC_1_20_1);

        assertEquals(before, firstStack(chunk), "only this mod's block entities may be rewritten");
    }

    private static NbtCompound migrateSingleStack(String stackSnbt) {
        NbtCompound chunk = chunkWith(stackSnbt);
        LegacyItemStacks.migrateChunk(chunk, MC_1_20_1);
        return firstStack(chunk);
    }

    private static NbtCompound chunkWith(String... stackSnbt) {
        return parse("{block_entities:[{id:\"villagersplus:ore_grinder_block_entity\",x:12,y:-40,z:98,"
                + "Items:[" + String.join(",", stackSnbt) + "]}]}");
    }

    private static NbtCompound firstStack(NbtCompound chunk) {
        NbtList blockEntities = chunk.getListOrEmpty("block_entities");
        return blockEntities.getCompoundOrEmpty(0).getListOrEmpty("Items").getCompoundOrEmpty(0);
    }

    private static NbtCompound parse(String snbt) {
        try {
            return StringNbtReader.readCompound(snbt);
        } catch (Exception e) {
            throw new AssertionError("bad test data: " + snbt, e);
        }
    }
}
