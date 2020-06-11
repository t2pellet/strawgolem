package com.commodorethrawn.strawgolem.entity;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;

public class RenderStrawGolem extends LivingRenderer<EntityStrawGolem, ModelStrawGolem> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Strawgolem.MODID, "textures/entity/straw_golem.png");

	public RenderStrawGolem(EntityRendererManager rendermanagerIn) {
		super(rendermanagerIn, new ModelStrawGolem(), 0.5f);
        this.addLayer(new HeldItemLayer(this));
    }

    @Override
    public void render(EntityStrawGolem entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        ModelStrawGolem golem = this.getEntityModel();
        golem.holdingItem = !entityIn.isHandEmpty();
        golem.holdingBlock = entityIn.getHeldItem(Hand.MAIN_HAND).getItem() instanceof BlockItem;
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityStrawGolem entity) {
        return TEXTURE;
    }

    @Override
    protected boolean canRenderName(EntityStrawGolem entity) {
        return entity.hasCustomName();
    }


}
