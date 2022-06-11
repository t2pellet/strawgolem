package com.t2pellet.strawgolem.platform.services;

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

public interface IClientRegistry {

    <T extends ParticleOptions> void registerParticleFactory(Supplier<ParticleType<T>> type, Function<SpriteSet, ParticleProvider<T>> aNew);

    <T extends Entity> void registerEntityRenderer(Supplier<EntityType<T>> type,
                                                   Function<EntityRenderDispatcher, EntityRenderer<T>> renderSupplier);

}
