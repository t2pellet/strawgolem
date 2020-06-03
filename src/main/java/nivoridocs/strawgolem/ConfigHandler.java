package nivoridocs.strawgolem;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = nivoridocs.strawgolem.Strawgolem.MODID)
public class ConfigHandler {
    @SubscribeEvent
    public static void configEvent(final ModConfig.ModConfigEvent event) {
        if (event.getConfig().getSpec() == ConfigHolder.COMMON_SPEC) {
            bakeConfig();
        }
    }

    private static void bakeConfig() {
        StrawgolemConfig.replantEnabled = ConfigHolder.COMMON_CONFIG.replantEnabled.get();
        StrawgolemConfig.lifespan = ConfigHolder.COMMON_CONFIG.lifespan.get();
        StrawgolemConfig.filterMode = ConfigHolder.COMMON_CONFIG.filterMode.get();
        StrawgolemConfig.whitelist = ConfigHolder.COMMON_CONFIG.whitelist.get();
        StrawgolemConfig.blacklist = ConfigHolder.COMMON_CONFIG.blacklist.get();
    }
}
