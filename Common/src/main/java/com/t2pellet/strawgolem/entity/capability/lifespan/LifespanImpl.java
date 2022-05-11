package com.t2pellet.strawgolem.entity.capability.lifespan;

import com.t2pellet.strawgolem.config.StrawgolemConfig;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;

class LifespanImpl implements Lifespan {
    private int tickLeft;

    public LifespanImpl() {
        this.tickLeft = StrawgolemConfig.Health.getLifespan();
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

    @Override
    public Tag writeTag() {
        return IntTag.valueOf(tickLeft);
    }

    @Override
    public void readTag(Tag tag) {
        IntTag NbtInt = (IntTag) tag;
        tickLeft = NbtInt.getAsInt();
    }
}
