package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.world.entity.ai.goal.PanicGoal;

public class GolemPanicGoal extends PanicGoal {
    public GolemPanicGoal(StrawGolem golem) {
        super(golem, 0.8D);
    }

    @Override
    public boolean canUse() {
        if (StrawgolemConfig.Behaviour.golemsPanicWhenHurt.get()) {
            return super.canUse();
        }
        return false;
    }
}
