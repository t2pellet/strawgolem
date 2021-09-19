package com.commodorethrawn.strawgolem;

import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.commodorethrawn.strawgolem.registry.ClientRegistry;
import com.commodorethrawn.strawgolem.registry.CommonRegistry;
import com.commodorethrawn.strawgolem.storage.StrawgolemSaveData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Strawgolem implements ModInitializer, ClientModInitializer {
    public static final String MODID = "strawgolem";
    public static final Logger logger = LogManager.getLogger(MODID);
    private static StrawgolemSaveData data;
    private static StrawgolemConfig config;

    @Override
    public void onInitialize() {
        config = StrawgolemConfig.init();
        CommonRegistry.register();
        registerSaveData();
    }

    @Override
    public void onInitializeClient() {
        ClientRegistry.register();
    }

    public void registerSaveData() {
        ServerWorldEvents.LOAD.register((minecraftServer, serverWorld) -> {
            data = new StrawgolemSaveData(minecraftServer);
            try {
                data.loadData(serverWorld);
                config.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ServerWorldEvents.UNLOAD.register((minecraftServer, serverWorld) -> {
            try {
                data.saveData(serverWorld);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
