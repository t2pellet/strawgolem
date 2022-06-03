package com.t2pellet.strawgolem;

import com.t2pellet.strawgolem.storage.StrawgolemSaveData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;

public class StrawgolemFabric implements ModInitializer, ClientModInitializer {

    private static MinecraftServer server;

    public static MinecraftServer getServer() {
        return server;
    }

    @Override
    public void onInitialize() {
        StrawgolemCommon.preInit();
        StrawgolemCommon.init();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            StrawgolemFabric.server = server;
            StrawgolemCommon.data = new StrawgolemSaveData(server);
            try {
                StrawgolemCommon.data.loadData(server);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            try {
                StrawgolemCommon.data.saveData(server);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onInitializeClient() {
        StrawgolemCommon.initClient();
    }
}
