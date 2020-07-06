package com.commodorethrawn.strawgolem.entity.strawgolem;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class RenderStrawGolem extends MobRenderer<EntityStrawGolem, ModelStrawGolem> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Strawgolem.MODID, "textures/entity/straw_golem.png");
    private static final ResourceLocation TEXTURE_OLD = new ResourceLocation(Strawgolem.MODID, "textures/entity/old_straw_golem.png");

    public RenderStrawGolem(EntityRendererManager rendermanagerIn) {
        super(rendermanagerIn, new ModelStrawGolem(), 0.5f);
        this.addLayer(new HeldItemLayer<>(this));
    }

    @Override
    public void render(EntityStrawGolem entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        ModelStrawGolem golem = this.getEntityModel();
        golem.holdingItem = !entityIn.isHandEmpty();
        golem.holdingBlock = entityIn.holdingFullBlock();
        // Shivering movement
        Biome b = entityIn.world.getBiome(entityIn.getPosition());
        if (b.getTempCategory() == Biome.TempCategory.COLD ||
                (b.getTempCategory() == Biome.TempCategory.MEDIUM && entityIn.getPosY() > 100)) {
            double offX = entityIn.getRNG().nextDouble() / 32 - 1 / 64F;
            double offZ = entityIn.getRNG().nextDouble() / 32 - 1 / 64F;
            matrixStackIn.translate(offX, 0, offZ);
        }
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityStrawGolem golem) {
        if (golem.getCurrentLifespan() < StrawgolemConfig.getLifespan() / 2) {
            return TEXTURE_OLD;
        }
        return TEXTURE;
    }

}
