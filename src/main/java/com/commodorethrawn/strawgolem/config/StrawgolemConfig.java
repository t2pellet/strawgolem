package com.commodorethrawn.strawgolem.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.Collections;
import java.util.List;

@EventBusSubscriber
public class StrawgolemConfig {

    static final String FILTER_MODE_WHITELIST = "whitelist";
    static final String FILTER_MODE_BLACKLIST = "blacklist";

    /* Harvesting */
    static boolean replantEnabled;
    static boolean deliveryEnabled;
    static int searchRangeHorizontal;
    static int searchRangeVertical;
    static String filterMode;
    static List<? extends String> whitelist;
    static List<? extends String> blacklist;
    /* Misc */
    static boolean soundsEnabled;
    static boolean shiverEnabled;
    static boolean golemInteract;
    static boolean enableHwyla;
    /* Tethering */
    static boolean tetherEnabled;
    static boolean temptResetsTether;
    static int tetherMinRange;
    static int tetherMaxRange;
    /* Lifespan */
    static int lifespan;
    static boolean rainPenalty;
    static boolean waterPenalty;
    static boolean heavyPenalty;

    public enum FilterMatch {
        None,
        Mod,
        Exact,
    }

    public static class CommonConfig {
        final ForgeConfigSpec.BooleanValue enableReplant, enableDelivery;
        final ForgeConfigSpec.IntValue lifespan;
        final ForgeConfigSpec.ConfigValue<String> filterMode;
        final ForgeConfigSpec.ConfigValue<List<? extends String>> whitelist, blacklist;
        final ForgeConfigSpec.IntValue searchRangeHorizontal, searchRangeVertical;
        final ForgeConfigSpec.BooleanValue soundsEnabled, golemInteract, shiverEnabled;
        final ForgeConfigSpec.BooleanValue enableHwyla;
        final ForgeConfigSpec.BooleanValue waterPenalty, rainPenalty, heavyPenalty;
        final ForgeConfigSpec.BooleanValue tetherEnabled, tetherToTemptEnabled;
        final ForgeConfigSpec.IntValue tetherRangeMin, tetherRangeMax;

        CommonConfig(final ForgeConfigSpec.Builder builder) {
            builder.push("Harvesting");
            enableReplant = builder.comment("Allow the straw golems to replant a crop when they harvest it.").define("enableReplant", true);
            enableDelivery = builder.comment("Allow the straw golem to deliver a crop (requires replantEnabled = true)").define("enableDelivery", true);
            searchRangeHorizontal = builder.comment("Horizontal search range for crops and chests").defineInRange("searchRangeHorizontal", 12, 8, 32);
            searchRangeVertical = builder.comment("Vertical search range for crops and chests").defineInRange("searchRangeVertical", 3, 2, 8);
            builder.pop();
            builder.push("Filtration");
            filterMode = builder.comment(
                    "Sets the method for applying harvest filters.  Note that only the most specific match will be taken into consideration.",
                    "If a crop's mod appears in the whitelist, but the crop itself is in the blacklist, the crop will be banned.",
                    "Likewise if a crop's mod appears in the blacklist, but the crop itself is in the whitelist, the crop will be allowed.",
                    "\"none\": allow all crops to be harvested (default).",
                    "\"whitelist\": will deny crops from being harvested unless the most specific match is in the whitelist.",
                    "\"blacklist\": will allows crops to be harvested unless the most specific match is in the blacklist.").define("filterMode", "none");
            whitelist = builder.comment("Whitelist Filter").defineList("whitelist", Collections.emptyList(), o -> o instanceof String);
            blacklist = builder.comment("Blacklist Filter").defineList("blacklist", Collections.emptyList(), o -> o instanceof String);
            builder.pop();
            builder.push("Lifespan");
            lifespan = builder.comment("Set the lifespan, in tick, of new created straw golems. Set -1 for infinite.").defineInRange("lifespan", 168000, -2, Integer.MAX_VALUE);
            heavyPenalty = builder.comment("Enable lifespan penalty for carrying a heavy item").define("penaltyHeavy", true);
            rainPenalty = builder.comment("Enable lifespan penalty for being in the rain").define("penaltyRain", true);
            waterPenalty = builder.comment("Enable lifespan penalty for being in the water").define("penaltyWater", true);
            builder.pop();
            builder.push("Tether");
            tetherEnabled = builder.comment("Anchor golems to a spot so they don't wander very far.").define("tetherEnabled", true);
            tetherToTemptEnabled = builder.comment("Tempting golems with an apple updates their tether location if pulled too far").define("temptResetsTether", false);
            tetherRangeMin = builder.comment("Range that golems will consider within their tether location").defineInRange("tetherMinRange", 4, 1, 16);
            tetherRangeMax = builder.comment("Range from tether that will cause golems to turn and run back").defineInRange("tetherMaxRange", 16, 1, 32);
            builder.pop();
            builder.push("Miscellaneous");
            soundsEnabled = builder.comment("Enable/disable golem sounds").define("soundsEnabled", true);
            shiverEnabled = builder.comment("Enable/disable golem shivering in cold").define("shiverEnabled", true);
            golemInteract = builder.comment("Enable iron golems picking up straw golems occasionally").define("golemInteract", true);
            enableHwyla = builder.comment("Enable HWYLA compatibility").define("enableHwyla", true);
            builder.pop();
        }
    }
}
