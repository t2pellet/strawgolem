package com.commodorethrawn.strawgolem.config;

import net.minecraft.block.Block;

import java.util.List;

public class ConfigHelper {

    public static boolean isReplantEnabled() {
        return StrawgolemConfig.harvesting.get("replantEnabled", Boolean.class);
    }

    public static boolean isDeliveryEnabled() {
        return StrawgolemConfig.harvesting.get("deliveryEnabled", Boolean.class);
    }

    public static boolean isSoundsEnabled() {
        return StrawgolemConfig.miscellaneous.get("soundsEnabled", Boolean.class);
    }

    public static boolean isShiverEnabled() {
        return StrawgolemConfig.miscellaneous.get("shiverEnabled", Boolean.class);
    }

    public static boolean isEnableHwyla() {
        return StrawgolemConfig.miscellaneous.get("enableHwyla", Boolean.class);
    }

    public static boolean doGolemPickup() {
        return StrawgolemConfig.miscellaneous.get("golemInteract", Boolean.class);
    }

    public static boolean isTetherEnabled() {
        return StrawgolemConfig.tether.get("tetherEnabled", Boolean.class);
    }

    public static boolean doesTemptResetTether() {
        return StrawgolemConfig.tether.get("temptResetsTether", Boolean.class);
    }

    public static int getTetherMaxRange() {
        return StrawgolemConfig.tether.get("tetherMaxRange", Integer.class);
    }

    public static int getSearchRangeHorizontal() {
        return StrawgolemConfig.harvesting.get("searchRangeHorizontal", Integer.class);
    }

    public static int getLifespan() {
        return StrawgolemConfig.health.get("lifespan", Integer.class);
    }

    public static int getHunger() {
        return StrawgolemConfig.health.get("hunger", Integer.class);
    }

    public static boolean isLifespanPenalty(String penalty) {
        switch (penalty) {
            case "rain":
                return StrawgolemConfig.health.get("rainPenalty", Boolean.class);
            case "water":
                return StrawgolemConfig.health.get("waterPenalty", Boolean.class);
            case "heavy":
                return StrawgolemConfig.health.get("heavyPenalty", Boolean.class);
            default:
                return false;
        }
    }

    public static int getSearchRangeVertical() {
        return StrawgolemConfig.harvesting.get("searchRangeVertical", Integer.class);
    }

    public static boolean blockHarvestAllowed(Block block) {
        switch (StrawgolemConfig.harvesting.get("filterMode", String.class)) {
            case StrawgolemConfig.FILTER_MODE_WHITELIST:
                // prioritise whitelist
                StrawgolemConfig.FilterMatch whitelistMatch = blockMatchesFilter(block, StrawgolemConfig.harvesting.getAll("whitelist"));
                // if we got a whitelist match by mod, check if we're blacklisted by item
                if (whitelistMatch == StrawgolemConfig.FilterMatch.Mod)
                    return blockMatchesFilter(block, StrawgolemConfig.harvesting.getAll("blacklist")) != StrawgolemConfig.FilterMatch.Exact;
                return whitelistMatch != StrawgolemConfig.FilterMatch.None;

            case StrawgolemConfig.FILTER_MODE_BLACKLIST:
                // prioritise blacklist
                StrawgolemConfig.FilterMatch blacklistMatch = blockMatchesFilter(block, StrawgolemConfig.harvesting.getAll("blacklist"));
                // if we got a blacklist match by mod, check if we're whitelisted by item
                if (blacklistMatch == StrawgolemConfig.FilterMatch.Mod)
                    return blockMatchesFilter(block, StrawgolemConfig.harvesting.getAll("whitelist")) == StrawgolemConfig.FilterMatch.Exact;
                return blacklistMatch == StrawgolemConfig.FilterMatch.None;

            default:
                return true;
        }
    }

    public static StrawgolemConfig.FilterMatch blockMatchesFilter(Block block, List<? extends String> filter) {
        StrawgolemConfig.FilterMatch bestMatch = StrawgolemConfig.FilterMatch.None;

        for (String s : filter) {
            String[] elements = s.split(":");

            if (elements.length == 1 && block.getLootTableId().getNamespace().equals(elements[0])) {
                bestMatch = StrawgolemConfig.FilterMatch.Mod;
                continue;
            }

            if (elements.length >= 2 && block.getLootTableId().getNamespace().equals(elements[0]) && block.getLootTableId().getPath().equals(elements[1])) {
                bestMatch = StrawgolemConfig.FilterMatch.Exact;
                break;
            }
        }

        return bestMatch;
    }
}
