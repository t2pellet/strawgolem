package com.t2pellet.strawgolem.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.t2pellet.strawgolem.client.renderer.entity.layers.IronGolemCracksLayer;
import com.t2pellet.strawgolem.client.renderer.entity.layers.IronGolemFlowerLayer;
import com.t2pellet.strawgolem.client.renderer.entity.model.ModelIronGolem;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

public class RenderIronGolem extends MobRenderer<IronGolem, ModelIronGolem<IronGolem>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");

    public RenderIronGolem(EntityRendererProvider.Context context) {
        super(context, new ModelIronGolem<>(context.bakeLayer(ModelLayers.IRON_GOLEM)), 0.7F);
        this.addLayer(new IronGolemCracksLayer(this));
        this.addLayer(new IronGolemFlowerLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation( IronGolem ironGolemEntity) {
        return TEXTURE;
    }

    @Override
    protected void setupRotations(IronGolem ironGolemEntity, PoseStack matrixStack, float f, float g, float h) {
        super.setupRotations(ironGolemEntity, matrixStack, f, g, h);
        if (!((double)ironGolemEntity.animationSpeed < 0.01D)) {
            float i = 13.0F;
            float j = ironGolemEntity.animationPosition - ironGolemEntity.animationSpeed * (1.0F - h) + 6.0F;
            float k = (Math.abs(j % 13.0F - 6.5F) - 3.25F) / 3.25F;
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(6.5F * k));
        }
    }
}
