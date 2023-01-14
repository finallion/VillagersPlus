package com.finallion.villagersplus.init;

import com.finallion.villagersplus.VillagersPlus;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModParticles {

    public static final DefaultParticleType EXPERIENCE_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType BUBBLE_PARTICLE = FabricParticleTypes.simple();

    public static void init() {
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(VillagersPlus.MOD_ID, "experience_particle"), EXPERIENCE_PARTICLE);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(VillagersPlus.MOD_ID, "bubble_particle"), BUBBLE_PARTICLE);
    }
}
