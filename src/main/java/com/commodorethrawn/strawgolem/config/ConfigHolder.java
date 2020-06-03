package com.commodorethrawn.strawgolem.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHolder {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final StrawgolemConfig.CommonConfig COMMON_CONFIG;

    static {
        {
            final Pair<StrawgolemConfig.CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(StrawgolemConfig.CommonConfig::new);
            COMMON_CONFIG = specPair.getLeft();
            COMMON_SPEC = specPair.getRight();
        }
    }
}
