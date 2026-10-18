package com.lion.villagersplus.mixin;

import com.lion.villagersplus.util.LegacyItemStacks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// Updates this mod's stored inventories while the chunk still carries its stored `DataVersion`.
@Mixin(SimpleRegionStorage.class)
public class VersionedChunkStorageMixin {

    @Shadow
    @Final
    private DataFixTypes dataFixType;

    @Inject(method = "upgradeChunkTag(Lnet/minecraft/nbt/CompoundTag;ILnet/minecraft/nbt/CompoundTag;I)Lnet/minecraft/nbt/CompoundTag;", at = @At("HEAD"))
    private void villagersplus$migrateStoredItems(CompoundTag nbt, int defaultVersion, CompoundTag context, int targetVersion,
                                                  CallbackInfoReturnable<CompoundTag> cir) {
        if (dataFixType == DataFixTypes.CHUNK) {
            LegacyItemStacks.migrateChunk(nbt, NbtUtils.getDataVersion(nbt, defaultVersion), targetVersion);
        }
    }
}
