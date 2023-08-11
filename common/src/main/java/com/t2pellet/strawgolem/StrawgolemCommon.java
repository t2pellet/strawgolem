package com.t2pellet.strawgolem;

import com.t2pellet.tlib.common.TLibMod;
import com.t2pellet.tlib.common.entity.capability.IModCapabilities;
import com.t2pellet.tlib.common.network.IModPackets;
import com.t2pellet.tlib.common.registry.IModEntities;
import com.t2pellet.tlib.common.registry.IModItems;
import com.t2pellet.tlib.common.registry.IModParticles;
import com.t2pellet.tlib.common.registry.IModSounds;
import com.t2pellet.tlib.config.Config;


public class StrawgolemCommon extends TLibMod {
    public static final StrawgolemCommon INSTANCE = new StrawgolemCommon();

    @Override
    public IModEntities entities() {
        return super.entities();
    }

    @Override
    public IModItems items() {
        return super.items();
    }

    @Override
    public IModParticles particles() {
        return super.particles();
    }

    @Override
    public IModSounds sounds() {
        return super.sounds();
    }

    @Override
    public IModPackets packets() {
        return super.packets();
    }

    @Override
    public IModCapabilities capabilities() {
        return super.capabilities();
    }

    @Override
    public Config config() {
        return super.config();
    }
}