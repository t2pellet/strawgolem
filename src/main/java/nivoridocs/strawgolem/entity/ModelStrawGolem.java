package nivoridocs.strawgolem.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

/**
 * StrawGolem - NivOridocs
 * Created using Tabula 7.0.0
 */
public class ModelStrawGolem extends ModelBase {
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
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.leftarm.offsetX, this.leftarm.offsetY, this.leftarm.offsetZ);
		GlStateManager.translate(this.leftarm.rotationPointX * f5, this.leftarm.rotationPointY * f5, this.leftarm.rotationPointZ * f5);
		GlStateManager.scale(1.0D, 1.0D, 0.5D);
		GlStateManager.translate(-this.leftarm.offsetX, -this.leftarm.offsetY, -this.leftarm.offsetZ);
		GlStateManager.translate(-this.leftarm.rotationPointX * f5, -this.leftarm.rotationPointY * f5, -this.leftarm.rotationPointZ * f5);
		this.leftarm.render(f5);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.leftleg.offsetX, this.leftleg.offsetY, this.leftleg.offsetZ);
		GlStateManager.translate(this.leftleg.rotationPointX * f5, this.leftleg.rotationPointY * f5, this.leftleg.rotationPointZ * f5);
		GlStateManager.scale(0.5D, 1.0D, 0.5D);
		GlStateManager.translate(-this.leftleg.offsetX, -this.leftleg.offsetY, -this.leftleg.offsetZ);
		GlStateManager.translate(-this.leftleg.rotationPointX * f5, -this.leftleg.rotationPointY * f5, -this.leftleg.rotationPointZ * f5);
		this.leftleg.render(f5);
		GlStateManager.popMatrix();
		this.body.render(f5);
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.rightleg.offsetX, this.rightleg.offsetY, this.rightleg.offsetZ);
		GlStateManager.translate(this.rightleg.rotationPointX * f5, this.rightleg.rotationPointY * f5, this.rightleg.rotationPointZ * f5);
		GlStateManager.scale(0.5D, 1.0D, 0.5D);
		GlStateManager.translate(-this.rightleg.offsetX, -this.rightleg.offsetY, -this.rightleg.offsetZ);
		GlStateManager.translate(-this.rightleg.rotationPointX * f5, -this.rightleg.rotationPointY * f5, -this.rightleg.rotationPointZ * f5);
		this.rightleg.render(f5);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.rightarm.offsetX, this.rightarm.offsetY, this.rightarm.offsetZ);
		GlStateManager.translate(this.rightarm.rotationPointX * f5, this.rightarm.rotationPointY * f5, this.rightarm.rotationPointZ * f5);
		GlStateManager.scale(1.0D, 1.0D, 0.5D);
		GlStateManager.translate(-this.rightarm.offsetX, -this.rightarm.offsetY, -this.rightarm.offsetZ);
		GlStateManager.translate(-this.rightarm.rotationPointX * f5, -this.rightarm.rotationPointY * f5, -this.rightarm.rotationPointZ * f5);
		this.rightarm.render(f5);
		GlStateManager.popMatrix();
		this.hip.render(f5);
		this.head.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		this.head.rotateAngleY = netHeadYaw * 0.017453292F;
		this.head.rotateAngleX = headPitch * 0.017453292F;

		this.body.rotateAngleY = 0.0F;
		
		float auxLimbSwing = limbSwing * 5.0F * 0.6662F;
		float armLimbSwingAmount = 2.0F * limbSwingAmount;
		float legLimbSwingAmount = 2.8F * limbSwingAmount;
		
		this.rightarm.rotateAngleX = MathHelper.cos(auxLimbSwing + (float) Math.PI) * armLimbSwingAmount;
		this.leftarm.rotateAngleX = MathHelper.cos(auxLimbSwing) * armLimbSwingAmount;
		this.rightarm.rotateAngleZ = 0.0F;
		this.leftarm.rotateAngleZ = 0.0F;
		this.rightleg.rotateAngleX = MathHelper.cos(auxLimbSwing) * legLimbSwingAmount;
		this.leftleg.rotateAngleX = MathHelper.cos(auxLimbSwing + (float) Math.PI) * legLimbSwingAmount;
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
		this.rightarm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		this.leftarm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		this.rightarm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		this.leftarm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;

	}
}
