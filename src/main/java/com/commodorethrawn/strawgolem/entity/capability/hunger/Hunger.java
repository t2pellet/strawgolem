package com.commodorethrawn.strawgolem.entity.capability.hunger;

import com.commodorethrawn.strawgolem.entity.capability.Capability;

public interface Hunger extends Capability {

    static Hunger getInstance() {
        return new HungerImpl();
    }

    int get();

    float getPercentage();

    void update();

    void set(int hunger);

    boolean isHungry();

}
