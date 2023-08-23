package com.t2pellet.strawgolem.client.model;

import com.t2pellet.strawgolem.Constants;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class StrawgolemGeoModel extends AnimatedGeoModel<StrawGolem> {

    private static final ResourceLocation modelResource = new ResourceLocation(Constants.MOD_ID, "geo/strawgolem.geo.json");
    private static final ResourceLocation textureResource = new ResourceLocation(Constants.MOD_ID, "textures/straw_golem.png");
    private static final ResourceLocation animationResource = new ResourceLocation(Constants.MOD_ID, "animations/strawgolem.animation.json");

    @Override
    public ResourceLocation getModelResource(StrawGolem golem) {
        return modelResource;
    }

    @Override
    public ResourceLocation getTextureResource(StrawGolem golem) {
        return textureResource;
    }

    @Override
    public ResourceLocation getAnimationResource(StrawGolem golem) {
        return animationResource;
    }
}
