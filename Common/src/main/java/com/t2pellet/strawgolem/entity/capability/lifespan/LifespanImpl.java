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
    public void shrink() {
        if (tickLeft > 0)
            tickLeft--;
    }

    @Override
    public void shrink(int ticks) {
        if (ticks > tickLeft && tickLeft > 0) {
            tickLeft = 0;
        } else {
            tickLeft -= ticks;
        }
    }

    @Override
    public boolean isOver() {
        return tickLeft == 0 && StrawgolemConfig.Health.getLifespan() > 0;
    }

    @Override
    public int get() {
        return tickLeft;
    }

    @Override
    public float getPercentage() {
        return StrawgolemConfig.Health.getLifespan() < 0 ? 1 : (float) tickLeft / StrawgolemConfig.Health.getLifespan();
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
