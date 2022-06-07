package com.t2pellet.strawgolem.registry;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import com.t2pellet.strawgolem.entity.EntityStrawngGolem;
import com.t2pellet.strawgolem.platform.Services;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

public class CommonRegistry {

    public static class Sounds {
        public static final SoundEvent GOLEM_AMBIENT = new SoundEvent(new ResourceLocation(StrawgolemCommon.MODID, "golem_ambient"));
        public static final SoundEvent GOLEM_STRAINED = new SoundEvent(new ResourceLocation(StrawgolemCommon.MODID, "golem_strained"));
        public static final SoundEvent GOLEM_HURT = new SoundEvent(new ResourceLocation(StrawgolemCommon.MODID, "golem_hurt"));
        public static final SoundEvent GOLEM_DEATH = new SoundEvent(new ResourceLocation(StrawgolemCommon.MODID, "golem_death"));
        public static final SoundEvent GOLEM_HEAL = new SoundEvent(new ResourceLocation(StrawgolemCommon.MODID, "golem_heal"));
        public static final SoundEvent GOLEM_SCARED = new SoundEvent(new ResourceLocation(StrawgolemCommon.MODID, "golem_scared"));
        public static final SoundEvent GOLEM_INTERESTED = new SoundEvent(new ResourceLocation(StrawgolemCommon.MODID, "golem_interested"));
        public static final SoundEvent GOLEM_DISGUSTED = new SoundEvent(new ResourceLocation(StrawgolemCommon.MODID, "golem_disgusted"));

        public static void register() {
            StrawgolemCommon.LOG.info("Registering sounds");
            registerSounds(GOLEM_AMBIENT,
                    GOLEM_STRAINED,
                    GOLEM_HURT,
                    GOLEM_DEATH,
                    GOLEM_HEAL,
                    GOLEM_SCARED,
                    GOLEM_INTERESTED,
                    GOLEM_DISGUSTED);
        }

        private static void registerSounds(SoundEvent... events) {
            for (SoundEvent event : events) {
                Services.COMMON_REGISTRY.registerSound(event);
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
