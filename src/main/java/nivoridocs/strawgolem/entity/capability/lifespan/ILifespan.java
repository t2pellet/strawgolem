package nivoridocs.strawgolem.entity.capability.lifespan;

public interface ILifespan {
	
	void update();
	
	boolean isOver();
	
	int get();
	
	void set(int tickLeft);

}
