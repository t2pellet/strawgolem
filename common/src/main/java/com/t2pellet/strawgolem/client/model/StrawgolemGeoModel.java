package com.t2pellet.strawgolem.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.t2pellet.strawgolem.Constants;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.decay.Decay;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.raw.pojo.Bone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class StrawgolemGeoModel extends AnimatedGeoModel<StrawGolem> {

    private static final ResourceLocation modelResource = new ResourceLocation(Constants.MOD_ID, "geo/strawgolem.geo.json");
    private static final ResourceLocation animationResource = new ResourceLocation(Constants.MOD_ID, "animations/strawgolem.animation.json");

    // Textures
    private static final ResourceLocation newTextureResource = new ResourceLocation(Constants.MOD_ID, "textures/straw_golem.png");
    private static final ResourceLocation oldTextureResource = new ResourceLocation(Constants.MOD_ID, "textures/straw_golem.png");
    private static final ResourceLocation dyingTextureResource = new ResourceLocation(Constants.MOD_ID, "textures/straw_golem.png");

    // Magic numbers
    private static final float ITEM_TRANSLATE_FACTOR = 0.17F;
    private static final float BLOCK_TRANSLATE_FACTOR = 0.0F;


    @Override
    public ResourceLocation getModelResource(StrawGolem golem) {
        return modelResource;
    }

    @Override
    public ResourceLocation getTextureResource(StrawGolem golem) {
        Decay.DecayState state = golem.getDecay().getState();
        switch (state) {
            case OLD -> {
                return oldTextureResource;
            }
            case WITHERED -> {
                return dyingTextureResource;
            }
            default -> {
                return newTextureResource;
            }
        }
    }

    @Override
    public ResourceLocation getAnimationResource(StrawGolem golem) {
        return animationResource;
    }

    @Override
    public void setCustomAnimations(StrawGolem animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        IBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * (float) Math.PI / 180);
            head.setRotationY(extraData.netHeadYaw * (float) Math.PI / 180);
        }
    }

    public void translateToHand(StrawGolem golem, PoseStack poseStack) {
        IBone body = getBone("upper");
        IBone arms = getBone("arms");
        boolean holdingBlock = golem.isHoldingBlock();
        float translation = (body.getPositionY() + arms.getPositionY()) * (holdingBlock ? BLOCK_TRANSLATE_FACTOR : ITEM_TRANSLATE_FACTOR);
        poseStack.mulPose(Quaternion.fromXYZ(body.getRotationX(), 0, body.getRotationZ()));
        if (holdingBlock) {
            poseStack.mulPose(Quaternion.fromXYZ(arms.getRotationX(), 0, 0));
            poseStack.translate(0, -translation, 0);
        } else poseStack.translate(0, translation, 0);
    }
}
