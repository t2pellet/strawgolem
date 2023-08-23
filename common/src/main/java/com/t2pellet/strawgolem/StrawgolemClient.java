package com.t2pellet.strawgolem;

import com.t2pellet.strawgolem.client.renderer.EntityRenderers;
import com.t2pellet.tlib.client.TLibModClient;
import com.t2pellet.tlib.client.registry.IModEntityModels;
import com.t2pellet.tlib.client.registry.IModEntityRenderers;
import com.t2pellet.tlib.client.registry.IModParticleFactories;

public class StrawgolemClient extends TLibModClient {
    public static final StrawgolemClient INSTANCE = new StrawgolemClient();

    @Override
    public IModEntityModels entityModels() {
        return super.entityModels();
    }

    @Override
    public IModEntityRenderers entityRenderers() {
        return new EntityRenderers();
    }

    @Override
    public IModParticleFactories particleFactories() {
        return super.particleFactories();
    }
}
