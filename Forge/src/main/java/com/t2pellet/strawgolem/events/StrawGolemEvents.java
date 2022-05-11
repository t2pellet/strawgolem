package com.t2pellet.strawgolem.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

public class StrawGolemEvents {

    public static class CropGrowthEvent extends BlockEvent {

        public CropGrowthEvent(LevelAccessor world, BlockPos pos, BlockState state) {
            super(world, pos, state);
        }
    }

    public static boolean onCropGrowth(LevelAccessor world, BlockPos pos, BlockState state) {
        CropGrowthEvent event = new CropGrowthEvent(world, pos, state);
        return MinecraftForge.EVENT_BUS.post(event);
    }
}
