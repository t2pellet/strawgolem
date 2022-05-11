package com.t2pellet.strawgolem.client.renderer.entity.layers;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.t2pellet.strawgolem.client.renderer.entity.model.ModelIronGolem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

import java.util.Map;

public class IronGolemCracksLayer extends RenderLayer<IronGolem, ModelIronGolem<IronGolem>> {
    private static final Map<IronGolem.Crackiness, ResourceLocation> field_229134_a_ = ImmutableMap.of(
            IronGolem.Crackiness.LOW, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_low.png"),
            IronGolem.Crackiness.MEDIUM, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_medium.png"),
            IronGolem.Crackiness.HIGH, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_high.png"));

    public IronGolemCracksLayer(RenderLayerParent<IronGolem, ModelIronGolem<IronGolem>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, IronGolem entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!entity.isInvisible()) {
            IronGolem.Crackiness cracks = entity.getCrackiness();
            if (cracks != IronGolem.Crackiness.NONE) {
                ResourceLocation resourceLocation = field_229134_a_.get(cracks);
                renderColoredCutoutModel(this.getParentModel(), resourceLocation, matrices, vertexConsumers, light, entity, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
