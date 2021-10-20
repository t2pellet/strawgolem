package com.commodorethrawn.strawgolem.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class FlyParticle extends SpriteBillboardParticle {

    private float thetaHoriz;
    private float thetaVert;

    protected FlyParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                          double xSpeed, double ySpeed, double zSpeed) {
        super(worldIn, xCoordIn, yCoordIn + 0.4F, zCoordIn);
        x += random.nextFloat() - 0.5F;
        z += random.nextFloat() - 0.5F;
        setBoundingBoxSpacing(0.02F, 0.02F);
        maxAge = 60;
        thetaHoriz = random.nextFloat() * (float) Math.PI;
        thetaVert = 0;
    }

    @Override
    public void tick() {
        super.tick();
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.move(this.velocityX, this.velocityY, this.velocityZ);
            thetaHoriz += 0.05;
            thetaVert += 0.05;
            this.velocityX = Math.sin(thetaHoriz) / 14;
            this.velocityZ =  Math.cos(thetaHoriz) / 14;
            this.velocityY = (Math.sin(2 * thetaVert)) / 12 + 0.025;
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }


    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteSet;

        public Factory(SpriteProvider sprite) {
            spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            FlyParticle flyParticle = new FlyParticle(world, x, y, z, velocityX, velocityY, velocityZ);
            flyParticle.setColor(1.0F, 1.0F, 1.0F);
            flyParticle.setSprite(spriteSet);
            return flyParticle;
        }
    }
}
