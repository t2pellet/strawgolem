package com.commodorethrawn.strawgolem.entity.capability.hunger;

import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;

class HungerImpl implements Hunger {

    private int hunger;

    public HungerImpl() {
        hunger = StrawgolemConfig.Health.getHunger();
    }

    @Override
    public int get() {
        return hunger;
    }

    @Override
    public void update() {
        if (hunger > 0) --hunger;
    }

    @Override
    public void set(int hunger) {
        this.hunger = hunger;
    }

    @Override
    public boolean isHungry() {
        return hunger == 0;
    }

    @Override
    public Tag writeTag() {
        return IntTag.of(hunger);
    }

    @Override
    public void readTag(Tag tag) {
        IntTag intTag = (IntTag) tag;
        hunger = intTag.getInt();
    }
}
