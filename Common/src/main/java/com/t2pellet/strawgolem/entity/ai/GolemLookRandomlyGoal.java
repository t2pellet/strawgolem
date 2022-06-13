package com.t2pellet.strawgolem.entity.ai;

import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

public class GolemLookRandomlyGoal extends RandomLookAroundGoal {

    private final EntityStrawGolem strawGolem;

    public GolemLookRandomlyGoal(EntityStrawGolem entitylivingIn) {
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
