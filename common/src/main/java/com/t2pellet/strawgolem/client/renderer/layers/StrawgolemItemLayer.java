package com.t2pellet.strawgolem.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.t2pellet.strawgolem.client.model.StrawgolemGeoModel;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.held_item.HeldItem;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class StrawgolemItemLayer extends GeoLayerRenderer<StrawGolem> {

    private final ItemInHandRenderer itemInHandRenderer;
    private final StrawgolemGeoModel model;

    public StrawgolemItemLayer(IGeoRenderer<StrawGolem> entityRendererIn, StrawgolemGeoModel model, ItemInHandRenderer itemInHandRenderer) {
        super(entityRendererIn);
        this.model = model;
        this.itemInHandRenderer = itemInHandRenderer;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, StrawGolem golem, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        HeldItem heldItem = golem.getHeldItem();
        if (heldItem.has()) {
            matrixStackIn.pushPose();
            this.model.translateToHand(matrixStackIn);
            this.renderItem(matrixStackIn, bufferIn, packedLightIn, golem);
            matrixStackIn.popPose();
        }
    }

    private void renderItem(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, StrawGolem golem) {
        HeldItem heldItem = golem.getHeldItem();
        boolean isBlock = golem.isHoldingBlock();
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(isBlock ? -180.0F : -90.0F));
        matrixStackIn.translate(0, isBlock ? -0.3F : -0.45F, isBlock ? 0.0F : -0.15F);
        matrixStackIn.scale(0.5F, 0.5F, 0.5F);
        this.itemInHandRenderer.renderItem(golem, heldItem.get(), ItemTransforms.TransformType.NONE, false, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.popPose();
    }
}
