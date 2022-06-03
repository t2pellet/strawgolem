package com.t2pellet.strawgolem;

import com.t2pellet.strawgolem.platform.ForgeCommonRegistry;
import com.t2pellet.strawgolem.storage.StrawgolemSaveData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

import java.io.IOException;
import java.util.function.Consumer;

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
        // Client pre-init
        if (FMLLoader.getDist().isClient()) {
            StrawgolemCommon.initClient();
        }
        // Save Data
        MinecraftForge.EVENT_BUS.addListener((Consumer<ServerStartingEvent>) event -> {
            StrawgolemCommon.data = new StrawgolemSaveData(event.getServer());
            try {
                StrawgolemCommon.data.loadData(event.getServer());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        MinecraftForge.EVENT_BUS.addListener((Consumer<ServerStoppingEvent>) event -> {
            try {
                StrawgolemCommon.data.saveData(event.getServer());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        StrawgolemCommon.init();
    }
}