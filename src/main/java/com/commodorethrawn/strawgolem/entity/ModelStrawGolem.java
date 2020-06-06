package com.commodorethrawn.strawgolem.entity;

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
	private final ModelRenderer Leftleg;
	private final ModelRenderer Rightarm;
	private final ModelRenderer Leftarm;

    public boolean holdingItem;

	public ModelStrawGolem() {
        holdingItem = false;
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

		Leftleg = new ModelRenderer(this);
		Leftleg.setRotationPoint(2.0F, 21.0F, 0.0F);
		Leftleg.setTextureOffset(12, 43).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);

		Rightarm = new ModelRenderer(this);
		Rightarm.setRotationPoint(-5.0F, 12.0F, 0.0F);
		Rightarm.setTextureOffset(4, 39).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);

		Leftarm = new ModelRenderer(this);
		Leftarm.setRotationPoint(5.0F, 12.0F, 0.0F);
		Leftarm.setTextureOffset(4, 39).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(EntityStrawGolem entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.Head.rotateAngleY = netHeadYaw * 0.017453292F;
		this.Head.rotateAngleX = headPitch * 0.017453292F;

		this.Body.rotateAngleY = 0.0F;

		float auxLimbSwing = limbSwing * 5.0F * 0.6662F;

		float swingAmountArm = 1.7F * limbSwingAmount;
		float swingAmoungLeg = 2.5F * limbSwingAmount;

		this.Rightarm.rotateAngleX = MathHelper.cos(auxLimbSwing + (float) Math.PI) * swingAmountArm;
		this.Leftarm.rotateAngleX = MathHelper.cos(auxLimbSwing) * swingAmountArm;
		this.Rightarm.rotateAngleZ = 0.0F;
		this.Leftarm.rotateAngleZ = 0.0F;
		this.rightleg.rotateAngleX = MathHelper.cos(auxLimbSwing) * swingAmoungLeg;
		this.Leftleg.rotateAngleX = MathHelper.cos(auxLimbSwing + (float) Math.PI) * swingAmoungLeg;
		this.rightleg.rotateAngleY = 0.0F;
		this.Leftleg.rotateAngleY = 0.0F;
		this.rightleg.rotateAngleZ = 0.0F;
		this.Leftleg.rotateAngleZ = 0.0F;

		this.Rightarm.rotateAngleY = 0.0F;
		this.Rightarm.rotateAngleZ = 0.0F;

		this.Leftarm.rotateAngleY = 0.0F;

		this.Rightarm.rotateAngleY = 0.0F;

		this.Body.rotateAngleX = 0.0F;

		// Arms idle movement
        if (!holdingItem) {
            this.Rightarm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.06F + 0.06F;
            this.Leftarm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.06F + 0.06F;
            this.Rightarm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.06F;
            this.Leftarm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.06F;
        } else {
            this.Rightarm.rotateAngleX = (float) -(0.29D * Math.PI);
            this.Rightarm.rotateAngleY = (float) -(0.12D * Math.PI);
            this.Rightarm.rotateAngleZ = (float) (0.08D * Math.PI);
            this.Leftarm.rotateAngleX = (float) -(0.29D * Math.PI);
            this.Leftarm.rotateAngleY = (float) (0.12D * Math.PI);
            this.Leftarm.rotateAngleZ = (float) -(0.08D * Math.PI);
        }
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Head.render(matrixStack, buffer, packedLight, packedOverlay);
		Body.render(matrixStack, buffer, packedLight, packedOverlay);
		rightleg.render(matrixStack, buffer, packedLight, packedOverlay);
		Leftleg.render(matrixStack, buffer, packedLight, packedOverlay);
		Rightarm.render(matrixStack, buffer, packedLight, packedOverlay);
		Leftarm.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	@Override
	public void translateHand(HandSide sideIn, MatrixStack matrixStackIn) {
        matrixStackIn.translate(0.05F, 1.3F, 0.2F);
        matrixStackIn.rotate(Vector3f.XN.rotationDegrees(90.0F));
	}
}
