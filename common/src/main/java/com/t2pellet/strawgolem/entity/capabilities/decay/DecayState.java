package com.t2pellet.strawgolem.entity.capabilities.decay;

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
}
