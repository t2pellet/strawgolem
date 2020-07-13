package com.commodorethrawn.strawgolem.config;

import net.minecraft.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.Collections;
import java.util.List;

@EventBusSubscriber
public class StrawgolemConfig {

    private static final String FILTER_MODE_WHITELIST = "whitelist";
    private static final String FILTER_MODE_BLACKLIST = "blacklist";

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
    /* Lifespan */
    static int lifespan;
    static boolean rainPenalty;
    static boolean waterPenalty;
    static boolean heavyPenalty;

    public static boolean isReplantEnabled() {
        return replantEnabled;
    }

    public static boolean isDeliveryEnabled() {
        return deliveryEnabled;
    }

    public static boolean isSoundsEnabled() {
        return soundsEnabled;
    }

    public static boolean isShiverEnabled() {
        return shiverEnabled;
    }

    public static boolean isEnableHwyla() {
        return enableHwyla;
    }

    public static boolean doGolemPickup() {
        return golemInteract;
    }

    public static int getSearchRangeHorizontal() {
        return searchRangeHorizontal;
    }

    public static int getLifespan() {
        return lifespan;
    }

    public static boolean isLifespanPenalty(String penalty) {
        if (penalty.equals("rain")) return rainPenalty;
        else if (penalty.equals("water")) return waterPenalty;
        else if (penalty.equals("heavy")) return heavyPenalty;
        return false;
    }

    public static int getSearchRangeVertical() {
        return searchRangeVertical;
    }

    public static boolean blockHarvestAllowed(Block block) {
        switch (filterMode) {
            case FILTER_MODE_WHITELIST:
                // prioritise whitelist
                FilterMatch whitelistMatch = blockMatchesFilter(block, whitelist);
                // if we got a whitelist match by mod, check if we're blacklisted by item
                if (whitelistMatch == FilterMatch.Mod)
                    return blockMatchesFilter(block, blacklist) != FilterMatch.Exact;
                return whitelistMatch != FilterMatch.None;

            case FILTER_MODE_BLACKLIST:
                // prioritise blacklist
                FilterMatch blacklistMatch = blockMatchesFilter(block, whitelist);
                // if we got a blacklist match by mod, check if we're whitelisted by item
                if (blacklistMatch == FilterMatch.Mod)
                    return blockMatchesFilter(block, whitelist) == FilterMatch.Exact;
                return blacklistMatch == FilterMatch.None;

            default:
                return true;
        }
    }

    public static FilterMatch blockMatchesFilter(Block block, List<? extends String> filter) {
        FilterMatch bestMatch = FilterMatch.None;

        for (String s : filter) {
            String[] elements = s.split(":");

            if (elements.length == 1 && block.getRegistryName().getNamespace().equals(elements[0])) {
                bestMatch = FilterMatch.Mod;
                continue;
            }

            if (elements.length >= 2 && block.getRegistryName().getNamespace().equals(elements[0]) && block.getRegistryName().getPath().equals(elements[1])) {
                bestMatch = FilterMatch.Exact;
                break;
            }
        }

        return bestMatch;
    }

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

        CommonConfig(final ForgeConfigSpec.Builder builder) {
            builder.push("Harvesting");
            enableReplant = builder.comment("Allow the straw golems to replant a crop when they harvest it.").define("replantEnabled", true);
            enableDelivery = builder.comment("Allow the straw golem to deliver a crop (requires replantEnabled = true)").define("deliveryEnabled", true);
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
            lifespan = builder.comment("Set the lifespan, in tick, of new created straw golems. Set -1 for infinite.").defineInRange("lifespan", 168000, -1, Integer.MAX_VALUE);
            heavyPenalty = builder.comment("Enable lifespan penalty for carrying a heavy item").define("heavyPenalty", true);
            rainPenalty = builder.comment("Enable lifespan penalty for being in the rain").define("rainPenalty", true);
            waterPenalty = builder.comment("Enable lifespan penalty for being in the water").define("waterPenalty", true);
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
