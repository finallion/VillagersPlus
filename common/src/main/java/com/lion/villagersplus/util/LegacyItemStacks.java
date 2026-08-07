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
/// reduced to one, a bottle with no potion, a bucket with no fish variant.
///
/// Two breaks matter so far. 1.20.5 moved item NBT into components, and 1.21.5 moved the bucket's
/// entity variant out of that NBT into components of its own. Each is recognised by a key that
/// cannot occur in anything written after it, so a current world is walked and left alone.
///
/// The conversion is Mojang's own fixer. Nothing here maps an old key onto a component by hand.
public final class LegacyItemStacks {

    /// Written before 1.20.5, when a stack was `{id, Count, tag}`.
    private static final int PRE_COMPONENTS = 3465;

    /// Written between 1.20.5 and 1.21.4: componentized, but a bucket still kept its variant as
    /// `BucketVariantTag` inside `bucket_entity_data`. 3955 is 1.21.1.
    private static final int PRE_VARIANT_COMPONENTS = 3955;

    private static final String BUCKET_ENTITY_DATA = "minecraft:bucket_entity_data";

    private LegacyItemStacks() {
    }

    /// Rewrites every outdated entry of an `Items` list in place.
    public static void fixInventory(NbtCompound blockEntityNbt) {
        NbtList items = blockEntityNbt.getListOrEmpty("Items");
        int current = SharedConstants.getGameVersion().dataVersion().id();

        for (int i = 0; i < items.size(); i++) {
            NbtCompound entry = items.getCompoundOrEmpty(i);
            int from = legacyDataVersion(entry);
            if (from < 0 || from >= current) {
                continue;
            }
            items.set(i, update(entry, from, current));
        }
    }

    /// The version an entry was written by, or -1 when it is already current.
    ///
    /// Both markers are positive identification rather than a guess: a capital `Count` is gone from
    /// everything 1.20.5 wrote, and `BucketVariantTag` is gone from everything 1.21.5 wrote. An
    /// entry that carries neither is left untouched, because running fixes over data that already
    /// had them applied is not safe in general.
    private static int legacyDataVersion(NbtCompound entry) {
        if (entry.contains("Count")) {
            return PRE_COMPONENTS;
        }
        boolean oldBucket = entry.getCompound("components")
                .flatMap(components -> components.getCompound(BUCKET_ENTITY_DATA))
                .map(bucket -> bucket.contains("BucketVariantTag"))
                .orElse(false);
        return oldBucket ? PRE_VARIANT_COMPONENTS : -1;
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
