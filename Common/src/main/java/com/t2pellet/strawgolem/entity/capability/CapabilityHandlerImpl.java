package com.t2pellet.strawgolem.entity.capability;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class CapabilityHandlerImpl implements CapabilityHandler {

    private final Map<Class<?>, CapabilityFactory<?>> capabilityFactoryMap = new HashMap<>();

    @Override
    public <T extends Capability, R extends CapabilityFactory<T>> void register(Class<T> cap, R factory) {
        capabilityFactoryMap.put(cap, factory);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Capability> Optional<T> get(Class<T> cap) {
        CapabilityFactory<T> factory = (CapabilityFactory<T>) capabilityFactoryMap.get(cap);
        if (factory != null) {
            return Optional.of(factory.getInstance());
        }
        return Optional.empty();
    }

}
