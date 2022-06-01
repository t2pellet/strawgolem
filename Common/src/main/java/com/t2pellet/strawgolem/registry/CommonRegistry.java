package com.t2pellet.strawgolem.registry;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import com.t2pellet.strawgolem.entity.EntityStrawngGolem;
import com.t2pellet.strawgolem.platform.Services;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

public class CommonRegistry {

    public static class Sounds {
        public static final ResourceLocation GOLEM_AMBIENT_ID = new ResourceLocation(StrawgolemCommon.MODID, "golem_ambient");
        public static final ResourceLocation GOLEM_STRAINED_ID = new ResourceLocation(StrawgolemCommon.MODID, "golem_strained");
        public static final ResourceLocation GOLEM_HURT_ID = new ResourceLocation(StrawgolemCommon.MODID, "golem_hurt");
        public static final ResourceLocation GOLEM_DEATH_ID = new ResourceLocation(StrawgolemCommon.MODID, "golem_death");
        public static final ResourceLocation GOLEM_HEAL_ID = new ResourceLocation(StrawgolemCommon.MODID, "golem_heal");
        public static final ResourceLocation GOLEM_SCARED_ID = new ResourceLocation(StrawgolemCommon.MODID, "golem_scared");
        public static final ResourceLocation GOLEM_INTERESTED_ID = new ResourceLocation(StrawgolemCommon.MODID, "golem_interested");
        public static final ResourceLocation GOLEM_DISGUSTED_ID = new ResourceLocation(StrawgolemCommon.MODID, "golem_disgusted");

        public static void register() {
            StrawgolemCommon.LOG.info("Registering sounds");
            registerSounds(GOLEM_AMBIENT_ID,
                    GOLEM_STRAINED_ID,
                    GOLEM_HURT_ID,
                    GOLEM_DEATH_ID,
                    GOLEM_HEAL_ID,
                    GOLEM_SCARED_ID,
                    GOLEM_INTERESTED_ID,
                    GOLEM_DISGUSTED_ID);
        }

        private static void registerSounds(ResourceLocation... ids) {
            for (ResourceLocation id : ids) {
                Services.COMMON_REGISTRY.registerSound(id);
            }
        }
    }

    public static class Entities {
        static Supplier<EntityType<EntityStrawGolem>> STRAW_GOLEM_TYPE;
        static Supplier<EntityType<EntityStrawngGolem>> STRAWNG_GOLEM_TYPE;

        public static EntityType<EntityStrawGolem> getStrawGolemType() {
            return STRAW_GOLEM_TYPE.get();
        }

        public static EntityType<EntityStrawngGolem> getStrawngGolemType() {
            return STRAWNG_GOLEM_TYPE.get();
        }

        public static void register() {
            StrawgolemCommon.LOG.info("Registering entities");
            STRAW_GOLEM_TYPE = Services.COMMON_REGISTRY.registerEntity("strawgolem", EntityStrawGolem::new, 0.6F, 0.9F, EntityStrawGolem::createMob);
            STRAWNG_GOLEM_TYPE = Services.COMMON_REGISTRY.registerEntity("strawnggolem", EntityStrawngGolem::new, 1.25F, 3.5F, EntityStrawngGolem::createMob);
        }

    }

    public static class Particles {
        private static Supplier<ParticleType<SimpleParticleType>> FLY_PARTICLE;

        public static ParticleType<SimpleParticleType> getFlyParticle() {
            return FLY_PARTICLE.get();
        }

        public static void register() {
            StrawgolemCommon.LOG.info("Registering particles");
            FLY_PARTICLE = Services.COMMON_REGISTRY.registerParticle(new ResourceLocation(StrawgolemCommon.MODID, "fly"));
        }

    }

}
