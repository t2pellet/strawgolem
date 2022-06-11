package com.t2pellet.strawgolem.platform;

import com.t2pellet.strawgolem.platform.services.IClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ForgeClientRegistry implements IClientRegistry {

    @Override
    public <T extends ParticleOptions> void registerParticleFactory(Supplier<ParticleType<T>> type, Function<SpriteSet, ParticleProvider<T>> aNew) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((Consumer<ParticleFactoryRegisterEvent>) particleFactoryRegisterEvent -> {
            Minecraft.getInstance().particleEngine.register(type.get(), aNew::apply);
        });
    }

    @Override
    public <T extends net.minecraft.world.entity.Entity> void registerEntityRenderer(Supplier<net.minecraft.world.entity.EntityType<T>> type, Function<EntityRenderDispatcher, EntityRenderer<T>> renderSupplier) {
        RenderingRegistry.registerEntityRenderingHandler(type.get(), renderSupplier::apply);
    }
}
