package com.t2pellet.strawgolem;

import com.t2pellet.strawgolem.platform.ForgeCommonRegistry;
import com.t2pellet.strawgolem.storage.StrawgolemSaveData;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.IOException;
import java.util.function.Consumer;

import static net.minecraft.world.level.Level.OVERWORLD;

@Mod(StrawgolemCommon.MODID)
public class StrawgolemForge {

    public StrawgolemForge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onCommonSetup);
        // Pre-init
        StrawgolemCommon.preInit();
        // Deferred Registers
        ForgeCommonRegistry.SOUNDS.register(bus);
        ForgeCommonRegistry.PARTICLES.register(bus);
        ForgeCommonRegistry.ENTITIES.register(bus);
        ForgeCommonRegistry.ITEMS.register(bus);
        // Client pre-init
        if (FMLLoader.getDist().isClient()) {
            StrawgolemCommon.initClient();
        }
        // Save Data
        MinecraftForge.EVENT_BUS.addListener((Consumer<ServerStartingEvent>) event -> {
            StrawgolemCommon.data = new StrawgolemSaveData(ServerLifecycleHooks.getCurrentServer());
            try {
                StrawgolemCommon.data.loadData(ServerLifecycleHooks.getCurrentServer());
            } catch (Exception e) {
                StrawgolemCommon.LOG.error("Failed to load strawgolem save data:", e);
            }
        });
        MinecraftForge.EVENT_BUS.addListener((Consumer<WorldEvent.Save>) event -> {
            if (event.getWorld() instanceof ServerLevel level && level.dimension().equals(OVERWORLD)) {
                try {
                    StrawgolemCommon.data.saveData(ServerLifecycleHooks.getCurrentServer());
                } catch (IOException e) {
                    StrawgolemCommon.LOG.error("Failed to save strawgolem save data:", e);
                }
            }
        });
        MinecraftForge.EVENT_BUS.addListener((Consumer<ServerStoppingEvent>) event -> {
            try {
                StrawgolemCommon.data.saveData(ServerLifecycleHooks.getCurrentServer());
            } catch (IOException e) {
                StrawgolemCommon.LOG.error("Failed to save strawgolem save data:", e);
            }
        });
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        StrawgolemCommon.init();
    }
}