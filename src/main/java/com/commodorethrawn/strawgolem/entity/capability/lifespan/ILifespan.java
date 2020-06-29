package com.commodorethrawn.strawgolem.entity.capability.lifespan;

public interface ILifespan {

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
	 * @param tickLeft
	 */
	void set(int tickLeft);

}
