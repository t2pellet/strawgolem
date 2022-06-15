package com.t2pellet.strawgolem.entity.ai;

import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

public class GolemWanderGoal extends WaterAvoidingRandomStrollGoal {
    private static final double speed = 0.5D;

    private final EntityStrawGolem strawGolem;

    public GolemWanderGoal(EntityStrawGolem creature) {
        super(creature, speed);
        strawGolem = creature;
    }

    @Override
    public boolean canUse() {
        return strawGolem.isHandEmpty()
                && strawGolem.getVehicle() == null
                && !strawGolem.isHarvesting()
                && !strawGolem.getHunger().isHungry()
                && super.canUse();
    }
}
