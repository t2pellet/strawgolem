package nivoridocs.strawgolem;

import net.minecraft.block.Block;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.PostConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Strawgolem.MODID)
@Config.LangKey("strawgolem.config.title")
@EventBusSubscriber
public class StrawgolemConfig {
	
	@Config.Comment("Allow the straw golems to replant a crop when they harvest it.")
	public static boolean replantEnabled = false;
	
	@Config.Comment("Set the lifespan, in tick, of new created straw golems. Set -1 for infinite.")
	@Config.RangeInt(min = -1, max = Integer.MAX_VALUE)
	public static int lifespan = 168000;
	
	public static boolean isReplantEnabled() {
		return replantEnabled;
	}
	
	public static int getLifespan() {
		return lifespan;
	}
	
	@SubscribeEvent
	public static void onConfigChange(PostConfigChangedEvent event) {
		if (event.getModID().equals(Strawgolem.MODID))
			ConfigManager.sync(Strawgolem.MODID, Config.Type.INSTANCE);
	}
	
	private StrawgolemConfig() {}

	@Config.Comment({
			"Sets the method for applying harvest filters.  Note that only the most specific match will be taken into consideration.",
			"If a crop's mod appears in the whitelist, but the crop itself is in the blacklist, the crop will be banned.",
			"Likewise if a crop's mod appears in the blacklist, but the crop itself is in the whitelist, the crop will be allowed.",
			"\"none\": allow all crops to be harvested (default).",
			"\"whitelist\": will deny crops from being harvested unless the most specific match is in the whitelist.",
			"\"blacklist\": will allows crops to be harvested unless the most specific match is in the blacklist."
	})
	public static String filterMode = "none";

	private static final String FILTER_MODE_WHITELIST = "whitelist";
	private static final String FILTER_MODE_BLACKLIST = "blacklist";

	@Config.Comment("Whitelist Filter")
	public static String[] whitelist = new String[0];

	@Config.Comment("Blacklist Filter")
	public static String[] blacklist = new String[0];

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

	public static FilterMatch blockMatchesFilter(Block block, String[] filter) {
		FilterMatch bestMatch = FilterMatch.None;

		for (String s : filter) {
			String[] elements = s.split(":");

			if (elements.length == 0)
				continue;

			if (elements.length == 1 && block.getRegistryName().getResourceDomain().equals(elements[0]))
			{
				bestMatch = FilterMatch.Mod;
				continue;
			}

			if (elements.length >= 2 && block.getRegistryName().getResourcePath().equals(elements[1]))
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
