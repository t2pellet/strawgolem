package com.t2pellet.strawgolem.registry;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.client.particle.FlyParticle;
import com.t2pellet.strawgolem.client.renderer.entity.RenderStrawGolem;
import com.t2pellet.strawgolem.client.renderer.entity.RenderStrawngGolem;
import com.t2pellet.strawgolem.client.renderer.entity.model.ModelStrawGolem;
import com.t2pellet.strawgolem.client.renderer.entity.model.ModelStrawngGolem;
import com.t2pellet.strawgolem.platform.Services;
import net.minecraft.client.model.geom.ModelLayerLocation;

import static com.t2pellet.strawgolem.registry.CommonRegistry.Entities.STRAWNG_GOLEM_TYPE;
import static com.t2pellet.strawgolem.registry.CommonRegistry.Entities.STRAW_GOLEM_TYPE;

public class ClientRegistry {

    public static class Entities {

        private static ModelLayerLocation STRAW_GOLEM_MODEL;
        private static ModelLayerLocation STRAWNG_GOLEM_MODEL;

        public static ModelLayerLocation getStrawGolemModel() {
            if (STRAW_GOLEM_MODEL == null) {
                STRAW_GOLEM_MODEL = new ModelLayerLocation(STRAW_GOLEM_TYPE.get().getDefaultLootTable(), "main");
            }
            return STRAW_GOLEM_MODEL;
        }

        public static ModelLayerLocation getStrawngGolemModel() {
            if (STRAWNG_GOLEM_MODEL == null) {
                STRAWNG_GOLEM_MODEL = new ModelLayerLocation(STRAWNG_GOLEM_TYPE.get().getDefaultLootTable(), "main");
            }
            return STRAWNG_GOLEM_MODEL;
        }

        public static void register() {
            StrawgolemCommon.LOG.info("Registering entity renderers");
            Services.CLIENT_REGISTRY.registerEntityRenderer(STRAW_GOLEM_TYPE, RenderStrawGolem::new, Entities::getStrawGolemModel, ModelStrawGolem.createModelData());
            Services.CLIENT_REGISTRY.registerEntityRenderer(STRAWNG_GOLEM_TYPE, RenderStrawngGolem::new, Entities::getStrawngGolemModel, ModelStrawngGolem.createModelData());
        }
    }

    public static class Particles {

        public static void register() {
            StrawgolemCommon.LOG.info("Registering particle factory");
            Services.CLIENT_REGISTRY.registerParticleFactory(CommonRegistry.Particles::getFlyParticle, FlyParticle.Factory::new);
        }

    }

}
