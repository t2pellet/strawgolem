package com.t2pellet.strawgolem.client.renderer.entity.model;

import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import com.t2pellet.strawgolem.entity.EntityStrawngGolem;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class ModelStrawngGolem extends HierarchicalModel<EntityStrawngGolem> {

	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart headBand;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;

	public ModelStrawngGolem(ModelPart root) {
		this.root = root;
		head = root.getChild("head");
		headBand = head.getChild("headBand");
		leftArm = root.getChild("leftArm");
		rightArm = root.getChild("rightArm");
		leftLeg = root.getChild("leftLeg");
		rightLeg = root.getChild("rightLeg");
	}

	public static LayerDefinition createModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();

		modelPartData.addOrReplaceChild("head",
						CubeListBuilder.create().texOffs(0, 33).addBox(-6.0F, -21.0F, -6.0F, 12.0F, 3.0F, 12.0F).texOffs(0, 8).addBox("accent", -6.0F, -18.0F, -6.0F, 12.0F, 12.0F, 12.0F),
						PartPose.offset(0.0F, -19.0F, 0.0F))
			.addOrReplaceChild("headBand",
					CubeListBuilder.create().texOffs(36, 17).addBox(0.0F, -8.0F, 0.0F, 4.0F, 3.0F, 0.0F),
					PartPose.offset(6.0F, -10.0F, 0.0F));
		modelPartData.addOrReplaceChild("body",
				CubeListBuilder.create()
						.texOffs(50,18).addBox(-12.0F, -13.75F, -7.5F, 24.0F, 15.0F, 15.0F)
						.texOffs(13, 107).addBox(-9.0F, 1.25F, -5.5F, 18.0F, 10.0F, 11.0F),
				PartPose.offset(-0.5F, -11.25F, -0.5F));
		modelPartData.addOrReplaceChild("leftArm",
				CubeListBuilder.create()
					.texOffs(0, 64).addBox(0.0F, -6.0F, -6.0F, 11.0F, 29.0F, 11.0F).mirror()
					.texOffs(0, 51).addBox(0.0F, -8.0F, -6.0F, 11.0F, 2.0F, 11.0F).mirror(),
				PartPose.offset(11.5F, -19.0F, 0.0F));
		modelPartData.addOrReplaceChild("rightArm",
				CubeListBuilder.create()
					.texOffs(0, 64).addBox(-11.0F, -6.0F, -5.5F, 11.0F, 29.0F, 11.0F)
					.texOffs(0, 51).addBox(-11.0F, -8.0F, -5.5F, 11.0F, 2.0F, 11.0F),
				PartPose.offset(-12.5F, -19.0F, -0.5F));
		CubeListBuilder leg = CubeListBuilder.create().texOffs(48, 72).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 24.0F, 8.0F);
		modelPartData.addOrReplaceChild("leftLeg", leg, PartPose.offset(-6.5F, 0.0F, 0.0F));
		modelPartData.addOrReplaceChild("rightLeg", leg.mirror(), PartPose.offset(5.5F, 0.0F, 0.0F));

		return LayerDefinition.create(modelData, 128, 128);
	}

	@Override
	public  ModelPart root() {
		return root;
	}

	@Override
	public void setupAnim( EntityStrawngGolem entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
		setRotationAngle(headBand, 0.0F, -0.2618F, 0.0F);
		this.head.yRot = headYaw * ((float) Math.PI / 180F);
		this.head.xRot = headPitch * ((float) Math.PI / 180F);
		this.leftLeg.xRot = -1.5F * this.triangleWave(limbAngle) * limbDistance;
		this.rightLeg.xRot = 1.5F * this.triangleWave(limbAngle) * limbDistance;
		this.leftLeg.yRot = 0.0F;
		this.rightLeg.yRot = 0.0F;
	}

	@Override
	public void prepareMobModel(EntityStrawngGolem entity, float limbAngle, float limbDistance, float tickDelta) {
		int attackTicks = entity.getAttackTicks();
		if (attackTicks > 0) {
			leftArm.xRot = - (float) Math.PI * (attackTicks - tickDelta) / 5;
			rightArm.xRot = - (float) Math.PI * (attackTicks - tickDelta) / 5;
		} else if (entity.hasPassenger(e -> e instanceof EntityStrawGolem) && entity.getPassengers().size() == 1) {
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
}