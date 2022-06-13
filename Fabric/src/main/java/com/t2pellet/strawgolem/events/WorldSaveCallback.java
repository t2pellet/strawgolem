package com.t2pellet.strawgolem.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerLevel;

public interface WorldSaveCallback {

    Event<WorldSaveCallback> EVENT = EventFactory.createArrayBacked(WorldSaveCallback.class,
            listeners -> (level) -> {
                for (WorldSaveCallback listener : listeners) {
                    listener.save(level);
                }
            });

    void save(ServerLevel level);
}
