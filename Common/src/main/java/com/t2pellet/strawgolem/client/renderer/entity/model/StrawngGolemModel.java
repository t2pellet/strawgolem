package com.t2pellet.strawgolem.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.StrawngGolem;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class StrawngGolemModel extends ListModel<StrawngGolem> {

    private final ModelPart head;
    private final ModelPart headBand;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public StrawngGolemModel() {
        texWidth = 128;
        texHeight = 128;
        head = new ModelPart(this)
                .texOffs(0, 33).addBox(-6.0F, -21.0F, -6.0F, 12.0F, 3.0F, 12.0F)
                .texOffs(0, 8).addBox(-6.0F, -18.0F, -6.0F, 12.0F, 12.0F, 12.0F);
        head.setPos(0.0F, -19.0F, 0.0F);

        headBand = new ModelPart(this)
                .texOffs(36, 17).addBox(0.0F, -8.0F, 0.0F, 4.0F, 3.0F, 0.0F);
        headBand.setPos(-0.5F, -11.25F, -0.5F);
        body = new ModelPart(this)
                .texOffs(50, 18).addBox(-12.0F, -13.75F, -7.5F, 24.0F, 15.0F, 15.0F)
                .texOffs(13, 107).addBox(-9.0F, 1.25F, -5.5F, 18.0F, 10.0F, 11.0F);
        body.setPos(-0.5F, -11.25F, -0.5F);
        leftArm = new ModelPart(this)
                .texOffs(0, 64).addBox(0.0F, -6.0F, -6.0F, 11.0F, 29.0F, 11.0F)
                .texOffs(0, 51).addBox(0.0F, -8.0F, -6.0F, 11.0F, 2.0F, 11.0F);
        leftArm.mirror = true;
        leftArm.setPos(11.5F, -19.0F, 0.0F);
        rightArm = new ModelPart(this)
                .texOffs(0, 64).addBox(-11.0F, -6.0F, -5.5F, 11.0F, 29.0F, 11.0F)
                .texOffs(0, 51).addBox(-11.0F, -8.0F, -5.5F, 11.0F, 2.0F, 11.0F);
        rightArm.setPos(-12.5F, -19.0F, -0.5F);
        leftLeg = new ModelPart(this).texOffs(48, 72).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 24.0F, 8.0F);
        leftLeg.setPos(-6.5F, 0.0F, 0.0F);
        rightLeg = new ModelPart(this).texOffs(48, 72).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 24.0F, 8.0F);
        rightLeg.mirror = true;
        rightLeg.setPos(5.5F, 0.0F, 0.0F);
    }

    @Override
    public void setupAnim(StrawngGolem entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        setRotationAngle(headBand, 0.0F, -0.2618F, 0.0F);
        this.head.yRot = headYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        this.leftLeg.xRot = -1.5F * this.triangleWave(limbAngle) * limbDistance;
        this.rightLeg.xRot = 1.5F * this.triangleWave(limbAngle) * limbDistance;
        this.leftLeg.yRot = 0.0F;
        this.rightLeg.yRot = 0.0F;
    }

    @Override
    public void prepareMobModel(StrawngGolem entity, float limbAngle, float limbDistance, float tickDelta) {
        int attackTicks = entity.getAttackTicks();
        if (attackTicks > 0) {
            leftArm.xRot = -(float) Math.PI * (attackTicks - tickDelta) / 5;
            rightArm.xRot = -(float) Math.PI * (attackTicks - tickDelta) / 5;
        } else if (entity.hasPassenger(StrawGolem.class) && entity.getPassengers().size() == 1) {
            leftArm.xRot = -0.45F * (float) Math.PI;
            rightArm.xRot = -0.45F * (float) Math.PI;
            leftArm.yRot = 0.34F;
            rightArm.yRot = -0.34F;
        } else {
            this.rightArm.xRot = (-0.2F + 1.5F * Mth.triangleWave(limbAngle, 13.0F)) * limbDistance;
            this.leftArm.xRot = (-0.2F - 1.5F * Mth.triangleWave(limbAngle, 13.0F)) * limbDistance;
        }
    }

    public void setRotationAngle(ModelPart part, float x, float y, float z) {
        part.xRot = x;
        part.yRot = y;
        part.zRot = z;
    }

    private float triangleWave(float f1) {
        return (Math.abs(f1 % (float) 13.0 - (float) 13.0 * 0.5F) - (float) 13.0 * 0.25F) / ((float) 13.0 * 0.25F);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(this.head, this.headBand, this.body, this.leftArm, this.rightArm, this.leftLeg, this.rightLeg);
    }
}