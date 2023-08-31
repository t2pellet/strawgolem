package com.t2pellet.strawgolem.client.renderer;

import com.t2pellet.strawgolem.client.model.StrawgolemGeoModel;
import com.t2pellet.strawgolem.client.renderer.layers.StrawgolemItemLayer;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class StrawGolemRenderer extends GeoEntityRenderer<StrawGolem> {

    public StrawGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StrawgolemGeoModel());
        this.addLayer(new StrawgolemItemLayer(this, renderManager.getItemInHandRenderer()));
    }
}
