package com.commodorethrawn.strawgolem.config;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.util.IniFile;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Optional;

public class StrawgolemConfig {

    static final String FILTER_MODE_WHITELIST = "whitelist";
    static final String FILTER_MODE_BLACKLIST = "blacklist";
    static final File configFile;
    static final IniFile ini;
    static final IniFile.Section harvesting;
    static final IniFile.Section miscellaneous;
    static final IniFile.Section tether;
    static final IniFile.Section lifespan;

    private static final IniFile.Section version;
    private static final String modVersion;

    static {
        String configPath = FabricLoader.getInstance().getConfigDir().toString()+ "\\strawgolem.ini";
        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer(Strawgolem.MODID);
        if (modContainerOptional.isPresent()) modVersion = modContainerOptional.get().getMetadata().getVersion().getFriendlyString();
        else modVersion = "1.9";
        configFile = new File(configPath);
        ini = IniFile.newInstance();
        try {
            if (configFile.createNewFile()) createConfig();
            else ini.load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        harvesting = ini.getSection("Harvesting");
        miscellaneous = ini.getSection("Miscellaneous");
        tether = ini.getSection("Tether");
        lifespan = ini.getSection("Lifespan");
        version = ini.getSection("Version");
        if (!version.get("version", String.class).equals(modVersion)) {
            try {
                ini.clear();
                createConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static void createConfig() throws IOException {
        PrintWriter writer = new PrintWriter(configFile);
        writer.print("");
        writer.close();

        IniFile.Section harvestingSection = ini.addSection("Harvesting");
        harvestingSection.add("replantEnabled", true);
//        harvestingSection.putComment("replantEnabled", "Enables golems replanting crops");
        harvestingSection.add("deliveryEnabled", true);
//        harvestingSection.putComment("deliveryEnabled", "Enables golems delivering crops to a chest");
        harvestingSection.add("searchRangeHorizontal", 24);
//        harvestingSection.putComment("searchRangeHorizontal", "The horizontal range of crops golems can detect");
        harvestingSection.add("searchRangeVertical", 4);
//        harvestingSection.putComment("searchRangeVertical", "The vertical range of crops golems can detect");
        harvestingSection.add("filterMode", FILTER_MODE_BLACKLIST);
//        harvestingSection.putComment("filterMode", "The golem filtration mode. Either 'whitelist' or 'blacklist'");
        harvestingSection.add("whitelist", new ArrayList<String>());
        harvestingSection.add("blacklist", new ArrayList<String>());

        IniFile.Section miscSection = ini.addSection("Miscellaneous");
        miscSection.add("soundsEnabled", true);
//        miscSection.putComment("soundsEnabled", "Enables golem sounds");
        miscSection.add("shiverEnabled", true);
//        miscSection.putComment("shiverEnabled", "Enables golems shivering in the cold or rain");
        miscSection.add("golemInteract", true);
//        miscSection.putComment("golemInteract", "Enables Iron Golems periodically picking up straw golems");
        miscSection.add("enableHwyla", true);
//        miscSection.putComment("enableHwyla", "Enables HWYLA Compat");

        IniFile.Section tetherSection = ini.addSection("Tether");
        tetherSection.add("tetherEnabled", true);
////        tetherSection.putComment("tetherEnabled", "Enables tether system, preventing golem from wandering too far");
        tetherSection.add("temptResetsTether", true);
////        tetherSection.putComment("temptResetsTether", "Enables whether tempting a golem away with an apple will reset its tether");
        tetherSection.add("tetherMaxRange", 24);
////        tetherSection.putComment("tetherMaxRange", "The maximum range away from its tether the golem should wander");

        IniFile.Section lifespanSection = ini.addSection("Lifespan");
        lifespanSection.add("lifespan", 168000);
//        lifespanSection.putComment("lifespan", "Set to -1 for infinite");
        lifespanSection.add("rainPenalty", true);
//        lifespanSection.putComment("rainPenalty", "Enables lifespan going down faster in the rain (+1 / tick)");
        lifespanSection.add("waterPenalty", true);
//        lifespanSection.putComment("waterPenalty", "Enables lifespan going down faster in water (+1 / tick)");
        lifespanSection.add("heavyPenalty", true);
//        lifespanSection.putComment("heavyPenalty", "Enables lifespan going down faster carrying something heavy (+1 / tick)");

        IniFile.Section configSection = ini.addSection("Version");
        configSection.add("version", modVersion);

        ini.store(configFile);
    }

    public enum FilterMatch {
        None,
        Mod,
        Exact,
    }
}
