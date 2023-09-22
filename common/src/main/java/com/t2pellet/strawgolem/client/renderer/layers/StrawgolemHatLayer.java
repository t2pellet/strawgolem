package com.t2pellet.strawgolem.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.client.renderer.MultiBufferSource;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class StrawgolemHatLayer extends GeoLayerRenderer<StrawGolem> {

    public StrawgolemHatLayer(IGeoRenderer<StrawGolem> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, StrawGolem entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    }
}
