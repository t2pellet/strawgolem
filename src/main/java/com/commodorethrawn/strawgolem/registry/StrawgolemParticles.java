package com.commodorethrawn.strawgolem.registry;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.client.particle.FlyParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class StrawgolemParticles {

    public static final DefaultParticleType FLY_PARTICLE = new DefaultParticleType(true) {};

    public static void register() {
        Registry.register(
                Registry.PARTICLE_TYPE,
                new Identifier(Strawgolem.MODID, "fly"),
                FLY_PARTICLE);
        ParticleFactoryRegistry.getInstance().register(FLY_PARTICLE, FlyParticle.Factory::new);
    }

}
