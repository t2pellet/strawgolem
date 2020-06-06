package com.commodorethrawn.strawgolem.entity.capability;

public interface ILifespan {
	
	void update();
	
	boolean isOver();
	
	int get();
	
	void set(int tickLeft);

}
