package com.t2pellet.strawgolem.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.t2pellet.strawgolem.client.model.StrawgolemGeoModel;
import com.t2pellet.strawgolem.client.renderer.layers.StrawgolemItemLayer;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class StrawGolemRenderer extends GeoEntityRenderer<StrawGolem> {

    public StrawGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StrawgolemGeoModel());
        this.addLayer(new StrawgolemItemLayer(this, (StrawgolemGeoModel) modelProvider, renderManager.getItemInHandRenderer()));
    }

    @Override
    public void render(StrawGolem animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // Shivering animation
        if (animatable.isInWaterRainOrBubble() || animatable.isInCold()) {
            double offX = animatable.getRandom().nextDouble() / 32 - 1 / 64F;
            double offZ = animatable.getRandom().nextDouble() / 32 - 1 / 64F;
            poseStack.translate(offX, 0, offZ);
        }
        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
