package com.t2pellet.strawgolem.events;

import com.t2pellet.strawgolem.crop.CropHandler;
import com.t2pellet.strawgolem.crop.CropValidator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

/**
 * Handles crop growth events, adds them to the crop handler
 */
public class CropGrowthHandler {

    private CropGrowthHandler() {}

    public static void onCropGrowth(Level world, BlockPos cropPos) {
        if (!world.isClientSide()) {
            CropHandler.INSTANCE.addCrop(world, cropPos);
        }
    }
}
