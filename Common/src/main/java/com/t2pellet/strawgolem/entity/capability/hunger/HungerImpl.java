package com.t2pellet.strawgolem.entity.capability.hunger;

import com.t2pellet.strawgolem.config.StrawgolemConfig;
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
    public float getPercentage() {
        return (float) hunger / StrawgolemConfig.Health.getHunger();
    }

    @Override
    public void shrink() {
        if (hunger > 0) --hunger;
    }

    @Override
    public void shrink(int ticks) {
        if (ticks > hunger) {
            if (hunger > 0) hunger = 0;
        } else {
            hunger -= ticks;
        }
    }

    @Override
    public void set(int hunger) {
        this.hunger = hunger;
    }

    @Override
    public boolean isHungry() {
        return hunger == 0 && StrawgolemConfig.Health.getHunger() > 0;
    }

    @Override
    public Tag writeTag() {
        return IntTag.valueOf(hunger);
    }

    @Override
    public void readTag(Tag tag) {
        IntTag NbtInt = (IntTag) tag;
        hunger = NbtInt.getAsInt();
    }
}
