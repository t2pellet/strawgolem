package com.t2pellet.strawgolem.platform;

import com.t2pellet.strawgolem.platform.services.IClientRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
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
    public <T extends Entity> void registerEntityRenderer(Supplier<EntityType<T>> type, EntityRendererProvider<T> renderSupplier, Supplier<ModelLayerLocation> model, LayerDefinition modelData) {
        EntityRendererRegistry.register(type.get(), renderSupplier);
        EntityModelLayerRegistry.registerModelLayer(model.get(), () -> modelData);
    }
}
