package com.t2pellet.strawgolem.registry;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.StrawngGolem;
import com.t2pellet.strawgolem.platform.Services;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

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
        static Supplier<EntityType<StrawGolem>> STRAW_GOLEM_TYPE;
        static Supplier<EntityType<StrawngGolem>> STRAWNG_GOLEM_TYPE;

        public static EntityType<StrawGolem> getStrawGolemType() {
            return STRAW_GOLEM_TYPE.get();
        }

        public static EntityType<StrawngGolem> getStrawngGolemType() {
            return STRAWNG_GOLEM_TYPE.get();
        }

        public static void register() {
            StrawgolemCommon.LOG.info("Registering entities");
            STRAW_GOLEM_TYPE = Services.COMMON_REGISTRY.registerEntity("strawgolem", StrawGolem::new, 0.6F, 0.9F, StrawGolem::createMob);
            STRAWNG_GOLEM_TYPE = Services.COMMON_REGISTRY.registerEntity("strawnggolem", StrawngGolem::new, 1.25F, 3.5F, StrawngGolem::createMob);
        }

    }

    public static class Particles {
        private static Supplier<SimpleParticleType> FLY_PARTICLE;

        public static SimpleParticleType getFlyParticle() {
            return FLY_PARTICLE.get();
        }

        public static void register() {
            StrawgolemCommon.LOG.info("Registering particles");
            FLY_PARTICLE = Services.COMMON_REGISTRY.registerParticle(new ResourceLocation(StrawgolemCommon.MODID, "fly"));
        }

    }

    public static class Items {

        private static Supplier<Item> STRAW_HAT;

        public static Item getStrawHat() {
            return STRAW_HAT.get();
        }

        public static void register() {
            StrawgolemCommon.LOG.info("Registering items");
            STRAW_HAT = Services.COMMON_REGISTRY.registerItem(
                    new ResourceLocation(StrawgolemCommon.MODID, "straw_hat"),
                    new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC));
        }

    }

}
