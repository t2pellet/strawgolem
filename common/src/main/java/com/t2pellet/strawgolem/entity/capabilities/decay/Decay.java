package com.t2pellet.strawgolem.entity.capabilities.decay;

import com.t2pellet.tlib.common.entity.capability.Capability;
import com.t2pellet.tlib.common.entity.capability.ICapabilityHaver;
import net.minecraft.world.entity.Entity;

public interface Decay extends Capability {

    static <E extends Entity & ICapabilityHaver> Decay getInstance(E entity) {
        return new DecayImpl<>(entity);
    }

    void decay();

    boolean repair();

    DecayState getState();

}
