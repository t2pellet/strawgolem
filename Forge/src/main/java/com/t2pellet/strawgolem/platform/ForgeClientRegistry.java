package com.t2pellet.strawgolem.platform;

import com.t2pellet.strawgolem.platform.services.IClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ForgeClientRegistry implements IClientRegistry {

    @Override
    public <T extends Entity> void registerEntityRenderer(Supplier<EntityType<T>> type, EntityRendererProvider<T> renderSupplier, Supplier<ModelLayerLocation> model, LayerDefinition modelData) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((Consumer<EntityRenderersEvent.RegisterRenderers>) event -> {
            event.registerEntityRenderer(type.get(), renderSupplier);
        });
        FMLJavaModLoadingContext.get().getModEventBus().addListener((Consumer<EntityRenderersEvent.RegisterLayerDefinitions>) event -> {
            event.registerLayerDefinition(model.get(), () -> modelData);
        });
    }

    @Override
    public <T extends ParticleOptions> void registerParticleFactory(Supplier<ParticleType<T>> type, Function<SpriteSet, ParticleProvider<T>> aNew) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((Consumer<RegisterParticleProvidersEvent>) particleFactoryRegisterEvent -> {
            Minecraft.getInstance().particleEngine.register(type.get(), aNew::apply);
        });
    }
}
