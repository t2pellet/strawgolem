package com.t2pellet.strawgolem;

import com.t2pellet.strawgolem.storage.StrawgolemSaveData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;

import static net.minecraft.world.level.Level.OVERWORLD;

public class StrawgolemFabric implements ModInitializer, ClientModInitializer {

    private static MinecraftServer server;

    public static MinecraftServer getServer() {
        return server;
    }

    @Override
    public void onInitialize() {
        StrawgolemCommon.preInit();
        StrawgolemCommon.init();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> StrawgolemFabric.server = server);
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.dimension().equals(OVERWORLD)) {
                StrawgolemSaveData data = new StrawgolemSaveData(server);
                try {
                    data.loadData(server);
                } catch (Exception e) {
                    StrawgolemCommon.LOG.error("Failed to load legacy strawgolem save data:", e);
                }
            }
        });
    }

    @Override
    public void onInitializeClient() {
        StrawgolemCommon.initClient();
    }
}
