package com.t2pellet.strawgolem.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

// TODO : Arms are slightly rotated for some reason

// Originally made by the talented Fr3nderman
public class ModelStrawGolem extends EntityModel<EntityStrawGolem> implements ArmedModel {

    private final ModelPart hat;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightleg;
    private final ModelPart leftleg;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private boolean holdingBlock;
    private boolean holdingItem;
    private boolean isHungry;
    private boolean tempted;

    public ModelStrawGolem(ModelPart root) {
        hat = root.getChild("hat");
        head = root.getChild("head");
        body = root.getChild("body");
        rightleg = root.getChild("rightLeg");
        leftleg = root.getChild("leftLeg");
        rightArm = root.getChild("rightArm");
        leftArm = root.getChild("leftArm");

        holdingBlock = false;
        holdingItem = false;
        isHungry = false;
        tempted = false;
    }

    public static LayerDefinition createModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        modelPartData.addOrReplaceChild("hat",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-5.0F, 0.0F, -5.0F, 10.0F, 0.0F, 10.0F)
                        .texOffs(0, 10).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 2.0F, 6.0F),
                PartPose.offset(0.0F, 8.5F, 0.0F));
        modelPartData.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(26, 24).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F)
                        .texOffs(11, 32).addBox(-2.0F, -5.0F, -2.0F, 4.0F, 1.0F, 4.0F),
                PartPose.offset(0.0F, 11.0F, 0.0F));
        modelPartData.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(20, 32).addBox(-4.0F, -13.0F, -3.0F, 8.0F, 10.0F, 6.0F),
                PartPose.offset(0.0F, 24.0F, 0.0F));
        CubeListBuilder leg = CubeListBuilder.create().texOffs(12, 43).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F);
        modelPartData.addOrReplaceChild("rightLeg", leg, PartPose.offset(-2.0F, 21.0F, 0.0F));
        modelPartData.addOrReplaceChild("leftLeg", leg, PartPose.offset(2.0F, 21.0F, 0.0F));
        CubeListBuilder arm = CubeListBuilder.create().texOffs(4, 39).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F);
        modelPartData.addOrReplaceChild("rightArm", arm, PartPose.offset(-5.0F, 12.0F, 0.0F));
        modelPartData.addOrReplaceChild("leftArm", arm, PartPose.offset(5.0F, 12.0F, 0.0F));

        return LayerDefinition.create(modelData, 48, 48);
    }

    @Override
    public void setupAnim(EntityStrawGolem entity, float limbAngle, float limbDistance, float tickDelta, float headYaw, float headPitch) {
        //Head rotation
        this.head.yRot = headYaw * 0.017453292F;
        this.hat.yRot = headYaw * 0.017453292F;
        this.head.xRot = headPitch * 0.017453292F;
        this.hat.xRot = headPitch * 0.017453292F;
        // Movement
        float swingAmountArm = 1.7F * limbDistance;
        float swingAmountLeg = 2.4F * limbDistance;
        float auxLimbSwing = limbAngle * 3.331F;
        this.rightArm.xRot = Mth.cos(auxLimbSwing + (float) Math.PI) * swingAmountArm;
        this.leftArm.xRot = Mth.cos(auxLimbSwing) * swingAmountArm;
        this.rightleg.xRot = Mth.cos(auxLimbSwing) * swingAmountLeg;
        this.leftleg.xRot = Mth.cos(auxLimbSwing + (float) Math.PI) * swingAmountLeg;
        // Animations
        if (isHungry) {
            if (tempted) greedyArms(tickDelta);
            else idleArms(tickDelta);
            this.leftleg.xRot = -(float) Math.PI / 2;
            this.leftleg.yRot = -(float) Math.PI / 8;
            this.rightleg.xRot = -(float) Math.PI / 2;
            this.rightleg.yRot = (float) Math.PI / 8;
        } else if (holdingBlock) {
            this.rightArm.xRot = (float) Math.PI;
            this.leftArm.xRot = (float) Math.PI;
        } else if (holdingItem) {
            this.rightArm.xRot = (float) -(0.29D * Math.PI);
            this.rightArm.yRot = (float) -(0.12D * Math.PI);
            this.rightArm.zRot = (float) (0.08D * Math.PI);
            this.leftArm.xRot = (float) -(0.29D * Math.PI);
            this.leftArm.yRot = (float) (0.12D * Math.PI);
            this.leftArm.zRot = (float) -(0.08D * Math.PI);
        } else if (tempted) greedyArms(tickDelta);
        else idleArms(tickDelta);
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        hat.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        head.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        body.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        leftArm.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        rightArm.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        // Fix sitting legs position
        if (isHungry) matrices.translate(0.0F, -0.06F, -0.18F);
        leftleg.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        rightleg.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack matrices) {
        if (holdingBlock) {
            matrices.translate(0.035F, -0.75F, 0.58F);
            matrices.mulPose(Vector3f.XN.rotationDegrees(15.0F));
            matrices.scale(1.5F, 1.5F, 1.5F);
        } else {
            matrices.translate(0.05F, 1.3F, 0.23F);
            matrices.mulPose(Vector3f.XN.rotationDegrees(90.0F));
        }
    }

    public void setHatVisible(boolean hatVisible) {
        this.hat.visible = hatVisible;
    }

    public void setHoldingBlock(boolean holdingBlock) {
        this.holdingBlock = holdingBlock;
    }

    public void setHoldingItem(boolean holdingItem) {
        this.holdingItem = holdingItem;
    }

    public void setHungry(boolean isHungry) {
        this.isHungry = isHungry;
    }

    public void setTempted(boolean tempted) {
        this.tempted = tempted;
    }

    /**
     * Greedy arms animation
     *
     * @param tickDelta the animation progress
     */
    private void greedyArms(float tickDelta) {
        this.rightArm.xRot = -(float) Math.PI / 1.6F;
        this.rightArm.yRot = -(float) Math.PI / 12 + Mth.cos(tickDelta * 1.1F) * 0.075F;
        this.leftArm.xRot = -(float) Math.PI / 1.6F;
        this.leftArm.yRot = (float) Math.PI / 12 - Mth.cos(tickDelta * 1.1F) * 0.075F;
    }

    /**
     * Idle arm swinging
     *
     * @param animationProgress the animation progress
     */
    private void idleArms(float animationProgress) {
        float roll = Mth.cos(animationProgress * 0.09F) * 0.06F + 0.06F;
        float pitch = Mth.sin(animationProgress * 0.067F) * 0.06F;
        this.rightArm.zRot = roll;
        this.rightArm.xRot += pitch;
        this.leftArm.zRot = -roll;
        this.leftArm.xRot -= pitch;
    }

}