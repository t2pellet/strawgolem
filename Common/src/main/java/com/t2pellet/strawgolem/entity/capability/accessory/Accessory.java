package com.t2pellet.strawgolem.entity.capability.accessory;

import com.t2pellet.strawgolem.entity.capability.Capability;

public interface Accessory extends Capability {

    static Accessory getInstance() {
        return new AccessoryImpl();
    }

    boolean hasHat();

    void setHasHat(boolean hasHat);

}
