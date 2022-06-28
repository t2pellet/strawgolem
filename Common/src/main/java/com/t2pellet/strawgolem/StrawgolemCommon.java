package com.t2pellet.strawgolem;

import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.capability.CapabilityHandler;
import com.t2pellet.strawgolem.entity.capability.accessory.Accessory;
import com.t2pellet.strawgolem.entity.capability.hunger.Hunger;
import com.t2pellet.strawgolem.entity.capability.lifespan.Lifespan;
import com.t2pellet.strawgolem.entity.capability.memory.Memory;
import com.t2pellet.strawgolem.entity.capability.tether.Tether;
import com.t2pellet.strawgolem.platform.Services;
import com.t2pellet.strawgolem.platform.services.ICommonRegistry;
import com.t2pellet.strawgolem.platform.services.IPacketHandler;
import com.t2pellet.strawgolem.registry.ClientRegistry;
import com.t2pellet.strawgolem.registry.CommonRegistry;
import com.t2pellet.strawgolem.util.io.ConfigHelper;
import net.minecraft.core.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


public class StrawgolemCommon {

    public static final String MODID = "strawgolem";
    public static final Logger LOG = LogManager.getLogger(MODID);

    public static void preInit() {
        StrawgolemCommon.LOG.info("Pre-init started!");
        try {
            LOG.info("Registering config");
            ConfigHelper.register(StrawgolemConfig::new);
        } catch (IOException | IllegalAccessException e) {
            LOG.error("Failed to register config");
        }
        Registry.BLOCK.forEach(ICommonRegistry::registerCrop);
        Services.COMMON_REGISTRY.registerEvents();
        CommonRegistry.Particles.register();
        CommonRegistry.Items.register();
        CommonRegistry.Entities.register();
        CommonRegistry.Sounds.register();
        registerCapabilities();
    }

    public static void initClient() {
        StrawgolemCommon.LOG.info("Client init started!");
        ClientRegistry.Particles.register();
        ClientRegistry.Entities.register();
    }

    public static void init() {
        StrawgolemCommon.LOG.info("Init started!");
        IPacketHandler.registerIDs();
        IPacketHandler.registerPackets();
    }

    private static void registerCapabilities() {
        StrawgolemCommon.LOG.info("Registering capabilities");
        CapabilityHandler.INSTANCE.register(Hunger.class, Hunger::getInstance);
        CapabilityHandler.INSTANCE.register(Lifespan.class, Lifespan::getInstance);
        CapabilityHandler.INSTANCE.register(Memory.class, Memory::getInstance);
        CapabilityHandler.INSTANCE.register(Tether.class, Tether::getInstance);
        CapabilityHandler.INSTANCE.register(Accessory.class, Accessory::getInstance);
    }


}
