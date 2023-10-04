package com.t2pellet.strawgolem.entity.capabilities.decay;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.tlib.entity.capability.api.AbstractCapability;
import com.t2pellet.tlib.entity.capability.api.ICapabilityHaver;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Random;


class DecayImpl<E extends LivingEntity & ICapabilityHaver> extends AbstractCapability<E> implements Decay {
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
            if (state == null) {
                entity.kill();
            }
            else {
                updateHealthFromState(false);
                synchronize();
            }
        } else ++ticksSinceLastDecay;

    }

    @Override
    public void setFromHealth() {
        state = DecayState.fromHealth(entity.getHealth());
        updateHealthFromState(false);
        synchronize();
    }

    @Override
    public boolean repair() {
        if (state == DecayState.NEW) return false;

        if (rand.nextInt(StrawgolemConfig.Lifespan.repairChance.get()) == 0) {
            state = DecayState.fromValue(state.getValue() - 1);
            updateHealthFromState(true);
        }

        synchronize();
        return true;
    }

    private void updateHealthFromState(boolean shouldHeal) {
        float health = state.getHealth();
        entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
        if (shouldHeal || entity.getHealth() > health) entity.setHealth(health);
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
