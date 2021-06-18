package com.commodorethrawn.strawgolem.events;

import com.commodorethrawn.strawgolem.crop.CropHandler;
import com.commodorethrawn.strawgolem.crop.CropValidator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

/**
 * Handles how golems tend to harvest crops
 * Every time a crop grows, it checks for a golem nearby to harvest. If none is found it is added to a queue
 * Every 100 ticks, it goes through the queue checking for golems to harvest, skipping through already harvested blocks
 * and breaking when it cannot find a golem to harvest
 */
public class CropGrowthHandler {

    private CropGrowthHandler() {}

    public static void onCropGrowth(WorldAccess world, BlockPos cropPos) {
        if (!world.isClient()) {
            if (CropValidator.isGrownCrop(world.getBlockState(cropPos))) {
                CropHandler.INSTANCE.addCrop((World) world, cropPos);
            }
        }
    }
}
