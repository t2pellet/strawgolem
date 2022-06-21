package com.t2pellet.strawgolem.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.client.renderer.entity.model.StrawGolemModel;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class StrawGolemHatLayer extends RenderLayer<StrawGolem, StrawGolemModel> {

    private static final ResourceLocation HAT_TEXTURE = new ResourceLocation(StrawgolemCommon.MODID, "textures/entity/straw_hat.png");

    public StrawGolemHatLayer(RenderLayerParent<StrawGolem, StrawGolemModel> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, StrawGolem entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        StrawGolemModel model = this.getParentModel();
        model.setHatVisible(entity.getAccessory().hasHat());
        renderColoredCutoutModel(model, HAT_TEXTURE, matrices, vertexConsumers, light, entity, 1.0F, 1.0F, 1.0F);
    }
}
