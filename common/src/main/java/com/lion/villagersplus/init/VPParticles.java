package com.lion.villagersplus.init;

import com.lion.villagersplus.platform.RegistryHelper;
import net.minecraft.particle.SimpleParticleType;


public class VPParticles {

    public static final SimpleParticleType EXPERIENCE_PARTICLE = new SimpleParticleType(false);
    public static final SimpleParticleType BUBBLE_PARTICLE = new SimpleParticleType(false);


    static {
        RegistryHelper.registerParticleType("experience_particle", EXPERIENCE_PARTICLE);
        RegistryHelper.registerParticleType("bubble_particle", BUBBLE_PARTICLE);
    }

    public static void init() {

    }
}
