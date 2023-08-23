package com.t2pellet.strawgolem.client.renderer;

import com.t2pellet.strawgolem.entity.EntityTypes;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.tlib.client.registry.IModEntityRenderers;

public class EntityRenderers implements IModEntityRenderers {

    public static final TLibEntityRenderer<StrawGolem> STRAW_GOLEM_RENDERER = new TLibEntityRenderer<>(
            EntityTypes.strawGolem(),
            StrawGolemRenderer::new
    );

}