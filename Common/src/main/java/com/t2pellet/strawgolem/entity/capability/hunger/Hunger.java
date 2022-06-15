package com.t2pellet.strawgolem.entity.capability.hunger;

import com.t2pellet.strawgolem.entity.capability.Capability;

public interface Hunger extends Capability {

    static Hunger getInstance() {
        return new HungerImpl();
    }

    int get();

    float getPercentage();

    void shrink();

    void shrink(int ticks);

    void set(int hunger);

    boolean isHungry();

}
