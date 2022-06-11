package com.t2pellet.strawgolem.registry;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.client.particle.FlyParticle;
import com.t2pellet.strawgolem.client.renderer.entity.StrawGolemRenderer;
import com.t2pellet.strawgolem.client.renderer.entity.StrawngGolemRenderer;
import com.t2pellet.strawgolem.platform.Services;

import static com.t2pellet.strawgolem.registry.CommonRegistry.Entities.STRAWNG_GOLEM_TYPE;
import static com.t2pellet.strawgolem.registry.CommonRegistry.Entities.STRAW_GOLEM_TYPE;

public class ClientRegistry {

    public static class Entities {

        public static void register() {
            StrawgolemCommon.LOG.info("Registering entity renderers");
            Services.CLIENT_REGISTRY.registerEntityRenderer(STRAW_GOLEM_TYPE, StrawGolemRenderer::new);
            Services.CLIENT_REGISTRY.registerEntityRenderer(STRAWNG_GOLEM_TYPE, StrawngGolemRenderer::new);
        }
    }

    public static class Particles {

        public static void register() {
            StrawgolemCommon.LOG.info("Registering particle factory");
            Services.CLIENT_REGISTRY.registerParticleFactory(CommonRegistry.Particles::getFlyParticle, FlyParticle.Factory::new);
        }

    }

}
