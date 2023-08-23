package com.t2pellet.strawgolem.entity.capabilities;

import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.decay.Decay;
import com.t2pellet.tlib.common.entity.capability.CapabilityRegistrar;
import com.t2pellet.tlib.common.entity.capability.ICapabilityHaver;
import com.t2pellet.tlib.common.entity.capability.IModCapabilities;
import net.minecraft.world.level.entity.EntityAccess;

public class Capabilities implements IModCapabilities {

    @ICapability(Decay.class)
    public static final TLibCapability<Decay> decay = new TLibCapability<>(new CapabilityRegistrar.CapabilityFactory<Decay>() {
        @Override
        public <E extends ICapabilityHaver & EntityAccess> Decay get(E e) {
            return Decay.getInstance((StrawGolem) e);
        }
    });

}
