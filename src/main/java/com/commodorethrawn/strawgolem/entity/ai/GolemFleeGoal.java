package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.HostileEntity;

public class GolemFleeGoal extends FleeEntityGoal<HostileEntity> {

    private static final double SLOW_SPEED = 0.8D;
    private static final double FAST_SPEED = 1.1D;

    private final EntityStrawGolem strawGolem;

    public GolemFleeGoal(EntityStrawGolem entityIn) {
        super(entityIn, HostileEntity.class, 12.0F, SLOW_SPEED, FAST_SPEED);
        strawGolem = entityIn;
    }

    @Override
    public boolean canStart() {
        return !strawGolem.getHunger().isHungry() && super.canStart();
    }

    @Override
    public void start() {
        this.fleeingEntityNavigation.startMovingAlong(this.fleePath, SLOW_SPEED * strawGolem.getHunger().getPercentage());
        if (StrawgolemConfig.Miscellaneous.isSoundsEnabled()) strawGolem.playSound(EntityStrawGolem.GOLEM_SCARED, 1.0F, 1.0F);
    }

    @Override
    public void tick() {
        if (this.mob.squaredDistanceTo(this.targetEntity) < 49.0D) {
            this.mob.getNavigation().setSpeed(FAST_SPEED * strawGolem.getHunger().getPercentage());
        } else {
            this.mob.getNavigation().setSpeed(SLOW_SPEED * strawGolem.getHunger().getPercentage());
        }

    }

}
