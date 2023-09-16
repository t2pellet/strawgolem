package com.t2pellet.strawgolem.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.t2pellet.strawgolem.client.model.StrawgolemGeoModel;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.held_item.HeldItem;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class StrawgolemItemLayer extends GeoLayerRenderer<StrawGolem> {

    private final StrawgolemGeoModel model;
    private final ItemInHandRenderer itemInHandRenderer;

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
            model.translateToHand(golem, matrixStackIn);
            if (golem.isHoldingBlock()) {
                renderBlock(matrixStackIn, bufferIn, packedLightIn, golem, heldItem.get());
            } else renderItem(matrixStackIn, bufferIn, packedLightIn, golem, heldItem.get());
            matrixStackIn.popPose();
        }
    }

    private void renderItem(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, StrawGolem golem, ItemStack item) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0, 0.6F, 0);
        matrixStackIn.scale(0.5F, 0.5F, 0.5F);
        this.itemInHandRenderer.renderItem(golem, item, ItemTransforms.TransformType.FIXED, true, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.popPose();
    }

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, StrawGolem golem, ItemStack item) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0, -1.4F, 0);
        this.itemInHandRenderer.renderItem(golem, item, ItemTransforms.TransformType.FIXED, true, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.popPose();
    }
}
