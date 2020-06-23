package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;

public class GolemWanderGoal extends WaterAvoidingRandomWalkingGoal {

    private final EntityStrawGolem strawGolem;

    public GolemWanderGoal(EntityStrawGolem creature, double speedIn) {
        super(creature, speedIn);
        strawGolem = creature;
    }

    @Override
    public boolean shouldExecute() {
        return strawGolem.isHandEmpty() && super.shouldExecute();
    }
}
