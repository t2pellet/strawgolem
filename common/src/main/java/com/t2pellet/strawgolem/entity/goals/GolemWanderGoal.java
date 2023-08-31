package com.t2pellet.strawgolem.entity.goals;

import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

public class GolemWanderGoal extends WaterAvoidingRandomStrollGoal {

    private final StrawGolem golem;

    public GolemWanderGoal(StrawGolem golem) {
        super(golem, 0.5F);
        this.golem = golem;
    }

    @Override
    public boolean canUse() {
        return !golem.getHarvester().isHarvesting() && super.canUse();
    }
}
