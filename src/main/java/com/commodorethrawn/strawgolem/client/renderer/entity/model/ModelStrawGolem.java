package com.commodorethrawn.strawgolem.client.renderer.entity.model;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

// Made using Blockbench 3.5.3 by the talented Fr3nderman
// Exported for Minecraft version 1.15
public class ModelStrawGolem extends EntityModel<EntityStrawGolem> implements ModelWithArms {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightleg;
    private final ModelPart leftleg;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private boolean holdingBlock;
    private boolean holdingItem;
    private boolean isHungry;
    private boolean playerHasFood;

    public ModelStrawGolem() {
        holdingBlock = false;
        holdingItem = false;
        isHungry = false;
        textureWidth = 48;
        textureHeight = 48;

        head = new ModelPart(this);
        head.setPivot(0.0F, 11.0F, 0.0F);
        head.setTextureOffset(26, 24).addCuboid(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);
        head.setTextureOffset(11, 32).addCuboid(-2.0F, -5.0F, -2.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);

        body = new ModelPart(this);
        body.setPivot(0.0F, 24.0F, 0.0F);
        body.setTextureOffset(20, 32).addCuboid(-4.0F, -13.0F, -3.0F, 8.0F, 10.0F, 6.0F, 0.0F, false);

        rightleg = new ModelPart(this);
        rightleg.setPivot(-2.0F, 21.0F, 0.0F);
        rightleg.setTextureOffset(12, 43).addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);

        leftleg = new ModelPart(this);
        leftleg.setPivot(2.0F, 21.0F, 0.0F);
        leftleg.setTextureOffset(12, 43).addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);

        rightArm = new ModelPart(this);
        rightArm.setPivot(-5.0F, 12.0F, 0.0F);
        rightArm.setTextureOffset(4, 39).addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);

        leftArm = new ModelPart(this);
        leftArm.setPivot(5.0F, 12.0F, 0.0F);
        leftArm.setTextureOffset(4, 39).addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);
    }

    @Override
    public void setAngles(EntityStrawGolem entity, float limbAngle, float limbDistance, float tickDelta, float headYaw, float headPitch) {

        //Head rotation
        this.head.yaw = headYaw * 0.017453292F;
        this.head.pitch = headPitch * 0.017453292F;

        //Body is pretty static
        this.body.yaw = 0.0F;
        this.body.pitch = 0.0F;

        // Limbs
        float swingAmountArm = 1.7F * limbDistance;
        float swingAmountLeg = 2.5F * limbDistance;
        float auxLimbSwing = limbAngle * 3.331F;
        this.rightArm.pitch = MathHelper.cos(auxLimbSwing + (float) Math.PI) * swingAmountArm;
        this.leftArm.pitch = MathHelper.cos(auxLimbSwing) * swingAmountArm;
        this.rightleg.pitch = MathHelper.cos(auxLimbSwing) * swingAmountLeg;
        this.leftleg.pitch = MathHelper.cos(auxLimbSwing + (float) Math.PI) * swingAmountLeg;

        this.rightArm.yaw = 0.0F;
        this.rightArm.roll = 0.0F;
        this.leftArm.yaw = 0.0F;
        this.leftArm.roll = 0.0F;

        this.rightleg.yaw = 0.0F;
        this.leftleg.yaw = 0.0F;
        this.rightleg.roll = 0.0F;
        this.leftleg.roll = 0.0F;

        if (isHungry) {
            if (playerHasFood) greedyArms(tickDelta);
            else idleArms(tickDelta);
            this.leftleg.pitch = -(float) Math.PI / 2;
            this.leftleg.yaw = -(float) Math.PI / 8;
            this.rightleg.pitch = -(float) Math.PI / 2;
            this.rightleg.yaw = (float) Math.PI / 8;
        } else if (holdingBlock) {
            this.rightArm.pitch = (float) Math.PI;
            this.leftArm.pitch = (float) Math.PI;
        } else if (holdingItem) {
            this.rightArm.pitch = (float) -(0.29D * Math.PI);
            this.rightArm.yaw = (float) -(0.12D * Math.PI);
            this.rightArm.roll = (float) (0.08D * Math.PI);
            this.leftArm.pitch = (float) -(0.29D * Math.PI);
            this.leftArm.yaw = (float) (0.12D * Math.PI);
            this.leftArm.roll = (float) -(0.08D * Math.PI);
        } else if (playerHasFood) greedyArms(tickDelta);
        else idleArms(tickDelta);
    }

    /**
     * Greedy arms animation
     * @param tickDelta the animation progress
     */
    private void greedyArms(float tickDelta) {
        this.rightArm.pitch = - (float) Math.PI / 1.6F;
        this.rightArm.yaw = - (float) Math.PI / 12 + MathHelper.cos(tickDelta * 1.1F) * 0.075F;
        this.leftArm.pitch = - (float) Math.PI / 1.6F;
        this.leftArm.yaw = (float) Math.PI / 12 - MathHelper.cos(tickDelta * 1.1F) * 0.075F;
    }

    /**
     * Idle arm swinging
     * @param animationProgress the animation progress
     */
    private void idleArms(float animationProgress) {
        this.rightArm.roll += MathHelper.cos(animationProgress * 0.09F) * 0.06F + 0.06F;
        this.leftArm.roll -= MathHelper.cos(animationProgress * 0.09F) * 0.06F + 0.06F;
        this.rightArm.pitch += MathHelper.sin(animationProgress * 0.067F) * 0.06F;
        this.leftArm.pitch -= MathHelper.sin(animationProgress * 0.067F) * 0.06F;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        body.render(matrices, vertices, light, overlay);
        head.render(matrices, vertices, light, overlay);

        rightArm.render(matrices, vertices, light, overlay);
        leftArm.render(matrices, vertices, light, overlay);

        if (isHungry) matrices.translate(0.0F, -0.06F, -0.18F);
        rightleg.render(matrices, vertices, light, overlay);
        leftleg.render(matrices, vertices, light, overlay);

    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        if (holdingBlock) {
            matrices.translate(0.035F, -0.75F, 0.58F);
            matrices.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(15.0F));
            matrices.scale(1.5F, 1.5F, 1.5F);
        } else {
            matrices.translate(0.05F, 1.3F, 0.23F);
            matrices.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(90.0F));
        }
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

    public void setPlayerHasFood(boolean playerHasFood) {
        this.playerHasFood = playerHasFood;
    }

}