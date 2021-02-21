package com.commodorethrawn.strawgolem.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public interface CropGrowthCallback {

    Event<CropGrowthCallback> EVENT = EventFactory.createArrayBacked(CropGrowthCallback.class,
            listeners -> (world, pos) -> {
                for (CropGrowthCallback listener : listeners) {
                    listener.grow(world, pos);
                }
            });

    void grow(WorldAccess world, BlockPos pos);
}
