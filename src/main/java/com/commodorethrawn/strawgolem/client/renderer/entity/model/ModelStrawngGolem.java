package com.commodorethrawn.strawgolem.client.renderer.entity.model;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.commodorethrawn.strawgolem.entity.EntityStrawngGolem;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class ModelStrawngGolem extends EntityModel<EntityStrawngGolem> {
	private final ModelPart Head;
	private final ModelPart ExtraBand;
	private final ModelPart RightArm;
	private final ModelPart LeftArm;
	private final ModelPart Body;
	private final ModelPart LeftLeg;
	private final ModelPart RightLeg;

	public ModelStrawngGolem() {
		textureWidth = 128;
		textureHeight = 128;

		Head = new ModelPart(this);
		Head.setPivot(0.0F, -19.0F, 0.0F);
		Head.setTextureOffset(0, 33).addCuboid(-6.0F, -21.0F, -6.0F, 12.0F, 3.0F, 12.0F, 0.0F, false);
		Head.setTextureOffset(0, 8).addCuboid(-6.0F, -18.0F, -6.0F, 12.0F, 12.0F, 12.0F, 0.0F, false);

		ExtraBand = new ModelPart(this);
		ExtraBand.setPivot(6.0F, -10.0F, 0.0F);
		Head.addChild(ExtraBand);
		ExtraBand.setTextureOffset(36, 17).addCuboid(0.0F, -8.0F, 0.0F, 4.0F, 3.0F, 0.0F, 0.0F, false);

		RightArm = new ModelPart(this);
		RightArm.setPivot(-12.5F, -19.0F, -0.5F);
		RightArm.setTextureOffset(0, 64).addCuboid(-11.0F, -6.0F, -5.5F, 11.0F, 29.0F, 11.0F, 0.0F, false);
		RightArm.setTextureOffset(0, 51).addCuboid(-11.0F, -8.0F, -5.5F, 11.0F, 2.0F, 11.0F, 0.0F, false);

		LeftArm = new ModelPart(this);
		LeftArm.setPivot(11.5F, -19.0F, 0.0F);
		LeftArm.setTextureOffset(0, 64).addCuboid(0.0F, -6.0F, -6.0F, 11.0F, 29.0F, 11.0F, 0.0F, true);
		LeftArm.setTextureOffset(0, 51).addCuboid(0.0F, -8.0F, -6.0F, 11.0F, 2.0F, 11.0F, 0.0F, true);

		Body = new ModelPart(this);
		Body.setPivot(-0.5F, -11.25F, -0.5F);
		Body.setTextureOffset(50, 18).addCuboid(-12.0F, -13.75F, -7.5F, 24.0F, 15.0F, 15.0F, 0.0F, false);
		Body.setTextureOffset(13, 107).addCuboid(-9.0F, 1.25F, -5.5F, 18.0F, 10.0F, 11.0F, 0.0F, false);

		LeftLeg = new ModelPart(this);
		LeftLeg.setPivot(-6.5F, 0.0F, 0.0F);
		LeftLeg.setTextureOffset(48, 72).addCuboid(-4.0F, 0.0F, -4.0F, 8.0F, 24.0F, 8.0F, 0.0F, false);

		RightLeg = new ModelPart(this);
		RightLeg.setPivot(5.5F, 0.0F, 0.0F);
		RightLeg.setTextureOffset(48, 72).addCuboid(-4.0F, 0.0F, -4.0F, 8.0F, 24.0F, 8.0F, 0.0F, true);
	}

	@Override
	public void setAngles(EntityStrawngGolem entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		setRotationAngle(ExtraBand, 0.0F, -0.2618F, 0.0F);
		this.Head.yaw = headYaw * ((float) Math.PI / 180F);
		this.Head.pitch = headPitch * ((float) Math.PI / 180F);
		this.LeftLeg.pitch = -1.5F * this.triangleWave(limbAngle, 13.0F) * limbDistance;
		this.RightLeg.pitch = 1.5F * this.triangleWave(limbAngle, 13.0F) * limbDistance;
		this.LeftLeg.yaw = 0.0F;
		this.RightLeg.yaw = 0.0F;
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		Head.render(matrixStack, buffer, packedLight, packedOverlay);
		RightArm.render(matrixStack, buffer, packedLight, packedOverlay);
		LeftArm.render(matrixStack, buffer, packedLight, packedOverlay);
		Body.render(matrixStack, buffer, packedLight, packedOverlay);
		LeftLeg.render(matrixStack, buffer, packedLight, packedOverlay);
		RightLeg.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	@Override
	public void animateModel(EntityStrawngGolem entity, float limbAngle, float limbDistance, float tickDelta) {
		int attackTicks = entity.getAttackTicks();
		if (attackTicks > 0) {
			LeftArm.pitch = - (float) Math.PI * (attackTicks - tickDelta) / 5;
			RightArm.pitch = - (float) Math.PI * (attackTicks - tickDelta) / 5;
		} else if (entity.getPassengerList().size() == 1 && entity.getPassengerList().get(0) instanceof EntityStrawGolem) {
			LeftArm.pitch = -0.45F * (float) Math.PI;
			RightArm.pitch = -0.45F * (float) Math.PI;
			LeftArm.yaw = 0.18F;
			RightArm.yaw = -0.18F;
		} else {
			this.RightArm.pitch = (-0.2F + 1.5F * MathHelper.method_24504(limbAngle, 13.0F)) * limbDistance;
			this.LeftArm.pitch = (-0.2F - 1.5F * MathHelper.method_24504(limbAngle, 13.0F)) * limbDistance;
		}
	}

	public void setRotationAngle(ModelPart part, float x, float y, float z) {
		part.pitch = x;
		part.yaw = y;
		part.roll = z;
	}

	private float triangleWave(float f1, float f2) {
		return (Math.abs(f1 % f2 - f2 * 0.5F) - f2 * 0.25F) / (f2 * 0.25F);
	}
}