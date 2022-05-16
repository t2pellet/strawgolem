package com.t2pellet.strawgolem.client.renderer.entity.model;

import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.IronGolem;

/**
 * Replacement for vanilla golem model
 */
public class ModelIronGolem<T extends IronGolem> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public ModelIronGolem(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T ironGolemEntity, float f, float g, float h, float yaw, float pitch) {
        this.head.yRot = yaw * 0.017453292F;
        this.head.xRot = pitch * 0.017453292F;
        this.rightLeg.xRot = -1.5F * Mth.triangleWave(f, 13.0F) * g;
        this.leftLeg.xRot = 1.5F * Mth.triangleWave(f, 13.0F) * g;
        this.rightLeg.yRot = 0.0F;
        this.leftLeg.yRot = 0.0F;
    }

    @Override
    public void prepareMobModel(T entity, float f, float g, float h) {
        int i = entity.getAttackAnimationTick();
        if (i > 0) {
            this.rightArm.xRot = -2.0F + 1.5F * Mth.triangleWave((float) i - h, 10.0F);
            this.leftArm.xRot = -2.0F + 1.5F * Mth.triangleWave((float) i - h, 10.0F);
        } else {
            int j = entity.getOfferFlowerTick();
            if (j > 0) {
                this.rightArm.xRot = -0.8F + 0.025F * Mth.triangleWave((float) j, 70.0F);
                this.leftArm.xRot = 0.0F;
            } else if (entity.hasPassenger(e -> e instanceof EntityStrawGolem) && entity.getPassengers().size() == 1) {
                leftArm.xRot = -0.45F * (float) Math.PI;
                rightArm.xRot = -0.45F * (float) Math.PI;
                leftArm.yRot = 0.18F;
                rightArm.yRot = -0.18F;
            } else {
                this.rightArm.xRot = (-0.2F + 1.5F * Mth.triangleWave(f, 13.0F)) * g;
                this.leftArm.xRot = (-0.2F - 1.5F * Mth.triangleWave(f, 13.0F)) * g;
            }
        }
    }

    public ModelPart getRightArm() {
        return this.rightArm;
    }
}
