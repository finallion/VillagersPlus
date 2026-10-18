package com.lion.villagersplus.particles;

import com.lion.villagersplus.init.VPBlocks;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class BubbleParticle extends SingleQuadParticle {

    BubbleParticle(ClientLevel clientWorld, double d, double e, double f, double g, double h, double i, TextureAtlasSprite sprite) {
        super(clientWorld, d, e, f, sprite);
        this.gravity = -0.125F;
        this.friction = 0.85F;
        this.setSize(0.02F, 0.02F);
        this.quadSize *= this.random.nextFloat() * 0.6F;
        this.xd = g * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D;
        this.yd = h * 0.00500000298023224D;
        this.zd = i * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D;
        this.lifetime = (int)(80.0D / (Math.random() * 0.8D + 0.2D));
    }

    public void tick() {
        super.tick();
        if (!this.removed && y - this.yo > 0.0325) {
            this.remove();
        }

        if (!this.removed && !this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).is(VPBlocks.OCEANOGRAPHER_TABLE_BLOCK.get())) {
            this.remove();
        }

    }

    protected Layer getLayer() {
        return Layer.bySprite(this.sprite);
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i, RandomSource random) {
            BubbleParticle bubble = new BubbleParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider.get(random));
            return bubble;
        }
    }
}
