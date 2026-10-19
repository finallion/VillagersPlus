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

/// Migrates stored inventories while the chunk still has its original `DataVersion`.
///
/// The hook misses opened workstations, which skip `WorldChunk.loadBlockEntity`.
/// Vanilla's fixer skips block entities with an unknown id.
@Mixin(VersionedChunkStorage.class)
public class VersionedChunkStorageMixin {

    @Inject(method = "updateChunkNbt", at = @At("HEAD"))
    private void villagersplus$migrateStoredItems(
            RegistryKey<World> worldKey,
            Supplier<PersistentStateManager> persistentStateManagerFactory,
            NbtCompound nbt,
            Optional<RegistryKey<MapCodec<? extends ChunkGenerator>>> generatorCodecKey,
            CallbackInfoReturnable<NbtCompound> cir
    ) {
        LegacyItemStacks.migrateChunk(nbt, VersionedChunkStorage.getDataVersion(nbt));
    }
}
