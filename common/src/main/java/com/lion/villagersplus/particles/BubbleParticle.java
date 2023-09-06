package com.lion.villagersplus.particles;

import com.lion.villagersplus.init.VPBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;

public class BubbleParticle extends SpriteBillboardParticle {

    BubbleParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f);
        this.gravityStrength = -0.125F;
        this.velocityMultiplier = 0.85F;
        this.setBoundingBoxSpacing(0.02F, 0.02F);
        this.scale *= this.random.nextFloat() * 0.6F;
        this.velocityX = g * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D;
        this.velocityY = h * 0.00500000298023224D;
        this.velocityZ = i * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D;
        this.maxAge = (int)(80.0D / (Math.random() * 0.8D + 0.2D));
    }

    public void tick() {
        super.tick();
        if (!this.dead && y - prevPosY > 0.0325) {
            this.markDead();
        }

        if (!this.dead && !this.world.getBlockState(new BlockPos(BlockPos.ofFloored(this.x, this.y, this.z))).isOf(VPBlocks.OCEANOGRAPHER_TABLE_BLOCK.get())) {
            this.markDead();
        }

    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            BubbleParticle bubble = new BubbleParticle(clientWorld, d, e, f, g, h, i);
            bubble.setSprite(this.spriteProvider);
            return bubble;
        }
    }
}
