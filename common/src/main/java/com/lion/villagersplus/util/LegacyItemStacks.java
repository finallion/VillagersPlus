package com.lion.villagersplus.util;

import com.lion.villagersplus.VillagersPlus;
import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// Converts this mod's stored inventories to the current item format when a world loads.
///
/// Vanilla's chunk fixer skips modded block entities, so their `Items` lose counts, variants and enchantments.
/// The conversion uses the vanilla fixer, driven by the `DataVersion` of the chunk file.
public final class LegacyItemStacks {

    private static final String BLOCK_ENTITIES = "block_entities";
    private static final String ITEMS = "Items";
    private static final String SLOT = "Slot";
    private static final String ID = "id";

    /// Own logger, because the logger of [VillagersPlus] pulls in the config.
    private static final Logger LOGGER = LoggerFactory.getLogger(VillagersPlus.MOD_ID);

    private static volatile int announcedVersion;

    private LegacyItemStacks() {
    }

    /// Rewrites the stored items of this mod's block entities in one chunk. -1 means no `DataVersion`.
    public static void migrateChunk(CompoundTag chunkNbt, int fromVersion) {
        migrateChunk(chunkNbt, fromVersion, SharedConstants.getCurrentVersion().dataVersion().version());
    }

    /// Stops at `current`, so stepwise callers never migrate a range twice.
    public static void migrateChunk(CompoundTag chunkNbt, int fromVersion, int current) {
        if (fromVersion <= 0 || fromVersion >= current) {
            return;
        }

        ListTag blockEntities = chunkNbt.getListOrEmpty(BLOCK_ENTITIES);
        for (int i = 0; i < blockEntities.size(); i++) {
            CompoundTag blockEntity = blockEntities.getCompoundOrEmpty(i);
            String id = blockEntity.getStringOr(ID, "");
            if (id.startsWith(VillagersPlus.MOD_ID + ":")) {
                announce(fromVersion, current);
                migrateInventory(blockEntity, id, fromVersion, current);
            }
        }
    }

    private static void announce(int fromVersion, int current) {
        if (announcedVersion != fromVersion) {
            announcedVersion = fromVersion;
            LOGGER.info("Migrating stored items from DataVersion {} to {}", fromVersion, current);
        }
    }

    private static void migrateInventory(CompoundTag blockEntity, String id, int from, int to) {
        ListTag items = blockEntity.getListOrEmpty(ITEMS);
        StringBuilder changed = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            CompoundTag before = items.getCompoundOrEmpty(i);
            if (before.isEmpty()) {
                continue;
            }

            CompoundTag after;
            try {
                after = update(before, from, to);
            } catch (RuntimeException e) {
                // Log the failing entry and continue. One bad entry does not stop the chunk.
                LOGGER.error("Could not migrate {} in {}", before, id, e);
                continue;
            }

            items.set(i, after);
            if (!changed.isEmpty()) {
                changed.append(", ");
            }
            changed.append(describe(before, after));
        }

        if (!changed.isEmpty()) {
            LOGGER.info("{} at {},{},{}: {}", id,
                    blockEntity.getIntOr("x", 0), blockEntity.getIntOr("y", 0), blockEntity.getIntOr("z", 0), changed);
        }
    }

    private static CompoundTag update(CompoundTag entry, int from, int to) {
        // The slot is inventory bookkeeping, so it stays out of the item fixer.
        Tag slot = entry.get(SLOT);
        CompoundTag stack = entry.copy();
        stack.remove(SLOT);

        Dynamic<Tag> fixed = DataFixers.getDataFixer().update(
                References.ITEM_STACK,
                new Dynamic<>(NbtOps.INSTANCE, stack),
                from,
                to);

        CompoundTag result = (CompoundTag) fixed.getValue();
        if (slot != null) {
            result.put(SLOT, slot);
        }
        return result;
    }

    /// Summarises one slot, for example `slot 3 minecraft:diamond_pickaxe 1 -> 1 [minecraft:enchantments]`.
    private static String describe(CompoundTag before, CompoundTag after) {
        StringBuilder line = new StringBuilder()
                .append("slot ").append(before.getIntOr(SLOT, -1))
                .append(' ').append(after.getStringOr(ID, before.getStringOr(ID, "?")))
                .append(' ').append(count(before)).append(" -> ").append(count(after));

        CompoundTag components = after.getCompoundOrEmpty("components");
        if (!components.isEmpty()) {
            line.append(' ').append(components.keySet());
        }
        return line.toString();
    }

    /// Reads both key spellings, `count` and `Count`. A missing field reads as one.
    private static int count(CompoundTag stack) {
        int componentised = stack.getIntOr("count", -1);
        return componentised >= 0 ? componentised : stack.getIntOr("Count", 1);
    }
}
