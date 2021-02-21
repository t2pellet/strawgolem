package com.commodorethrawn.strawgolem.client.renderer.entity.model;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.math.MathHelper;

/**
 * Replacement for vanilla golem model
 */
public class ModelIronGolem<T extends IronGolemEntity> extends CompositeEntityModel<T> {
    private final ModelPart ironGolemHead;
    private final ModelPart ironGolemBody;
    private final ModelPart ironGolemRightArm;
    private final ModelPart ironGolemLeftArm;
    private final ModelPart ironGolemLeftLeg;
    private final ModelPart ironGolemRightLeg;

    public ModelIronGolem() {
        this.ironGolemHead = (new ModelPart(this)).setTextureSize(128, 128);
        this.ironGolemHead.setPivot(0.0F, -7.0F, -2.0F);
        this.ironGolemHead.setTextureOffset(0, 0).addCuboid(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, 0.0F);
        this.ironGolemHead.setTextureOffset(24, 0).addCuboid(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F, 0.0F);
        this.ironGolemBody = (new ModelPart(this)).setTextureSize(128, 128);
        this.ironGolemBody.setPivot(0.0F, -7.0F, 0.0F);
        this.ironGolemBody.setTextureOffset(0, 40).addCuboid(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F, 0.0F);
        this.ironGolemBody.setTextureOffset(0, 70).addCuboid(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, 0.5F);
        this.ironGolemRightArm = (new ModelPart(this)).setTextureSize(128, 128);
        this.ironGolemRightArm.setPivot(0.0F, -7.0F, 0.0F);
        this.ironGolemRightArm.setTextureOffset(60, 21).addCuboid(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, 0.0F);
        this.ironGolemLeftArm = (new ModelPart(this)).setTextureSize(128, 128);
        this.ironGolemLeftArm.setPivot(0.0F, -7.0F, 0.0F);
        this.ironGolemLeftArm.setTextureOffset(60, 58).addCuboid(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, 0.0F);
        this.ironGolemLeftLeg = (new ModelPart(this, 0, 22)).setTextureSize(128, 128);
        this.ironGolemLeftLeg.setPivot(-4.0F, 11.0F, 0.0F);
        this.ironGolemLeftLeg.setTextureOffset(37, 0).addCuboid(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, 0.0F);
        this.ironGolemRightLeg = (new ModelPart(this, 0, 22)).setTextureSize(128, 128);
        this.ironGolemRightLeg.mirror = true;
        this.ironGolemRightLeg.setTextureOffset(60, 0).setPivot(5.0F, 11.0F, 0.0F);
        this.ironGolemRightLeg.addCuboid(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, 0.0F);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.ironGolemHead.yaw = headYaw * ((float) Math.PI / 180F);
        this.ironGolemHead.pitch = headPitch * ((float) Math.PI / 180F);
        this.ironGolemLeftLeg.pitch = -1.5F * this.triangleWave(limbAngle, 13.0F) * limbDistance;
        this.ironGolemRightLeg.pitch = 1.5F * this.triangleWave(limbAngle, 13.0F) * limbDistance;
        this.ironGolemLeftLeg.yaw = 0.0F;
        this.ironGolemRightLeg.yaw = 0.0F;
    }



    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.ironGolemHead, this.ironGolemBody, this.ironGolemLeftLeg, this.ironGolemRightLeg, this.ironGolemRightArm, this.ironGolemLeftArm);
    }

    @Override
    public void animateModel(T entity, float f, float g, float h) {
        int i = entity.getAttackTicksLeft();
        if (i > 0) {
            this.ironGolemRightArm.pitch = -2.0F + 1.5F * MathHelper.method_24504((float)i - h, 10.0F);
            this.ironGolemLeftArm.pitch = -2.0F + 1.5F * MathHelper.method_24504((float)i - h, 10.0F);
        } else {
            int j = entity.getLookingAtVillagerTicks();
            if (j > 0) {
                this.ironGolemRightArm.pitch = -0.8F + 0.025F * MathHelper.method_24504((float)j, 70.0F);
                this.ironGolemLeftArm.pitch = 0.0F;
            } else if (entity.getPassengerList().size() == 1 && entity.getPassengerList().get(0) instanceof EntityStrawGolem) {
                ironGolemLeftArm.pitch = -0.45F * (float) Math.PI;
                ironGolemRightArm.pitch = -0.45F * (float) Math.PI;
                ironGolemLeftArm.yaw = 0.18F;
                ironGolemRightArm.yaw = -0.18F;
            } else {
                this.ironGolemRightArm.pitch = (-0.2F + 1.5F * MathHelper.method_24504(f, 13.0F)) * g;
                this.ironGolemLeftArm.pitch = (-0.2F - 1.5F * MathHelper.method_24504(f, 13.0F)) * g;
            }
        }
    }

    private float triangleWave(float f1, float f2) {
        return (Math.abs(f1 % f2 - f2 * 0.5F) - f2 * 0.25F) / (f2 * 0.25F);
    }

    public ModelPart getArmHoldingRose() {
        return this.ironGolemRightArm;
    }
}
