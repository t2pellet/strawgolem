package com.commodorethrawn.strawgolem.entity.irongolem;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class IronGolemCracksLayer extends LayerRenderer<IronGolemEntity, ModelIronGolem<IronGolemEntity>> {
    private static final Map<IronGolemEntity.Cracks, ResourceLocation> field_229134_a_ = ImmutableMap.of(IronGolemEntity.Cracks.LOW, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_low.png"), IronGolemEntity.Cracks.MEDIUM, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_medium.png"), IronGolemEntity.Cracks.HIGH, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_high.png"));

    public IronGolemCracksLayer(IEntityRenderer<IronGolemEntity, ModelIronGolem<IronGolemEntity>> p_i226040_1_) {
        super(p_i226040_1_);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, IronGolemEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entitylivingbaseIn.isInvisible()) {
            IronGolemEntity.Cracks irongolementity$cracks = entitylivingbaseIn.func_226512_l_();
            if (irongolementity$cracks != IronGolemEntity.Cracks.NONE) {
                ResourceLocation resourcelocation = field_229134_a_.get(irongolementity$cracks);
                renderCutoutModel(this.getEntityModel(), resourcelocation, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
