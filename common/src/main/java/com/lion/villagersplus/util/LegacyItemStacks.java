package com.lion.villagersplus.util;

import com.lion.villagersplus.VillagersPlus;
import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// Brings this mod's stored inventories up to the current item format.
///
/// Vanilla's fixer skips modded block entities, so their `Items` keep the old format.
public final class LegacyItemStacks {

    private static final String BLOCK_ENTITIES = "block_entities";
    private static final String ITEMS = "Items";
    private static final String SLOT = "Slot";
    private static final String ID = "id";

    private static final Logger LOGGER = LoggerFactory.getLogger(VillagersPlus.MOD_ID);

    private static volatile int announcedVersion;

    private LegacyItemStacks() {}

    /// A `DataVersion` of -1 means the chunk has none.
    public static void migrateChunk(NbtCompound chunkNbt, int fromVersion) {
        int current = SharedConstants.getGameVersion().dataVersion().id();

        if (fromVersion <= 0 || fromVersion >= current) {
            return;
        }

        NbtList blockEntities = chunkNbt.getListOrEmpty(BLOCK_ENTITIES);
        for (int i = 0; i < blockEntities.size(); i++) {
            NbtCompound blockEntity = blockEntities.getCompoundOrEmpty(i);
            String id = blockEntity.getString(ID, "");

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

    private static void migrateInventory(NbtCompound blockEntity, String id, int from, int to) {
        NbtList items = blockEntity.getListOrEmpty(ITEMS);
        StringBuilder changed = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            NbtCompound before = items.getCompoundOrEmpty(i);

            if (before.isEmpty()) {
                continue;
            }

            NbtCompound after;
            try {
                after = update(before, from, to);
            } catch (RuntimeException e) {
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
            LOGGER.info(
                    "{} at {},{},{}: {}",
                    id,
                    blockEntity.getInt("x", 0),
                    blockEntity.getInt("y", 0),
                    blockEntity.getInt("z", 0),
                    changed
            );
        }
    }

    private static NbtCompound update(NbtCompound entry, int from, int to) {
        // Lift the slot out of the item fixer's reach.
        NbtElement slot = entry.get(SLOT);
        NbtCompound stack = entry.copy();
        stack.remove(SLOT);

        Dynamic<NbtElement> fixed = Schemas.getFixer().update(
                TypeReferences.ITEM_STACK,
                new Dynamic<>(NbtOps.INSTANCE, stack),
                from,
                to
        );

        NbtCompound result = (NbtCompound) fixed.getValue();

        if (slot != null) {
            result.put(SLOT, slot);
        }

        return result;
    }

    private static String describe(NbtCompound before, NbtCompound after) {
        StringBuilder line = new StringBuilder()
                .append("slot ").append(before.getInt(SLOT, -1))
                .append(' ').append(after.getString(ID, before.getString(ID, "?")))
                .append(' ')
                .append(count(before))
                .append(" -> ")
                .append(count(after));

        NbtCompound components = after.getCompoundOrEmpty("components");

        if (!components.isEmpty()) {
            line.append(' ').append(components.getKeys());
        }

        return line.toString();
    }

    /// Reads `Count` or `count`. A missing key means one.
    private static int count(NbtCompound stack) {
        int componentised = stack.getInt("count", -1);
        return componentised >= 0 ? componentised : stack.getInt("Count", 1);
    }
}
