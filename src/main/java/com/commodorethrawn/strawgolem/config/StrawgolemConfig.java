package com.commodorethrawn.strawgolem.config;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.util.io.Config;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.util.ArrayList;

public class StrawgolemConfig extends Config {

    static final String FILTER_MODE_WHITELIST = "whitelist";
    static final String FILTER_MODE_BLACKLIST = "blacklist";

    public static StrawgolemConfig init() {
        try {
            return new StrawgolemConfig();
        } catch (Exception e) {
            System.err.println("Error loading Strawgolem Config");
            e.printStackTrace();
            return null;
        }
    }

    private StrawgolemConfig() throws IOException, IllegalAccessException {
        super(Strawgolem.MODID);
    }

    @Section("Harvesting")
    public static class Harvest {
        @Section.Comment("Enables golems replanting crops")
        private static boolean replantEnabled = true;
        @Section.Comment("Enables golems delivering crops to a chest")
        private static boolean deliveryEnabled = true;
        @Section.Comment("The range of crops golems can detect")
        private static int searchRange = 24;

        @Section.Comment("The golem filtration mode. Enter 'whitelist' or 'blacklist'")
        private static String filterMode = FILTER_MODE_BLACKLIST;
        @Section.Comment("Format Example: whitelist = [minecraft:carrots,minecraft:wheat]")
        private static ArrayList<String> whitelist = new ArrayList<>();
        private static ArrayList<String> blacklist = new ArrayList<>();

        public static boolean isHarvestAllowed(Block block) {
            String blockStr = Registry.BLOCK.getId(block).toString();
            switch (filterMode) {
                case FILTER_MODE_WHITELIST:
                    return whitelist.stream().anyMatch(s -> s.trim().equals(blockStr));
                case FILTER_MODE_BLACKLIST:
                    return blacklist.stream().noneMatch(s -> s.trim().equals(blockStr));
                default:
                    return true;
            }
        }

        public static boolean isReplantEnabled() {
            return replantEnabled;
        }

        public static boolean isDeliveryEnabled() {
            return deliveryEnabled;
        }

        public static int getSearchRange() {
            return searchRange;
        }
    }

    @Section("Miscellaneous")
    public static class Miscellaneous {
        @Section.Comment("Enables golem sounds")
        private static boolean soundsEnabled = true;
        @Section.Comment("Enables golems shivering in the cold & rain")
        private static boolean shiverEnabled = true;
        @Section.Comment("Enables Iron Golem's picking up Straw Golems")
        private static boolean golemInteract = true;
        @Section.Comment("Enables HWYLA Compat")
        private static boolean enableHwyla = true;

        public static boolean isSoundsEnabled() {
            return soundsEnabled;
        }

        public static boolean isShiverEnabled() {
            return shiverEnabled;
        }

        public static boolean isGolemInteract() {
            return golemInteract;
        }

        public static boolean isEnableHwyla() {
            return enableHwyla;
        }
    }

    @Section("Tether")
    public static class Tether {
        @Section.Comment("Enables tether system preventing golems from wandering too far")
        private static boolean tetherEnabled = true;
        @Section.Comment("Enables whether tempting a golem away with an apple will change its tether")
        private static boolean temptResetsTether = true;
        @Section.Comment("The maximum range away from its tether the golem should wander")
        private static int tetherMaxRange = 36;
        @Section.Comment("The min distance to the tether the golem should return to when it wanders too far")
        private static int tetherMinRange = 0;

        public static boolean isTetherEnabled() {
            return tetherEnabled;
        }

        public static boolean doesTemptResetTether() {
            return temptResetsTether;
        }

        public static int getTetherMaxRange() {
            return tetherMaxRange;
        }

        public static int getTetherMinRange() {
            return tetherMinRange;
        }
    }

    @Section("Health")
    public static class Health {
        @Section.Comment("Golem lifespan in ticks. Set to -1 for infinite")
        private static int lifespan = 168000;
        @Section.Comment("Golem hunger in ticks. Set to -1 for infinite")
        private static int hunger = 48000;
        @Section.Comment("Enables lifespan penalty in the rain (-1 extra / tick)")
        private static boolean rainPenalty = true;
        @Section.Comment("Enables lifespan penalty in water (-1 extra / tick)")
        private static boolean waterPenalty = true;
        @Section.Comment("Enables lifespan heavy penalty, such as carrying a gourd block (-1 extra / tick)")
        private static boolean heavyPenalty = true;

        public static int getLifespan() {
            return lifespan;
        }

        public static int getHunger() {
            return hunger;
        }

        public static boolean isRainPenalty() {
            return rainPenalty;
        }

        public static boolean isWaterPenalty() {
            return waterPenalty;
        }

        public static boolean isHeavyPenalty() {
            return heavyPenalty;
        }
    }

}
