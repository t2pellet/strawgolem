package com.t2pellet.strawgolem.entity.ai;

import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

public class GolemLookRandomlyGoal extends RandomLookAroundGoal {

    private final StrawGolem strawGolem;

    public GolemLookRandomlyGoal(StrawGolem entitylivingIn) {
        super(entitylivingIn);
        strawGolem = entitylivingIn;
    }

    @Override
    public boolean canUse() {
        return strawGolem.getVehicle() == null
                && !strawGolem.isHarvesting()
                && strawGolem.isHandEmpty()
                && !strawGolem.getHunger().isHungry()
                && super.canUse();
    }
}
