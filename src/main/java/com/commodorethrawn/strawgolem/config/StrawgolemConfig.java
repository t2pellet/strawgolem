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

    public static boolean replantEnabled;
    public static boolean deliveryEnabled;
	public static int lifespan;
	public static String filterMode;
	public static List<? extends String> whitelist;
	public static List<? extends String> blacklist;

    public static boolean isReplantEnabled() {
        return replantEnabled;
    }

    public static boolean isDeliveryEnabled() {
        return deliveryEnabled;
    }

	public static class CommonConfig {
        final ForgeConfigSpec.BooleanValue replantEnabled;
        final ForgeConfigSpec.BooleanValue deliveryEnabled;
		final ForgeConfigSpec.IntValue lifespan;
		final ForgeConfigSpec.ConfigValue<String> filterMode;
		final ForgeConfigSpec.ConfigValue<List<? extends String>> whitelist;
		final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklist;

		CommonConfig(final ForgeConfigSpec.Builder builder) {
            builder.push("Harvesting");
            replantEnabled = builder.comment("Allow the straw golems to replant a crop when they harvest it.").define("replantEnabled", true);
            deliveryEnabled = builder.comment("Allow the straw golem to deliver a crop (requires replantEnabled = true)").define("deliveryEnabled", true);
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
            builder.push("Miscellaneous");
            lifespan = builder.comment("Set the lifespan, in tick, of new created straw golems. Set -1 for infinite.").defineInRange("lifespan", 168000, -1, Integer.MAX_VALUE);
            builder.pop();
		}
	}

	public static int getLifespan() {
		return lifespan;
	}

	public static boolean blockHarvestAllowed(Block block) {
		switch (filterMode)
		{
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

			if (elements.length == 1 && block.getRegistryName().getNamespace().equals(elements[0]))
			{
				bestMatch = FilterMatch.Mod;
				continue;
			}

			if (elements.length >= 2 && block.getRegistryName().getNamespace().equals(elements[0]) && block.getRegistryName().getPath().equals(elements[1]))
			{
				bestMatch = FilterMatch.Exact;
				break;
			}
		}

		return bestMatch;
	}

	public enum FilterMatch
	{
		None,
		Mod,
		Exact,
	}
}
