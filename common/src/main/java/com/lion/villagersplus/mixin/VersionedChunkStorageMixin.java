package com.lion.villagersplus.mixin;

import com.lion.villagersplus.util.LegacyItemStacks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


/// Migrates stored inventories while the chunk still has its original `DataVersion`.
///
/// The hook misses opened workstations, which skip `WorldChunk.loadBlockEntity`.
/// Vanilla's fixer skips block entities with an unknown id.
@Mixin(VersionedChunkStorage.class)
public class VersionedChunkStorageMixin {

    @Inject(method = "updateChunkNbt(Lnet/minecraft/nbt/NbtCompound;ILnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/nbt/NbtCompound;", at = @At("HEAD"))
    private void villagersplus$migrateStoredItems(
            NbtCompound nbt,
            int dataVersion,
            NbtCompound context,
            CallbackInfoReturnable<NbtCompound> cir
    ) {
        LegacyItemStacks.migrateChunk(nbt, NbtHelper.getDataVersion(nbt, dataVersion));
    }
}
