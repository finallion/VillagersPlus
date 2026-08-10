package com.lion.villagersplus.mixin;

import com.lion.villagersplus.util.LegacyItemStacks;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Supplier;

/// Updates this mod's stored inventories on the way in from an older world.
///
/// This is the one place where a chunk still carries the `DataVersion` it was written with, so the
/// conversion knows its exact starting point instead of guessing it from the shape of an entry.
///
/// A block entity is not a usable hook here. A full chunk hands its own out through `keepPacked`:
/// only an entry that was never instantiated goes to `WorldChunk.loadBlockEntity`, every
/// workstation the player has opened goes straight to `BlockEntity.createFromNbt`.
///
/// Running before vanilla's own fixer is safe because that fixer never reaches inside a block entity
/// whose id it does not know, which is the very reason this class has to exist.
@Mixin(VersionedChunkStorage.class)
public class VersionedChunkStorageMixin {

    @Inject(method = "updateChunkNbt", at = @At("HEAD"))
    private void villagersplus$migrateStoredItems(RegistryKey<World> worldKey,
                                                  Supplier<PersistentStateManager> persistentStateManagerFactory,
                                                  NbtCompound nbt,
                                                  Optional<RegistryKey<MapCodec<? extends ChunkGenerator>>> generatorCodecKey,
                                                  CallbackInfoReturnable<NbtCompound> cir) {
        LegacyItemStacks.migrateChunk(nbt, VersionedChunkStorage.getDataVersion(nbt));
    }
}
