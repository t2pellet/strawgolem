package com.commodorethrawn.strawgolem.entity.capability;

import java.util.Optional;

public interface CapabilityHandler {

    /**
     * The capability handler instance. Use this to register & instantiate your capabilities
     */
    public static CapabilityHandler INSTANCE = new CapabilityHandlerImpl();

    /**
     * Registers the given capability.
     * @param cap : the capability class
     * @param factory : the capability factory
     * @param <T> the capability parameter
     * @param <R> the factory parameter
     */
    public <T extends Capability, R extends CapabilityFactory<T>> void register(Class<T> cap, R factory);

    /**
     * Instantiates the given capability, if registered
     * @param cap the capability class
     * @param <T> the capability parameter
     * @return an optional for the instance of the capability. Optional is empty if the capability is not registered
     */
    public <T extends Capability> Optional<T> get(Class<T> cap);

}
