package com.lion.villagersplus.mixin;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.util.LegacyItemStacks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// Componentizes this mod's stored inventories on the way in from a pre-1.20.5 world.
///
/// This is the last place the raw NBT is still reachable. `readData` receives a `ReadView` whose
/// codecs have already dropped everything they did not recognise, and by then the fish variant,
/// the potion and the enchantments are gone.
///
/// Guarded on the block entity id so nothing outside this mod is touched.
@Mixin(BlockEntity.class)
public class BlockEntityMixin {

    @Inject(method = "createFromNbt", at = @At("HEAD"))
    private static void villagersplus$fixLegacyInventory(BlockPos pos, BlockState state, NbtCompound nbt,
                                                         RegistryWrapper.WrapperLookup registries,
                                                         CallbackInfoReturnable<BlockEntity> cir) {
        if (nbt.getString("id", "").startsWith(VillagersPlus.MOD_ID + ":")) {
            LegacyItemStacks.fixInventory(nbt);
        }
    }
}
