package com.commodorethrawn.strawgolem.entity.capability.lifespan;

import com.commodorethrawn.strawgolem.entity.capability.Capability;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;

public interface Lifespan extends Capability {

    /**
     * Updates the lifespan
     */
    void update();

    /**
     * @return whether the golem's lifespan is over
     */
    boolean isOver();

    /**
     * @return the current lifespan
     */
    int get();

    /**
     * Sets the current lifespan to tickLeft
     *
     * @param tickLeft
     */
    void set(int tickLeft);

}
