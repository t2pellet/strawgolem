package com.commodorethrawn.strawgolem.config;

import net.minecraftforge.fml.config.ModConfig;

public class ConfigHandler {

    public void configEvent(final ModConfig.ModConfigEvent event) {
        if (event.getConfig().getSpec() == ConfigHolder.COMMON_SPEC) {
            StrawgolemConfig.harvestEnabled = ConfigHolder.COMMON_CONFIG.replantEnabled.get();
            StrawgolemConfig.lifespan = ConfigHolder.COMMON_CONFIG.lifespan.get();
            StrawgolemConfig.filterMode = ConfigHolder.COMMON_CONFIG.filterMode.get();
            StrawgolemConfig.whitelist = ConfigHolder.COMMON_CONFIG.whitelist.get();
            StrawgolemConfig.blacklist = ConfigHolder.COMMON_CONFIG.blacklist.get();
        }
    }
}
