package com.commodorethrawn.strawgolem;

import com.commodorethrawn.strawgolem.registry.ClientRegistry;
import com.commodorethrawn.strawgolem.registry.CommonRegistry;
import com.commodorethrawn.strawgolem.storage.StrawgolemSaveData;
import com.commodorethrawn.strawgolem.util.scheduler.ClientScheduler;
import com.commodorethrawn.strawgolem.util.scheduler.ServerScheduler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Strawgolem implements ModInitializer, ClientModInitializer {
    public static final String MODID = "strawgolem";
    public static final Logger logger = LogManager.getLogger(MODID);
    private static StrawgolemSaveData data;

    public static StrawgolemSaveData getSaveData() {
        return data;
    }

    @Override
    public void onInitialize() {
        CommonRegistry.register();
        registerSaveData();
        ServerTickEvents.END_WORLD_TICK.register(ServerScheduler.INSTANCE::tick);
    }

    @Override
    public void onInitializeClient() {
        ClientRegistry.register();
        ClientTickEvents.END_WORLD_TICK.register(ClientScheduler.INSTANCE::tick);
    }

    public void registerSaveData() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            data = new StrawgolemSaveData(server);
            try {
                data.loadData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            try {
                data.saveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
