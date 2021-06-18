package com.commodorethrawn.strawgolem.registry;

import com.commodorethrawn.strawgolem.client.compat.CompatHwyla;
import com.commodorethrawn.strawgolem.crop.CropRegistry;
import com.commodorethrawn.strawgolem.entity.capability.CapabilityHandler;
import com.commodorethrawn.strawgolem.entity.capability.hunger.Hunger;
import com.commodorethrawn.strawgolem.entity.capability.lifespan.Lifespan;
import com.commodorethrawn.strawgolem.entity.capability.memory.Memory;
import com.commodorethrawn.strawgolem.entity.capability.tether.Tether;
import com.commodorethrawn.strawgolem.events.*;
import com.commodorethrawn.strawgolem.network.PacketHandler;
import com.commodorethrawn.strawgolem.util.scheduler.ActionScheduler;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;

public class CommonRegistry {

    public static void register() {
        StrawgolemSounds.register();
        StrawgolemEntities.register();
        PacketHandler.register();
        registerEvents();
        registerCapabilities();
        registerCrops();
    }

    private static void registerEvents() {
        // Crop Registry
        RegistryEntryAddedCallback.event(Registry.BLOCK).register((i, id, block) -> registerCrop(block));
        // Server Scheduler
        ServerTickEvents.END_WORLD_TICK.register(ActionScheduler.INSTANCE::tick);
        // Iron Golem Handling
        ServerLifecycleEvents.SERVER_STOPPING.register(IronGolemHandler::stopHolding);
        //Crop growth handling
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

    private static void registerCrops() {
        Registry.BLOCK.forEach(CommonRegistry::registerCrop);
    }

    private static void registerCrop(Block block) {
        if (block instanceof CropBlock) CropRegistry.INSTANCE.register(block, ((CropBlock) block).getAgeProperty());
        else if (block instanceof GourdBlock) CropRegistry.INSTANCE.register(block, null);
        else if (block instanceof NetherWartBlock) CropRegistry.INSTANCE.register(block, NetherWartBlock.AGE);
        else if (block instanceof PlantBlock && block instanceof Fertilizable) {
            // Register any Fertilizable PlantBlock with 3, 5 or 7 age states
            IntProperty[] ageProperties = {Properties.AGE_3, Properties.AGE_5, Properties.AGE_7};
            Arrays.stream(ageProperties)
                    .filter(age -> block.getDefaultState().contains(age))
                    .findFirst()
                    .ifPresent(ageProperty -> CropRegistry.INSTANCE.register(block, ageProperty));
        }
    }

}
