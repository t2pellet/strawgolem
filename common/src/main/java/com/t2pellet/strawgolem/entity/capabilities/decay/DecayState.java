package com.t2pellet.strawgolem.entity.capabilities.decay;

import com.t2pellet.strawgolem.StrawgolemConfig;

public enum DecayState {
    NEW("strawgolem.health.new", 0),
    OLD("strawgolem.health.old", 1),
    WITHERED("strawgolem.health.withered", 2),
    DYING("strawgolem.health.dying", 3);

    private final String description;
    private final int value;
    DecayState(String description, int value) {
        this.description = description;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public float getHealth() {
        int decayStates = DecayState.values().length;
        float healthRatio = (float) (decayStates - value) / decayStates;
        float healthValue = healthRatio * StrawgolemConfig.Lifespan.baseHealth.get();
        return (float) Math.round(healthValue * 2) / 2;
    }

    public String getDescription() {
        return description;
    }

    static DecayState fromValue(int value) {
        DecayState[] states = DecayState.values();
        for (DecayState state : states) {
            if (state.value == value) return state;
        }
        return null;
    }

    static DecayState fromHealth(float health) {
        DecayState[] states = DecayState.values();
        for (int i = states.length - 1; i > 0; --i) {
            if (health <= states[i].getHealth()) return states[i];
        }
        return NEW;
    }
}
