package com.commodorethrawn.strawgolem.client.renderer.entity;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.client.renderer.entity.model.ModelStrawngGolem;
import com.commodorethrawn.strawgolem.entity.EntityStrawngGolem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RenderStrawngGolem extends MobEntityRenderer<EntityStrawngGolem, ModelStrawngGolem> {

    private static final Identifier TEXTURE = new Identifier(Strawgolem.MODID, "textures/entity/strawng_golem.png");

    public RenderStrawngGolem(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ModelStrawngGolem(), 1.05F);
    }

    @Override
    public Identifier getTexture(EntityStrawngGolem entity) {
        return TEXTURE;
    }


}
