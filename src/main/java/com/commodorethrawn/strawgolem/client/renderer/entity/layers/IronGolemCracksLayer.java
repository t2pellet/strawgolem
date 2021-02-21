package com.commodorethrawn.strawgolem.client.renderer.entity.layers;

import com.commodorethrawn.strawgolem.client.renderer.entity.model.ModelIronGolem;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;

import java.util.Map;

public class IronGolemCracksLayer extends FeatureRenderer<IronGolemEntity, ModelIronGolem<IronGolemEntity>> {
    private static final Map<IronGolemEntity.Crack, Identifier> field_229134_a_ = ImmutableMap.of(
            IronGolemEntity.Crack.LOW, new Identifier("textures/entity/iron_golem/iron_golem_crackiness_low.png"),
            IronGolemEntity.Crack.MEDIUM, new Identifier("textures/entity/iron_golem/iron_golem_crackiness_medium.png"),
            IronGolemEntity.Crack.HIGH, new Identifier("textures/entity/iron_golem/iron_golem_crackiness_high.png"));

    public IronGolemCracksLayer(FeatureRendererContext<IronGolemEntity, ModelIronGolem<IronGolemEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, IronGolemEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!entity.isInvisible()) {
            IronGolemEntity.Crack cracks = entity.getCrack();
            if (cracks != IronGolemEntity.Crack.NONE) {
                Identifier Identifier = field_229134_a_.get(cracks);
                renderModel(this.getContextModel(), Identifier, matrices, vertexConsumers, light, entity, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
