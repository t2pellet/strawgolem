package com.t2pellet.strawgolem.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.t2pellet.strawgolem.Constants;
import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.decay.DecayState;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.util.RenderUtils;

public class StrawgolemGeoModel extends AnimatedGeoModel<StrawGolem> {

    private static final ResourceLocation modelResource = new ResourceLocation(Constants.MOD_ID, "geo/strawgolem.geo.json");
    private static final ResourceLocation animationResource = new ResourceLocation(Constants.MOD_ID, "animations/strawgolem.animation.json");

    // Textures
    private static final ResourceLocation newTextureResource = new ResourceLocation(Constants.MOD_ID, "textures/straw_golem.png");
    private static final ResourceLocation oldTextureResource = new ResourceLocation(Constants.MOD_ID, "textures/straw_golem_old.png");
    private static final ResourceLocation dyingTextureResource = new ResourceLocation(Constants.MOD_ID, "textures/straw_golem_dying.png");

    // Magic numbers
    private static final float ITEM_TRANSLATE_FACTOR = 0.165F;


    @Override
    public ResourceLocation getModelResource(StrawGolem golem) {
        return modelResource;
    }

    @Override
    public ResourceLocation getTextureResource(StrawGolem golem) {
        if (!StrawgolemConfig.Visual.golemDecayingTexture.get()) return newTextureResource;

        DecayState state = golem.getDecay().getState();
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

    public void translateToHand(PoseStack poseStack) {
        GeoBone arms = (GeoBone) getBone("arms");
        GeoBone upper = (GeoBone) getBone("upper");
        RenderUtils.prepMatrixForBone(poseStack, upper);
        RenderUtils.translateAndRotateMatrixForBone(poseStack, upper);
        RenderUtils.prepMatrixForBone(poseStack, arms);
        RenderUtils.translateAndRotateMatrixForBone(poseStack, arms);
    }
}
