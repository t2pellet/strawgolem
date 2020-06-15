package com.commodorethrawn.strawgolem.config;

import com.commodorethrawn.strawgolem.Strawgolem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = Strawgolem.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigHandler {

    @SubscribeEvent
    public static void configEvent(final ModConfig.ModConfigEvent event) {
        if (event.getConfig().getSpec() == ConfigHolder.COMMON_SPEC) {
            StrawgolemConfig.replantEnabled = ConfigHolder.COMMON_CONFIG.replantEnabled.get();
            StrawgolemConfig.deliveryEnabled = ConfigHolder.COMMON_CONFIG.deliveryEnabled.get();
            StrawgolemConfig.lifespan = ConfigHolder.COMMON_CONFIG.lifespan.get();
            StrawgolemConfig.filterMode = ConfigHolder.COMMON_CONFIG.filterMode.get();
            StrawgolemConfig.whitelist = ConfigHolder.COMMON_CONFIG.whitelist.get();
            StrawgolemConfig.blacklist = ConfigHolder.COMMON_CONFIG.blacklist.get();
        }
    }
}
