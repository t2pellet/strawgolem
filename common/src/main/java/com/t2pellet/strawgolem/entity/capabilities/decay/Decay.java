package com.t2pellet.strawgolem.entity.capabilities.decay;

import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.tlib.common.entity.capability.Capability;
import com.t2pellet.tlib.common.entity.capability.IModCapabilities;

public interface Decay extends Capability {

    static Decay getInstance(StrawGolem golem) {
        return new DecayImpl(golem);
    }

    enum DecayState {
        NEW(0),
        OLD(1),
        WITHERED(2);

        private final int value;
        DecayState(int value) {
            this.value = value;
        }

        int getValue() {
            return value;
        }

        static DecayState fromValue(int value) {
            DecayState[] states = DecayState.values();
            for (DecayState state : states) {
                if (state.value == value) return state;
            }
            return null;
        }
    }

    void decay();

    boolean repair();

    DecayState getState();

}
