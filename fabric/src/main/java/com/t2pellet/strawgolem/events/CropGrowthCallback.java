package com.t2pellet.strawgolem.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public interface CropGrowthCallback {

    Event<CropGrowthCallback> EVENT = EventFactory.createArrayBacked(CropGrowthCallback.class,
            listeners -> (world, pos) -> {
                for (CropGrowthCallback listener : listeners) {
                    listener.grow(world, pos);
                }
            });

    void grow(ServerLevel world, BlockPos pos);
}
