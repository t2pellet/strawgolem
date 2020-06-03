package com.commodorethrawn.strawgolem;

import com.commodorethrawn.strawgolem.config.ConfigHandler;
import com.commodorethrawn.strawgolem.config.ConfigHolder;
import com.commodorethrawn.strawgolem.entity.EntityRegistry;
import com.commodorethrawn.strawgolem.entity.RenderStrawGolem;
import com.commodorethrawn.strawgolem.entity.capability.lifespan.ILifespan;
import com.commodorethrawn.strawgolem.entity.capability.lifespan.Lifespan;
import com.commodorethrawn.strawgolem.entity.capability.lifespan.LifespanStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Strawgolem.MODID)
public class Strawgolem {
    public static final String MODID = "strawgolem";
    public Strawgolem() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(new ConfigHandler()::configEvent);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(ILifespan.class, new LifespanStorage(), Lifespan::new);
    }

    public void clientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.STRAW_GOLEM_TYPE, RenderStrawGolem::new);
    }


}
