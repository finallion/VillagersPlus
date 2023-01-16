package com.finallion.villagersplus.init;

import com.finallion.villagersplus.VillagersPlus;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {

    public static final DefaultParticleType EXPERIENCE_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType BUBBLE_PARTICLE = FabricParticleTypes.simple();

    public static void init() {
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(VillagersPlus.MOD_ID, "experience_particle"), EXPERIENCE_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(VillagersPlus.MOD_ID, "bubble_particle"), BUBBLE_PARTICLE);
    }
}
