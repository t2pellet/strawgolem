package com.commodorethrawn.strawgolem;

import com.commodorethrawn.strawgolem.registry.ClientRegistry;
import com.commodorethrawn.strawgolem.registry.CommonRegistry;
import com.commodorethrawn.strawgolem.storage.StrawgolemSaveData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
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
    }

    @Override
    public void onInitializeClient() {
        ClientRegistry.register();
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
