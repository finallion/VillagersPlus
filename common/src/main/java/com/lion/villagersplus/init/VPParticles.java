package com.lion.villagersplus.init;

import com.lion.villagersplus.platform.RegistryHelper;
import net.minecraft.particle.DefaultParticleType;


public class VPParticles {

    public static final DefaultParticleType EXPERIENCE_PARTICLE = new DefaultParticleType(false);
    public static final DefaultParticleType BUBBLE_PARTICLE = new DefaultParticleType(false);


    static {
        RegistryHelper.registerParticleType("experience_particle", EXPERIENCE_PARTICLE);
        RegistryHelper.registerParticleType("bubble_particle", BUBBLE_PARTICLE);
    }

    public static void init() {

    }
}
