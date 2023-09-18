package com.t2pellet.strawgolem.util.crop;

import com.t2pellet.strawgolem.compat.api.Seed;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class SeedUtil {

    public static final TagKey<Item> SEEDS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("strawgolem", "seeds"));

    private SeedUtil() {}

    public static boolean isSeed(ItemStack item) {
        if (item.getItem() instanceof ItemNameBlockItem seed) {
            BlockState state = seed.getBlock().defaultBlockState();
            if (CropUtil.isCrop(state)) return true;
        } else if (item.getItem() instanceof Seed) {
            return true;
        } else return item.is(SEEDS);
        return false;
    }
}
