package com.commodorethrawn.strawgolem.registry;

import com.commodorethrawn.strawgolem.client.compat.CompatHwyla;
import com.commodorethrawn.strawgolem.entity.capability.CapabilityHandler;
import com.commodorethrawn.strawgolem.entity.capability.hunger.Hunger;
import com.commodorethrawn.strawgolem.entity.capability.lifespan.Lifespan;
import com.commodorethrawn.strawgolem.entity.capability.memory.Memory;
import com.commodorethrawn.strawgolem.entity.capability.tether.Tether;
import com.commodorethrawn.strawgolem.events.*;
import com.commodorethrawn.strawgolem.events.CropGrowthCallback;
import com.commodorethrawn.strawgolem.network.PacketHandler;
import com.commodorethrawn.strawgolem.util.scheduler.ServerScheduler;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;

public class CommonRegistry {

    public static void register() {
        StrawgolemSounds.register();
        StrawgolemEntities.register();
        PacketHandler.register();
        registerEvents();
        registerCapabilities();
    }

    private static void registerEvents() {
        // Server Scheduler
        ServerTickEvents.END_WORLD_TICK.register(ServerScheduler.INSTANCE::tick);
        // Iron Golem Handling
        ServerLifecycleEvents.SERVER_STOPPING.register(IronGolemHandler::stopHolding);
        //Crop growth handling
        ServerTickEvents.END_WORLD_TICK.register(CropGrowthHandler::tick);
        CropGrowthCallback.EVENT.register(CropGrowthHandler::onCropGrowth);
        //Golem Creation Handling
        UseBlockCallback.EVENT.register(GolemCreationHandler::onGolemBuilt);
        UseBlockCallback.EVENT.register(GolemCreationHandler::onGolemBuiltAlternate);
        //Chest Handling
        UseBlockCallback.EVENT.register(GolemChestHandler::setPriorityChest);
        //WAILA
        if (FabricLoader.getInstance().isModLoaded("waila")) {
            WailaTooltipEvent.WAILA_HANDLE_TOOLTIP.register(CompatHwyla::patchTooltip);
        }
    }

    private static void registerCapabilities() {
        CapabilityHandler.INSTANCE.register(Hunger.class, Hunger::getInstance);
        CapabilityHandler.INSTANCE.register(Lifespan.class, Lifespan::getInstance);
        CapabilityHandler.INSTANCE.register(Memory.class, Memory::getInstance);
        CapabilityHandler.INSTANCE.register(Tether.class, Tether::getInstance);
    }

}
