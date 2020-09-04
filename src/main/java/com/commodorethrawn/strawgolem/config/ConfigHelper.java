package com.commodorethrawn.strawgolem.config;

import net.minecraft.block.Block;

import java.util.List;

public class ConfigHelper {
    public static boolean isReplantEnabled() {
        return StrawgolemConfig.replantEnabled;
    }

    public static boolean isDeliveryEnabled() {
        return StrawgolemConfig.deliveryEnabled;
    }

    public static boolean isSoundsEnabled() {
        return StrawgolemConfig.soundsEnabled;
    }

    public static boolean isShiverEnabled() {
        return StrawgolemConfig.shiverEnabled;
    }

    public static boolean isEnableHwyla() {
        return StrawgolemConfig.enableHwyla;
    }

    public static boolean doGolemPickup() {
        return StrawgolemConfig.golemInteract;
    }

    public static boolean isTetherEnabled() { return StrawgolemConfig.tetherEnabled; }
    public static boolean doesTemptResetTether() { return StrawgolemConfig.temptResetsTether; }

    public static int getTetherMinRange() { return StrawgolemConfig.tetherMinRange; }
    public static int getTetherMaxRange() { return StrawgolemConfig.tetherMaxRange; }

    public static int getSearchRangeHorizontal() {
        return StrawgolemConfig.searchRangeHorizontal;
    }

    public static int getLifespan() {
        return StrawgolemConfig.lifespan;
    }

    public static boolean isLifespanPenalty(String penalty) {
        if (penalty.equals("rain")) return StrawgolemConfig.rainPenalty;
        else if (penalty.equals("water")) return StrawgolemConfig.waterPenalty;
        else if (penalty.equals("heavy")) return StrawgolemConfig.heavyPenalty;
        return false;
    }

    public static int getSearchRangeVertical() {
        return StrawgolemConfig.searchRangeVertical;
    }

    public static boolean blockHarvestAllowed(Block block) {
        switch (StrawgolemConfig.filterMode) {
            case StrawgolemConfig.FILTER_MODE_WHITELIST:
                // prioritise whitelist
                StrawgolemConfig.FilterMatch whitelistMatch = blockMatchesFilter(block, StrawgolemConfig.whitelist);
                // if we got a whitelist match by mod, check if we're blacklisted by item
                if (whitelistMatch == StrawgolemConfig.FilterMatch.Mod)
                    return blockMatchesFilter(block, StrawgolemConfig.blacklist) != StrawgolemConfig.FilterMatch.Exact;
                return whitelistMatch != StrawgolemConfig.FilterMatch.None;

            case StrawgolemConfig.FILTER_MODE_BLACKLIST:
                // prioritise blacklist
                StrawgolemConfig.FilterMatch blacklistMatch = blockMatchesFilter(block, StrawgolemConfig.whitelist);
                // if we got a blacklist match by mod, check if we're whitelisted by item
                if (blacklistMatch == StrawgolemConfig.FilterMatch.Mod)
                    return blockMatchesFilter(block, StrawgolemConfig.whitelist) == StrawgolemConfig.FilterMatch.Exact;
                return blacklistMatch == StrawgolemConfig.FilterMatch.None;

            default:
                return true;
        }
    }

    public static StrawgolemConfig.FilterMatch blockMatchesFilter(Block block, List<? extends String> filter) {
        StrawgolemConfig.FilterMatch bestMatch = StrawgolemConfig.FilterMatch.None;

        for (String s : filter) {
            String[] elements = s.split(":");

            if (elements.length == 1 && block.getRegistryName().getNamespace().equals(elements[0])) {
                bestMatch = StrawgolemConfig.FilterMatch.Mod;
                continue;
            }

            if (elements.length >= 2 && block.getRegistryName().getNamespace().equals(elements[0]) && block.getRegistryName().getPath().equals(elements[1])) {
                bestMatch = StrawgolemConfig.FilterMatch.Exact;
                break;
            }
        }

        return bestMatch;
    }
}
