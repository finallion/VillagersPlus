package com.lion.villagersplus.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.MathHelper;

public class ExperienceParticle extends BillboardParticle {

    public ExperienceParticle(
            ClientWorld world,
            SpriteProvider spriteProvider,
            double x,
            double y,
            double z,
            double velocityX,
            double velocityY,
            double velocityZ
    ) {
        super(world, x, y - 0.125D, z, velocityX, velocityY, velocityZ, spriteProvider.getSprite(world.random));
        this.setBoundingBoxSpacing(0.01F, 0.01F);
        this.scale *= this.random.nextFloat() * 0.6F + 0.6F;
        this.maxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
        this.collidesWithWorld = false;
        this.velocityMultiplier = 1.0F;
        this.gravityStrength = 0.0F;
    }

    protected RenderType getRenderType() {
        return RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    public static class ExperienceParticleFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public ExperienceParticleFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(
                SimpleParticleType defaultParticleType,
                ClientWorld clientWorld,
                double d,
                double e,
                double f,
                double g,
                double h,
                double i,
                Random random
        ) {
            ExperienceParticle waterSuspendParticle = new ExperienceParticle(clientWorld, this.spriteProvider, d, e, f, 0.0D, -0.800000011920929D, 0.0D);
            waterSuspendParticle.maxAge = MathHelper.nextBetween(clientWorld.random, 500, 1000);
            waterSuspendParticle.gravityStrength = 0.01F;
            waterSuspendParticle.setColor(0.655F, 0.8F, 0.1F);
            return waterSuspendParticle;
        }
    }
}

