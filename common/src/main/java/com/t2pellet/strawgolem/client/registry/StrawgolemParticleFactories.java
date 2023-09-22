package com.t2pellet.strawgolem.client.registry;


import com.t2pellet.strawgolem.client.particle.FlyParticle;
import com.t2pellet.strawgolem.registry.StrawgolemParticles;
import com.t2pellet.tlib.client.registry.api.ParticleFactoryEntryType;
import com.t2pellet.tlib.registry.api.RegistryClass;
import net.minecraft.core.particles.ParticleType;

@RegistryClass.IRegistryClass(ParticleType.class)
public class StrawgolemParticleFactories implements RegistryClass {

    @IRegistryEntry
    public static final ParticleFactoryEntryType FLY_PARTICLE = new ParticleFactoryEntryType(StrawgolemParticles.FLY_PARTICLE, FlyParticle.Factory::new);

}
