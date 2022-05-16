package com.t2pellet.strawgolem;

import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.capability.CapabilityHandler;
import com.t2pellet.strawgolem.entity.capability.hunger.Hunger;
import com.t2pellet.strawgolem.entity.capability.lifespan.Lifespan;
import com.t2pellet.strawgolem.entity.capability.memory.Memory;
import com.t2pellet.strawgolem.entity.capability.tether.Tether;
import com.t2pellet.strawgolem.platform.Services;
import com.t2pellet.strawgolem.platform.services.ICommonRegistry;
import com.t2pellet.strawgolem.platform.services.IPacketHandler;
import com.t2pellet.strawgolem.registry.ClientRegistry;
import com.t2pellet.strawgolem.registry.CommonRegistry;
import com.t2pellet.strawgolem.storage.StrawgolemSaveData;
import com.t2pellet.strawgolem.util.io.ConfigHelper;
import net.minecraft.core.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


public class StrawgolemCommon {

    public static final String MODID = "strawgolem";
    public static final Logger LOG = LogManager.getLogger(MODID);

    public static StrawgolemSaveData data;

    public static void preInit() {
        try {
            ConfigHelper.register(new StrawgolemConfig());
            LOG.debug("Registered config");
        } catch (IOException | IllegalAccessException e) {
            LOG.debug("Failed to register config");
        }
        Services.COMMON_REGISTRY.registerEvents();
        CommonRegistry.Particles.register();
        CommonRegistry.Entities.register();
        CommonRegistry.Sounds.register();
        registerCapabilities();
    }

    public static void preInitClient() {
        ClientRegistry.Particles.register();
        ClientRegistry.Entities.register();
    }

    public static void init() {
        registerCrops();
        IPacketHandler.registerIDs();
        IPacketHandler.registerServer();
    }

    public static void initClient() {
        IPacketHandler.registerClient();
    }

    private static void registerCapabilities() {
        CapabilityHandler.INSTANCE.register(Hunger.class, Hunger::getInstance);
        CapabilityHandler.INSTANCE.register(Lifespan.class, Lifespan::getInstance);
        CapabilityHandler.INSTANCE.register(Memory.class, Memory::getInstance);
        CapabilityHandler.INSTANCE.register(Tether.class, Tether::getInstance);
    }

    private static void registerCrops() {
        Registry.BLOCK.forEach(ICommonRegistry::registerCrop);
    }


}
