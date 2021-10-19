package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.WanderAroundPointOfInterestGoal;
import net.minecraft.util.math.BlockPos;

public class GolemWanderGoal extends WanderAroundGoal {

    private static final double speed = 0.5D;

    private final EntityStrawGolem strawGolem;

    public GolemWanderGoal(EntityStrawGolem creature) {
        super(creature, speed);
        strawGolem = creature;
    }

    @Override
    public boolean canStart() {
        return strawGolem.isHandEmpty()
                && !strawGolem.isHarvesting()
                && !strawGolem.getHunger().isHungry()
                && super.canStart();
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, speed * strawGolem.getHunger().getPercentage());
    }
}
