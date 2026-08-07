package com.lion.villagersplus.util;

import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;

/// Brings inventory entries written before 1.20.5 up to the component format.
///
/// Vanilla's fixer walks only the block entities it has a schema for, so a modded one keeps its
/// pre-component `Items` untouched. Those entries then meet an `ItemStack` codec that reads `count`
/// and `components`, silently ignores the `Count` and `tag` that are actually there, and yields a
/// bare item: a bucket with no fish variant, a bottle with no potion, a pickaxe with no
/// enchantments, and a stack of any size reduced to one.
///
/// The conversion is Mojang's own fixer, so no `tag` key is mapped onto a component by hand here.
public final class LegacyItemStacks {

    /// The version the old entries are read as. Anything below the componentization schema does the
    /// job; 1.20.1 is where this mod last wrote them, and starting there also picks up the item
    /// renames from 1.20.2 through 1.20.4.
    private static final int PRE_COMPONENT_DATA_VERSION = 3465;

    private LegacyItemStacks() {
    }

    /// Rewrites every pre-component entry of an `Items` list in place.
    ///
    /// The marker is a capital `Count`, which no entry written since 1.20.5 carries, so a current
    /// world walks the list and changes nothing.
    public static void fixInventory(NbtCompound blockEntityNbt) {
        NbtList items = blockEntityNbt.getListOrEmpty("Items");
        for (int i = 0; i < items.size(); i++) {
            NbtCompound entry = items.getCompoundOrEmpty(i);
            if (!entry.contains("Count")) {
                continue;
            }
            items.set(i, fix(entry));
        }
    }

    private static NbtCompound fix(NbtCompound entry) {
        // The slot is the inventory's own bookkeeping and means nothing to an item fixer, so it is
        // lifted out and put back afterwards rather than fed through.
        NbtElement slot = entry.get("Slot");
        NbtCompound stack = entry.copy();
        stack.remove("Slot");

        Dynamic<NbtElement> fixed = Schemas.getFixer().update(
                TypeReferences.ITEM_STACK,
                new Dynamic<>(NbtOps.INSTANCE, stack),
                PRE_COMPONENT_DATA_VERSION,
                SharedConstants.getGameVersion().dataVersion().id());

        NbtCompound result = (NbtCompound) fixed.getValue();
        if (slot != null) {
            result.put("Slot", slot);
        }
        return result;
    }
}
