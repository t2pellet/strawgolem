package com.commodorethrawn.strawgolem.events;

import com.commodorethrawn.strawgolem.crop.CropHandler;
import com.commodorethrawn.strawgolem.crop.CropValidator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

/**
 * Handles crop growth events, adds them to the crop handler
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
