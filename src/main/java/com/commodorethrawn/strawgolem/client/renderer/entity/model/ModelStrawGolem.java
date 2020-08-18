package com.commodorethrawn.strawgolem.client.renderer.entity.model;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

// Made using Blockbench 3.5.3 by the talented Fr3nderman
// Exported for Minecraft version 1.15
public class ModelStrawGolem extends EntityModel<EntityStrawGolem> implements IHasArm {
    private final ModelRenderer Head;
    private final ModelRenderer Body;
    private final ModelRenderer rightleg;
    private final ModelRenderer leftleg;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;

    public boolean holdingItem;
    public boolean holdingBlock;

    public ModelStrawGolem() {
        holdingItem = false;
        holdingBlock = false;
        textureWidth = 48;
        textureHeight = 48;

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 11.0F, 0.0F);
        Head.setTextureOffset(26, 24).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);
        Head.setTextureOffset(11, 32).addBox(-2.0F, -5.0F, -2.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);

        Body = new ModelRenderer(this);
        Body.setRotationPoint(0.0F, 24.0F, 0.0F);
        Body.setTextureOffset(20, 32).addBox(-4.0F, -13.0F, -3.0F, 8.0F, 10.0F, 6.0F, 0.0F, false);

        rightleg = new ModelRenderer(this);
        rightleg.setRotationPoint(-2.0F, 21.0F, 0.0F);
        rightleg.setTextureOffset(12, 43).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);

        leftleg = new ModelRenderer(this);
        leftleg.setRotationPoint(2.0F, 21.0F, 0.0F);
        leftleg.setTextureOffset(12, 43).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);

        rightArm = new ModelRenderer(this);
        rightArm.setRotationPoint(-5.0F, 12.0F, 0.0F);
        rightArm.setTextureOffset(4, 39).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);

        leftArm = new ModelRenderer(this);
        leftArm.setRotationPoint(5.0F, 12.0F, 0.0F);
        leftArm.setTextureOffset(4, 39).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);
    }

    @Override
    public void setRotationAngles(EntityStrawGolem entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.Head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.Head.rotateAngleX = headPitch * 0.017453292F;

        this.Body.rotateAngleY = 0.0F;

        float auxLimbSwing = limbSwing * 5.0F * 0.6662F;

        float swingAmountArm = 1.7F * limbSwingAmount;
        float swingAmoungLeg = 2.5F * limbSwingAmount;

        this.rightArm.rotateAngleX = MathHelper.cos(auxLimbSwing + (float) Math.PI) * swingAmountArm;
        this.leftArm.rotateAngleX = MathHelper.cos(auxLimbSwing) * swingAmountArm;
        this.rightArm.rotateAngleZ = 0.0F;
        this.leftArm.rotateAngleZ = 0.0F;
        this.rightleg.rotateAngleX = MathHelper.cos(auxLimbSwing) * swingAmoungLeg;
        this.leftleg.rotateAngleX = MathHelper.cos(auxLimbSwing + (float) Math.PI) * swingAmoungLeg;
        this.rightleg.rotateAngleY = 0.0F;
        this.leftleg.rotateAngleY = 0.0F;
        this.rightleg.rotateAngleZ = 0.0F;
        this.leftleg.rotateAngleZ = 0.0F;

        this.rightArm.rotateAngleY = 0.0F;
        this.rightArm.rotateAngleZ = 0.0F;

        this.leftArm.rotateAngleY = 0.0F;

        this.rightArm.rotateAngleY = 0.0F;

        this.Body.rotateAngleX = 0.0F;

        // Arms idle movement
        if (holdingBlock) {
            this.rightArm.rotateAngleX = (float) Math.PI;
            this.leftArm.rotateAngleX = (float) Math.PI;
        } else if (holdingItem) {
            this.rightArm.rotateAngleX = (float) -(0.29D * Math.PI);
            this.rightArm.rotateAngleY = (float) -(0.12D * Math.PI);
            this.rightArm.rotateAngleZ = (float) (0.08D * Math.PI);
            this.leftArm.rotateAngleX = (float) -(0.29D * Math.PI);
            this.leftArm.rotateAngleY = (float) (0.12D * Math.PI);
            this.leftArm.rotateAngleZ = (float) -(0.08D * Math.PI);
        } else {
            this.rightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.06F + 0.06F;
            this.leftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.06F + 0.06F;
            this.rightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.06F;
            this.leftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.06F;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Head.render(matrixStack, buffer, packedLight, packedOverlay);
        Body.render(matrixStack, buffer, packedLight, packedOverlay);
        rightleg.render(matrixStack, buffer, packedLight, packedOverlay);
        leftleg.render(matrixStack, buffer, packedLight, packedOverlay);
        rightArm.render(matrixStack, buffer, packedLight, packedOverlay);
        leftArm.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public void translateHand(HandSide sideIn, MatrixStack matrixStackIn) {
        if (holdingBlock) {
            matrixStackIn.translate(0.075F, -0.75F, 0.585F);
            matrixStackIn.rotate(Vector3f.XN.rotationDegrees(15.0F));
            matrixStackIn.scale(1.5F, 1.5F, 1.5F);
        } else {
            matrixStackIn.translate(0.05F, 1.3F, 0.23F);
            matrixStackIn.rotate(Vector3f.XN.rotationDegrees(90.0F));
        }
    }
}
