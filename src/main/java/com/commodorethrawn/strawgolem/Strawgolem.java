package com.commodorethrawn.strawgolem;

import com.commodorethrawn.strawgolem.client.compat.CompatHwyla;
import com.commodorethrawn.strawgolem.events.*;
import com.commodorethrawn.strawgolem.network.PacketHandler;
import com.commodorethrawn.strawgolem.storage.StrawgolemSaveData;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Strawgolem implements ModInitializer, ClientModInitializer {
    public static final String MODID = "strawgolem";
    public static final Logger logger = LogManager.getLogger(MODID);
    private static StrawgolemSaveData data;

    @Override
    public void onInitialize() {
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
        ServerLifecycleEvents.SERVER_STOPPING.register(IronGolemHandler::stopHolding);
        ServerTickEvents.END_WORLD_TICK.register(CropGrowthHandler::tick);
        CropGrowthCallback.EVENT.register(CropGrowthHandler::onCropGrowth);
        UseBlockCallback.EVENT.register(GolemCreationHandler::onGolemBuilt);
        UseBlockCallback.EVENT.register(GolemCreationHandler::onGolemBuiltAlternate);
        UseBlockCallback.EVENT.register(GolemChestHandler::setPriorityChest);
        if (FabricLoader.getInstance().isModLoaded("waila")) {
            WailaTooltipEvent.WAILA_HANDLE_TOOLTIP.register(CompatHwyla::patchTooltip);
        }
    }

    @Override
    public void onInitializeClient() {
        PacketHandler.register();
    }

    public static StrawgolemSaveData getSaveData() {
        return data;
    }
}
