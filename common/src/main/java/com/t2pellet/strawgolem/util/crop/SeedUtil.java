package com.t2pellet.strawgolem.util.crop;

import com.t2pellet.strawgolem.compat.Seed;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.state.BlockState;

public class SeedUtil {

    private SeedUtil() {}

    public static boolean isSeed(Item item) {
        if (item instanceof ItemNameBlockItem seed) {
            BlockState state = seed.getBlock().defaultBlockState();
            if (CropUtil.isCrop(state)) return true;
        } else if (item instanceof Seed) {
            return true;
        }
        return false;
    }
}
