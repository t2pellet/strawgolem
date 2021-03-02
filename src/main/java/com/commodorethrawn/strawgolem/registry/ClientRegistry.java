package com.commodorethrawn.strawgolem.registry;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.client.particle.FlyParticle;
import com.commodorethrawn.strawgolem.client.renderer.entity.RenderIronGolem;
import com.commodorethrawn.strawgolem.client.renderer.entity.RenderStrawGolem;
import com.commodorethrawn.strawgolem.client.renderer.entity.RenderStrawngGolem;
import com.commodorethrawn.strawgolem.config.ConfigHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.commodorethrawn.strawgolem.registry.CommonRegistry.STRAWNG_GOLEM_TYPE;
import static com.commodorethrawn.strawgolem.registry.CommonRegistry.STRAW_GOLEM_TYPE;

public class ClientRegistry implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerParticle();
        registerParticleFactory();
        registerEntityRenderer();
    }

    @Environment(EnvType.CLIENT)
    public static void registerEntityRenderer() {
        EntityRendererRegistry.INSTANCE.register(STRAW_GOLEM_TYPE, (dispatcher, ctx) -> {
            return new RenderStrawGolem(dispatcher);
        });
        EntityRendererRegistry.INSTANCE.register(STRAWNG_GOLEM_TYPE, (dispatcher, ctx) -> {
            return new RenderStrawngGolem(dispatcher);
        });
        if (ConfigHelper.doGolemPickup()) {
            EntityRendererRegistry.INSTANCE.register(EntityType.IRON_GOLEM, (dispatcher, ctx) -> {
                return new RenderIronGolem(dispatcher);
            });
        }
    }

    public static final DefaultParticleType FLY_PARTICLE = new DefaultParticleType(true) {};

    @Environment(EnvType.CLIENT)
    public static void registerParticle() {
        Registry.register(
                Registry.PARTICLE_TYPE,
                new Identifier(Strawgolem.MODID, "fly"),
                FLY_PARTICLE);
    }

    @Environment(EnvType.CLIENT)
    public static void registerParticleFactory() {
        ParticleFactoryRegistry.getInstance().register(FLY_PARTICLE, FlyParticle.Factory::new);
    }
}
