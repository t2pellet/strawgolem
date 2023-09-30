package com.t2pellet.strawgolem.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.client.model.StrawgolemGeoModel;
import com.t2pellet.strawgolem.client.renderer.layers.StrawgolemItemLayer;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class StrawGolemRenderer extends GeoEntityRenderer<StrawGolem> {

    public StrawGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StrawgolemGeoModel());
        this.addLayer(new StrawgolemItemLayer(this, (StrawgolemGeoModel) modelProvider, renderManager.getItemInHandRenderer()));
    }

    @Override
    public void render(GeoModel model, StrawGolem animatable, float partialTick, RenderType type, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // Set whether to render hat
        modelProvider.getBone("hat").setHidden(!animatable.hasHat());
        // Shivering animation
        if (StrawgolemConfig.Visual.golemShiversWhenDecayingFast.get() && animatable.isInWaterOrRain()) {
            if (animatable.isInWater() || !animatable.hasHat()) {
                shiver(animatable, poseStack);
            }
        } else if (StrawgolemConfig.Visual.golemShiversWhenCold.get() && animatable.isInCold()) {
            shiver(animatable, poseStack);
        }
        super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private void shiver(StrawGolem animatable, PoseStack poseStack) {
        double offX = animatable.getRandom().nextDouble() / 32 - 1 / 64F;
        double offZ = animatable.getRandom().nextDouble() / 32 - 1 / 64F;
        poseStack.translate(offX, 0, offZ);
    }

}
