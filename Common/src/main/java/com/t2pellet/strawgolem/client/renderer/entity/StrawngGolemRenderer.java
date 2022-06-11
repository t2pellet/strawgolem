package com.t2pellet.strawgolem.client.renderer.entity;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.client.renderer.entity.model.StrawngGolemModel;
import com.t2pellet.strawgolem.entity.StrawngGolem;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class StrawngGolemRenderer extends MobRenderer<StrawngGolem, StrawngGolemModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(StrawgolemCommon.MODID, "textures/entity/strawng_golem.png");

    public StrawngGolemRenderer(EntityRenderDispatcher context) {
        super(context, new StrawngGolemModel(), 1.05F);
    }

    @Override
    public ResourceLocation getTextureLocation(StrawngGolem entity) {
        return TEXTURE;
    }


}
