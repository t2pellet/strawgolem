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
            StrawgolemConfig.replantEnabled = ConfigHolder.COMMON_CONFIG.enableReplant.get();
            StrawgolemConfig.deliveryEnabled = ConfigHolder.COMMON_CONFIG.enableDelivery.get();
            StrawgolemConfig.searchRangeHorizontal = ConfigHolder.COMMON_CONFIG.searchRangeHorizontal.get();
            StrawgolemConfig.searchRangeVertical = ConfigHolder.COMMON_CONFIG.searchRangeVertical.get();

            StrawgolemConfig.lifespan = ConfigHolder.COMMON_CONFIG.lifespan.get();
            StrawgolemConfig.heavyPenalty = ConfigHolder.COMMON_CONFIG.heavyPenalty.get();
            StrawgolemConfig.waterPenalty = ConfigHolder.COMMON_CONFIG.waterPenalty.get();
            StrawgolemConfig.rainPenalty = ConfigHolder.COMMON_CONFIG.rainPenalty.get();

            StrawgolemConfig.filterMode = ConfigHolder.COMMON_CONFIG.filterMode.get();
            StrawgolemConfig.whitelist = ConfigHolder.COMMON_CONFIG.whitelist.get();
            StrawgolemConfig.blacklist = ConfigHolder.COMMON_CONFIG.blacklist.get();

            StrawgolemConfig.soundsEnabled = ConfigHolder.COMMON_CONFIG.soundsEnabled.get();
            StrawgolemConfig.shiverEnabled = ConfigHolder.COMMON_CONFIG.shiverEnabled.get();
            StrawgolemConfig.golemInteract = ConfigHolder.COMMON_CONFIG.golemInteract.get();
            StrawgolemConfig.enableHwyla = ConfigHolder.COMMON_CONFIG.enableHwyla.get();

            StrawgolemConfig.tetherEnabled = ConfigHolder.COMMON_CONFIG.tetherEnabled.get();
            StrawgolemConfig.temptResetsTether = ConfigHolder.COMMON_CONFIG.tetherToTemptEnabled.get();
            StrawgolemConfig.tetherMinRange = ConfigHolder.COMMON_CONFIG.tetherRangeMin.get();
            StrawgolemConfig.tetherMaxRange = ConfigHolder.COMMON_CONFIG.tetherRangeMax.get();
        }
    }
}
