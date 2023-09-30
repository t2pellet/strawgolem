package com.t2pellet.strawgolem;

import com.t2pellet.strawgolem.entity.capabilities.StrawgolemCapabilities;
import com.t2pellet.strawgolem.registry.StrawgolemEntities;
import com.t2pellet.strawgolem.registry.StrawgolemItems;
import com.t2pellet.strawgolem.registry.StrawgolemParticles;
import com.t2pellet.strawgolem.registry.StrawgolemSounds;
import com.t2pellet.tlib.TLibMod;
import com.t2pellet.tlib.config.api.Config;
import com.t2pellet.tlib.entity.capability.api.registry.IModCapabilities;
import com.t2pellet.tlib.registry.api.RegistryClass;

import java.io.IOException;


public class StrawgolemCommon extends TLibMod {
    public static final StrawgolemCommon INSTANCE = new StrawgolemCommon();

    @Override
    public Class<? extends RegistryClass> particles() {
        return StrawgolemParticles.class;
    }

    @Override
    public Class<? extends RegistryClass> entities() {
        return StrawgolemEntities.class;
    }

    @Override
    public Class<? extends RegistryClass> items() {
        return StrawgolemItems.class;
    }

    @Override
    public Class<? extends RegistryClass> sounds() {
        return StrawgolemSounds.class;
    }

    @Override
    public IModCapabilities capabilities() {
        return new StrawgolemCapabilities();
    }

    @Override
    public Config config() throws IOException, IllegalAccessException {
        return new StrawgolemConfig();
    }
}