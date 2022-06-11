package com.t2pellet.strawgolem.platform;

import com.t2pellet.strawgolem.platform.services.IClientRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Function;
import java.util.function.Supplier;

public class FabricClientRegistry implements IClientRegistry {
    @Override
    public <T extends ParticleOptions> void registerParticleFactory(Supplier<ParticleType<T>> type, Function<SpriteSet, ParticleProvider<T>> aNew) {
        ParticleFactoryRegistry.getInstance().register(type.get(), aNew::apply);
    }

    @Override
    public <T extends Entity> void registerEntityRenderer(Supplier<EntityType<T>> type, Function<EntityRenderDispatcher, EntityRenderer<T>> renderSupplier) {
        EntityRendererRegistry.INSTANCE.register(type.get(), (manager, context) -> renderSupplier.apply(manager));
    }
}
