package com.commodorethrawn.strawgolem.entity.capability.lifespan;

import com.commodorethrawn.strawgolem.config.ConfigHelper;

public class Lifespan implements ILifespan {
    private int tickLeft;

    public Lifespan() {
        this.tickLeft = ConfigHelper.getLifespan();
    }

    @Override
    public void update() {
        if (tickLeft > 0)
            tickLeft--;
    }

    @Override
    public boolean isOver() {
        return tickLeft == 0;
    }

    @Override
    public int get() {
        return tickLeft;
    }

    @Override
    public void set(int tickLeft) {
        this.tickLeft = tickLeft;
    }

}
