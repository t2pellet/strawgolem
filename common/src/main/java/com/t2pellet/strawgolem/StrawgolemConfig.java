package com.t2pellet.strawgolem;

import com.t2pellet.tlib.config.Config;
import com.t2pellet.tlib.config.property.BoolProperty;
import com.t2pellet.tlib.config.property.IntProperty;
import com.t2pellet.tlib.config.property.ListProperty;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

@Config.ModConfig(comment = "Config for Straw Golem")
public class StrawgolemConfig extends Config {

    public static final StrawgolemConfig INSTANCE = new StrawgolemConfig();

    private StrawgolemConfig() {
        super(Constants.MOD_ID);
    }

    @Section(name = "Harvesting", description = "Harvesting related options")
    public static class Harvesting {
        @Entry(comment = "Range for golems to harvest (and deliver to a chest)")
        public static final IntProperty harvestRange = new IntProperty(24, 8, 48);
        @Entry(comment = "Whether the golem should harvest gourd blocks like pumpkins and melons. Will apply over whitelist, but specific gourd blocks can still be blacklisted.")
        public static final BoolProperty shouldHarvestBlocks = new BoolProperty(true);
        @Entry(comment = "Blacklisted crops. Must use valid resource locations")
        public static final ListProperty<String> blacklist = createResourceLocationList();
        @Entry(comment = "Whether to enable whitelist. Will ignore blacklist")
        public static final BoolProperty enableWhitelist = new BoolProperty(false);
        @Entry(comment = "Whitelisted crops. Only applies if enableWhitelist=true. Must use valid resource locations")
        public static final ListProperty<String>  whitelist = createResourceLocationList();
    }

    @Section(name = "Lifespan", description = "Golem lifespan options")
    public static class Lifespan {
        @Entry(comment = "Whether to enable lifespan/decay feature")
        public static final BoolProperty enabled = new BoolProperty(true);
        @Entry(comment = "Whether being in the rain accelerates decay")
        public static final BoolProperty rainAcceleratesDecay = new BoolProperty(true);
        @Entry(comment = "Whether being in water accelerated decay")
        public static final BoolProperty waterAcceleratesDecay = new BoolProperty(true);
        @Entry(comment = "How many ticks before checking to see if golem decays")
        public static final IntProperty ticksToDecayCheck = new IntProperty(12000);
        @Entry(comment = "Chance to decay on check. Its 1 in whatever number is here. So decayChance=5 means 1/5 chance")
        public static final IntProperty decayChance = new IntProperty(5);
        @Entry(comment = "Chance to repair on wheat usage. Same logic as decayChance")
        public static final IntProperty repairChance = new IntProperty(3);
    }

    @Section(name = "Behaviour", description = "Golem and Mob behaviour options")
    public static class Behaviour {
        @Entry(comment = "Should golems run from raiders")
        public static final BoolProperty golemsRunFromRaiders = new BoolProperty(true);
        @Entry(comment = "Should raiders attack golems")
        public static final BoolProperty raidersAttackGolems = new BoolProperty(true);
        @Entry(comment = "Should golems run from monsters")
        public static final BoolProperty golemsRunFromMonsters = new BoolProperty(true);
        @Entry(comment = "Should monsters attack golems")
        public static final BoolProperty monstersAttackGolems = new BoolProperty(false);
        @Entry(comment = "Should golems run from sheep and cows")
        public static final BoolProperty golemsRunFromLivestock = new BoolProperty(true);
        @Entry(comment = "Should sheep and cows munch on golems")
        public static final BoolProperty livestockEatGolems = new BoolProperty(true);
        @Entry(comment = "Should golems panic when damaged")
        public static final BoolProperty golemsPanicWhenHurt = new BoolProperty(true);
        @Entry(comment = "How far the golem can wander")
        public static final IntProperty golemWanderRange = new IntProperty(24, 8, 48);
    }

    @Section(name = "Visual", description = "Visual related settings")
    public static class Visual {
        @Entry(comment = "Whether the golem should shiver when in an accelerated decay state")
        public static final BoolProperty golemShiversWhenDecayingFast = new BoolProperty(true);
        @Entry(comment = "Whether the golem should shiver when cold")
        public static final BoolProperty golemShiversWhenCold = new BoolProperty(true);
        @Entry(comment = "Whether the golem texture should change as it decays")
        public static final BoolProperty golemDecayingTexture = new BoolProperty(true);
        @Entry(comment = "Show harvesting animations. Requires restart")
        public static final BoolProperty showHarvestAnimations = new BoolProperty(true);
    }

    private static ListProperty<String> createResourceLocationList() {
        return ListProperty.of(new ArrayList<>(), ResourceLocation::isValidResourceLocation);
    }
}
