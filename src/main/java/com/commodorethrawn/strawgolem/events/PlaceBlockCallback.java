package com.commodorethrawn.strawgolem.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface PlaceBlockCallback {
    Event<PlaceBlockCallback> EVENT = EventFactory.createArrayBacked(PlaceBlockCallback.class,
            listeners -> (world, pos) -> {
                for (PlaceBlockCallback listener : listeners) {
                    ActionResult result = listener.interact(world, pos);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult interact(World world, BlockPos pos);
}
