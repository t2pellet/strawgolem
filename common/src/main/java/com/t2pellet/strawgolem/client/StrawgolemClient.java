package com.t2pellet.strawgolem.client;

import com.t2pellet.strawgolem.client.registry.StrawgolemEntityRenderers;
import com.t2pellet.strawgolem.client.registry.StrawgolemParticleFactories;
import com.t2pellet.tlib.client.TLibModClient;
import com.t2pellet.tlib.registry.api.RegistryClass;

public class StrawgolemClient extends TLibModClient {
    public static final StrawgolemClient INSTANCE = new StrawgolemClient();

    @Override
    public Class<? extends RegistryClass> entityRenderers() {
        return StrawgolemEntityRenderers.class;
    }

    @Override
    public Class<? extends RegistryClass> particleFactories() {
        return StrawgolemParticleFactories.class;
    }
}
