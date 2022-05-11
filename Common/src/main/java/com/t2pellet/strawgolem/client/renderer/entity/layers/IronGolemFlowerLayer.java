package com.t2pellet.strawgolem.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.t2pellet.strawgolem.client.renderer.entity.model.ModelIronGolem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.block.Blocks;

public class IronGolemFlowerLayer extends RenderLayer<IronGolem, ModelIronGolem<IronGolem>> {

    public IronGolemFlowerLayer(RenderLayerParent<IronGolem, ModelIronGolem<IronGolem>> renderer) {
        super(renderer);
    }

    @Override
    public void render( PoseStack matrices,  MultiBufferSource vertexConsumers, int light, IronGolem entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity.getOfferFlowerTick() != 0) {
            matrices.pushPose();
            ModelPart armHoldingRose = this.getParentModel().getRightArm();
            armHoldingRose.translateAndRotate(matrices);
            matrices.translate(-1.1875D, 1.0625D, -0.9375D);
            matrices.translate(0.5D, 0.5D, 0.5D);
            matrices.scale(0.5F, 0.5F, 0.5F);
            matrices.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            matrices.translate(-0.5D, -0.5D, -0.5D);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.POPPY.defaultBlockState(), matrices, vertexConsumers, light, OverlayTexture.NO_OVERLAY);
            matrices.popPose();
        }
    }

}
