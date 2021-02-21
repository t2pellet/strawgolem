package com.commodorethrawn.strawgolem.client.renderer.entity.layers;

import com.commodorethrawn.strawgolem.client.renderer.entity.model.ModelIronGolem;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.IronGolemEntity;

public class IronGolemFlowerLayer extends FeatureRenderer<IronGolemEntity, ModelIronGolem<IronGolemEntity>> {

    public IronGolemFlowerLayer(FeatureRendererContext<IronGolemEntity, ModelIronGolem<IronGolemEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, IronGolemEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity.getLookingAtVillagerTicks() != 0) {
            matrices.push();
            ModelPart armHoldingRose = this.getContextModel().getArmHoldingRose();
            armHoldingRose.rotate(matrices);
            matrices.translate(-1.1875D, 1.0625D, -0.9375D);
            matrices.translate(0.5D, 0.5D, 0.5D);
            matrices.scale(0.5F, 0.5F, 0.5F);
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            matrices.translate(-0.5D, -0.5D, -0.5D);
            MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(Blocks.POPPY.getDefaultState(), matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();
        }
    }

}
