package com.t2pellet.strawgolem.entity.capabilities.decay;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.tlib.common.entity.capability.AbstractCapability;
import com.t2pellet.tlib.common.entity.capability.ICapabilityHaver;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Random;


class DecayImpl<E extends Entity & ICapabilityHaver> extends AbstractCapability<E> implements Decay {
    private DecayState state = DecayState.NEW;
    private int ticksSinceLastDecay = 0;
    private final Random rand;

    protected DecayImpl(E e) {
        super(e);
        this.rand = new Random();
    }


    @Override
    public void decay() {
        if (!StrawgolemConfig.Lifespan.enabled.get()) return;

        int ticksToDecay = StrawgolemConfig.Lifespan.ticksToDecayCheck.get();
        int decayChance = StrawgolemConfig.Lifespan.decayChance.get();
        if (ticksSinceLastDecay == ticksToDecay && rand.nextInt(decayChance) == 0) {
            state = DecayState.fromValue(state.getValue() + 1);
            ticksSinceLastDecay = 0;
            synchronize();
        } else ++ticksSinceLastDecay;

        if (state == null) entity.kill();
    }

    @Override
    public boolean repair() {
        if (state == DecayState.NEW) return false;

        if (rand.nextInt(StrawgolemConfig.Lifespan.repairChance.get()) == 0) {
            state = DecayState.fromValue(state.getValue() - 1);
        }

        synchronize();
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
        CompoundTag compoundTag = (CompoundTag) tag;
        ticksSinceLastDecay = compoundTag.getInt("ticksSinceLastDecay");
        state = DecayState.fromValue(compoundTag.getInt("decayState"));
    }
}
