package com.t2pellet.strawgolem.client.renderer.entity;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.client.renderer.entity.model.ModelStrawngGolem;
import com.t2pellet.strawgolem.entity.EntityStrawngGolem;
import com.t2pellet.strawgolem.registry.ClientRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderStrawngGolem extends MobRenderer<EntityStrawngGolem, ModelStrawngGolem> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(StrawgolemCommon.MODID, "textures/entity/strawng_golem.png");

    public RenderStrawngGolem(EntityRendererProvider.Context context) {
        super(context, new ModelStrawngGolem(context.bakeLayer(ClientRegistry.Entities.getStrawngGolemModel())), 1.05F);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityStrawngGolem entity) {
        return TEXTURE;
    }


}
