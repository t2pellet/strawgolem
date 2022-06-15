package com.t2pellet.strawgolem.entity.capability.lifespan;

import com.t2pellet.strawgolem.entity.capability.Capability;

public interface Lifespan extends Capability {

    static Lifespan getInstance() {
        return new LifespanImpl();
    }

    /**
     * Updates the lifespan
     */
    void shrink();

    /**
     * Updates the lifespan by given # of ticks
     *
     * @param ticks the given # of ticks
     */
    void shrink(int ticks);

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
