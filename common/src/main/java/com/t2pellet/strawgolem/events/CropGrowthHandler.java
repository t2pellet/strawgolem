package com.t2pellet.strawgolem.events;

import com.t2pellet.strawgolem.world.WorldCrops;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Handles crop growth events, adds them to the crop handler
 */
public class CropGrowthHandler {

    private CropGrowthHandler() {
    }

    public static void onCropGrowth(ServerLevel world, BlockPos cropPos) {
        System.out.println("crop grown at pos: " + cropPos.toShortString());
        WorldCrops.of(world).add(cropPos);
    }
}
