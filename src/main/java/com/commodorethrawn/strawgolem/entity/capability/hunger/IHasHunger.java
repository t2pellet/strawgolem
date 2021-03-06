package com.commodorethrawn.strawgolem.entity.capability.hunger;

public interface IHasHunger {

    /**
     * Get the Hunger capability
     * @return the hunger capability
     */
    Hunger getHunger();

    /**
     * Set the entity to the tempted state
     * @param tempted whether its tempted
     */
    void setTempted(boolean tempted);

    /**
     * Return whether the entity is being tempted
     * @return whether the entity is being tempted
     */
    boolean isTempted();
}
