package com.commodorethrawn.strawgolem.entity.capability;

public interface CapabilityFactory<T extends Capability> {
    public T getInstance();
}
