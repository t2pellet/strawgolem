package com.t2pellet.strawgolem.entity.capabilities.decay;

import com.t2pellet.tlib.entity.capability.api.Capability;
import com.t2pellet.tlib.entity.capability.api.ICapabilityHaver;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface Decay extends Capability {

    static <E extends Entity & ICapabilityHaver> Decay getInstance(E entity) {
        return new DecayImpl<>((LivingEntity & ICapabilityHaver) entity);
    }

    void decay();
    void setFromHealth();

    boolean repair();

    DecayState getState();

}
