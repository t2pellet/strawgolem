package com.t2pellet.strawgolem.entity.capabilities.decay;

import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;


class DecayImpl implements Decay {

    private static final int TICKS_TO_DECAY = 12000;
    private static final int DECAY_CHANCE_IN = 5;
    private static final int REPAIR_CHANCE_IN = 3;

    private DecayState state = DecayState.NEW;
    private int ticksSinceLastDecay = 0;
    private final StrawGolem golem;

    public DecayImpl(StrawGolem golem) {
        this.golem = golem;
    }

    @Override
    public void decay() {
        if (ticksSinceLastDecay == TICKS_TO_DECAY && golem.getRandom().nextInt(DECAY_CHANCE_IN) == 0) {
            state = DecayState.fromValue(state.getValue() + 1);
            ticksSinceLastDecay = 0;
        } else ++ticksSinceLastDecay;

        if (state == null) golem.kill();
    }

    @Override
    public boolean repair() {
        if (state == DecayState.NEW) return false;

        if (golem.getRandom().nextInt(REPAIR_CHANCE_IN) == 0) {
            state = DecayState.fromValue(state.getValue() - 1);
        }

        return true;
    }

    @Override
    public DecayState getState() {
        return state;
    }

    @Override
    public Tag writeTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("ticksSinceLastDecay", ticksSinceLastDecay);
        tag.putInt("decayState", state.getValue());
        return tag;
    }

    @Override
    public void readTag(Tag tag) {
        CompoundTag compoundTag = new CompoundTag();
        ticksSinceLastDecay = compoundTag.getInt("ticksSinceLastDecay");
        state = DecayState.fromValue(compoundTag.getInt("decayState"));
    }
}
