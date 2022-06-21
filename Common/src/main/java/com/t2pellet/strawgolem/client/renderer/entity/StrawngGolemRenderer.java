package com.t2pellet.strawgolem.client.renderer.entity;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.client.renderer.entity.model.StrawngGolemModel;
import com.t2pellet.strawgolem.entity.StrawngGolem;
import com.t2pellet.strawgolem.registry.ClientRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class StrawngGolemRenderer extends MobRenderer<StrawngGolem, StrawngGolemModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(StrawgolemCommon.MODID, "textures/entity/strawng_golem.png");

    public StrawngGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new StrawngGolemModel(context.bakeLayer(ClientRegistry.Entities.getStrawngGolemModel())), 1.05F);
    }

    @Override
    public ResourceLocation getTextureLocation(StrawngGolem entity) {
        return TEXTURE;
    }


}
