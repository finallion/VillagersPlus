package com.lion.villagersplus.util;

import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;

/// Brings stored inventory entries up to the current item format.
///
/// Vanilla's fixer walks only the block entities it has a schema for, so a modded one keeps whatever
/// shape it was last written in. The entries then meet a codec that reads today's fields, ignores
/// the ones that are actually there, and hands back something diminished: a stack of any size
/// reduced to one, a bottle with no potion, a pickaxe with no enchantments.
///
/// The break that matters here is 1.20.5 moving item NBT into components. It is recognised by a key
/// that cannot occur in anything written after it, so a current world is walked and left alone.
///
/// The conversion is Mojang's own fixer. Nothing here maps an old key onto a component by hand.
public final class LegacyItemStacks {

    /// Written before 1.20.5, when a stack was `{id, Count, tag}`. Starting at 1.20.1 also picks up
    /// the item renames from 1.20.2 through 1.20.4.
    private static final int PRE_COMPONENTS = 3465;

    private LegacyItemStacks() {
    }

    /// Rewrites every outdated entry of an `Items` list in place.
    public static void fixInventory(NbtCompound blockEntityNbt) {
        NbtList items = blockEntityNbt.getList("Items", NbtElement.COMPOUND_TYPE);
        int current = SharedConstants.getGameVersion().getSaveVersion().getId();

        for (int i = 0; i < items.size(); i++) {
            NbtCompound entry = items.getCompound(i);
            int from = legacyDataVersion(entry);
            if (from < 0 || from >= current) {
                continue;
            }
            items.set(i, update(entry, from, current));
        }
    }

    /// The version an entry was written by, or -1 when it is already current.
    ///
    /// The marker is positive identification rather than a guess: a capital `Count` is gone from
    /// everything 1.20.5 wrote. An entry without it is left untouched, because running fixes over
    /// data that already had them applied is not safe in general.
    private static int legacyDataVersion(NbtCompound entry) {
        return entry.contains("Count") ? PRE_COMPONENTS : -1;
    }

    private static NbtCompound update(NbtCompound entry, int from, int to) {
        // The slot is the inventory's own bookkeeping and means nothing to an item fixer, so it is
        // lifted out and put back afterwards rather than fed through.
        NbtElement slot = entry.get("Slot");
        NbtCompound stack = entry.copy();
        stack.remove("Slot");

        Dynamic<NbtElement> fixed = Schemas.getFixer().update(
                TypeReferences.ITEM_STACK,
                new Dynamic<>(NbtOps.INSTANCE, stack),
                from,
                to);

        NbtCompound result = (NbtCompound) fixed.getValue();
        if (slot != null) {
            result.put("Slot", slot);
        }
        return result;
    }
}
