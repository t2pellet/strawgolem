package com.t2pellet.strawgolem.entity.capability;

public interface CapabilityFactory<T extends Capability> {
    public T getInstance();
}
