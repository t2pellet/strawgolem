package com.commodorethrawn.strawgolem.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/**
 * StrawGolem - NivOridocs
 * Created using Tabula 7.0.0
 */
public class ModelStrawGolem extends EntityModel<EntityStrawGolem> {
	public ModelRenderer leftleg;
	public ModelRenderer rightleg;
	public ModelRenderer hip;
	public ModelRenderer body;
	public ModelRenderer leftarm;
	public ModelRenderer rightarm;
	public ModelRenderer head;

	public ModelStrawGolem() {
		this.textureWidth = 28;
		this.textureHeight = 19;
		this.leftarm = new ModelRenderer(this, 12, 8);
		this.leftarm.setRotationPoint(3.0F, 14.0F, 0.0F);
		this.leftarm.addBox(-0.5F, -1.25F, -1.5F, 1, 8, 3, 0.0F);
		this.leftleg = new ModelRenderer(this, 0, 8);
		this.leftleg.setRotationPoint(1.25F, 20.0F, 0.0F);
		this.leftleg.addBox(-1.5F, -1.0F, -1.5F, 3, 5, 3, 0.0F);
		this.body = new ModelRenderer(this, 0, 0);
		this.body.setRotationPoint(0.0F, 14.5F, 0.0F);
		this.body.addBox(-2.5F, -2.5F, -1.5F, 5, 5, 3, 0.0F);
		this.rightleg = new ModelRenderer(this, 0, 8);
		this.rightleg.mirror = true;
		this.rightleg.setRotationPoint(-1.25F, 20.0F, 0.0F);
		this.rightleg.addBox(-1.5F, -1.0F, -1.5F, 3, 5, 3, 0.0F);
		this.rightarm = new ModelRenderer(this, 12, 8);
		this.rightarm.mirror = true;
		this.rightarm.setRotationPoint(-3.0F, 14.0F, 0.0F);
		this.rightarm.addBox(-0.5F, -1.25F, -1.5F, 1, 8, 3, 0.0F);
		this.hip = new ModelRenderer(this, 18, 6);
		this.hip.setRotationPoint(0.0F, 18.0F, 0.1F);
		this.hip.addBox(-1.5F, -1.0F, -1.0F, 3, 2, 2, 0.0F);
		this.head = new ModelRenderer(this, 16, 0);
		this.head.setRotationPoint(0.0F, 12.0F, 0.0F);
		this.head.addBox(-1.5F, -3.0F, -2.0F, 3, 3, 3, 0.0F);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		matrixStackIn.push();
        matrixStackIn.translate(-0.23F, -0.9F, 0.0F);
		matrixStackIn.scale(1.0F, 1.0F, 0.5F);
        this.leftarm.translateRotate(matrixStackIn);
        this.leftarm.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		matrixStackIn.pop();
		matrixStackIn.push();
        matrixStackIn.translate(0.01F, -1.255F, 0.0F);
		matrixStackIn.scale(0.5F, 1.0F, 0.5F);
        this.leftleg.translateRotate(matrixStackIn);
		this.leftleg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		matrixStackIn.pop();
		this.body.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		matrixStackIn.push();
        matrixStackIn.translate(-0.01F, -1.255F, 0.0F);
		matrixStackIn.scale(0.5F, 1.0F, 0.5F);
        this.rightleg.translateRotate(matrixStackIn);
		this.rightleg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		matrixStackIn.pop();
		matrixStackIn.push();
        matrixStackIn.translate(0.23F, -0.9F, 0.0F);
        matrixStackIn.scale(1.0F, 1.0F, 0.5F);
        this.rightarm.translateRotate(matrixStackIn);
        this.rightarm.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		matrixStackIn.pop();
		this.hip.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.head.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public void setRotationAngles(EntityStrawGolem entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.rotateAngleY = netHeadYaw * 0.017453292F;
		this.head.rotateAngleX = headPitch * 0.017453292F;

		this.body.rotateAngleY = 0.0F;
		
		float auxLimbSwing = limbSwing * 5.0F * 0.6662F;

        this.rightarm.rotateAngleX = MathHelper.cos(auxLimbSwing + (float) Math.PI) * 0.8F * limbSwingAmount;
        this.leftarm.rotateAngleX = MathHelper.cos(auxLimbSwing) * 0.8F * limbSwingAmount;
		this.rightarm.rotateAngleZ = 0.0F;
		this.leftarm.rotateAngleZ = 0.0F;
        this.rightleg.rotateAngleX = MathHelper.cos(auxLimbSwing) * limbSwingAmount;
        this.leftleg.rotateAngleX = MathHelper.cos(auxLimbSwing + (float) Math.PI) * limbSwingAmount;
		this.rightleg.rotateAngleY = 0.0F;
		this.leftleg.rotateAngleY = 0.0F;
		this.rightleg.rotateAngleZ = 0.0F;
		this.leftleg.rotateAngleZ = 0.0F;

		this.rightarm.rotateAngleY = 0.0F;
		this.rightarm.rotateAngleZ = 0.0F;

		this.leftarm.rotateAngleY = 0.0F;

		this.rightarm.rotateAngleY = 0.0F;

		this.body.rotateAngleX = 0.0F;
		
		// Arms idle movement
        this.rightarm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.015F + 0.04F;
        this.leftarm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.015F + 0.04F;
        this.rightarm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.015F;
        this.leftarm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.015F;

	}
}
