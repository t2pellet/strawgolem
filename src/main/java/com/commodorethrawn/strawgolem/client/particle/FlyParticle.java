package com.commodorethrawn.strawgolem.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class FlyParticle extends SpriteTexturedParticle {

    private float thetaHoriz;
    private float thetaVert;

    protected FlyParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                          double xSpeed, double ySpeed, double zSpeed) {
        super(worldIn, xCoordIn, yCoordIn + 0.4F, zCoordIn);
        posX += rand.nextFloat() - 0.5F;
        posZ += rand.nextFloat() - 0.5F;
        setSize(0.02F, 0.02F);
        maxAge = 60;
        thetaHoriz = rand.nextFloat() * (float) Math.PI;
        thetaVert = 0;
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.move(this.motionX, this.motionY, this.motionZ);
            thetaHoriz += 0.05;
            thetaVert += 0.05;
            this.motionX = Math.sin(thetaHoriz) / 14;
            this.motionZ =  Math.cos(thetaHoriz) / 14;
            this.motionY = (Math.sin(2 * thetaVert)) / 12 + 0.025;
        }
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite sprite) {
            spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            FlyParticle flyParticle = new FlyParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            flyParticle.setColor(1.0F, 1.0F, 1.0F);
            flyParticle.selectSpriteRandomly(spriteSet);
            return flyParticle;
        }

    }
}
