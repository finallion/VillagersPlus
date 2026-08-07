package com.lion.villagersplus.mixin;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.util.LegacyItemStacks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// Updates this mod's stored inventories on the way in from an older world.
///
/// `loadBlockEntity` is where a chunk hands a block entity its saved NBT, and the last point at
/// which that NBT is still raw. Afterwards the inventory codec has run and whatever it did not
/// recognise is already gone. `BlockEntity.createFromNbt` looks like the obvious hook and is not
/// one: nothing on the chunk loading path calls it.
///
/// Guarded on the block entity id so nothing outside this mod is touched.
@Mixin(WorldChunk.class)
public class WorldChunkMixin {

    @Inject(method = "loadBlockEntity", at = @At("HEAD"))
    private void villagersplus$fixLegacyInventory(BlockPos pos, NbtCompound nbt,
                                                  CallbackInfoReturnable<BlockEntity> cir) {
        if (nbt.getString("id", "").startsWith(VillagersPlus.MOD_ID + ":")) {
            LegacyItemStacks.fixInventory(nbt);
        }
    }
}
