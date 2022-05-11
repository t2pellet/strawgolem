package com.t2pellet.strawgolem.platform;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.platform.services.*;

import java.util.ServiceLoader;

public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IPacketHandler PACKETS = load(IPacketHandler.class);
    public static final ICommonRegistry COMMON_REGISTRY = load(ICommonRegistry.class);
    public static final IClientRegistry CLIENT_REGISTRY = load(IClientRegistry.class);

    public static final ISidedExecutor SIDE = load(ISidedExecutor.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        StrawgolemCommon.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
