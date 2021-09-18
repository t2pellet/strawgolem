package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.HostileEntity;

public class GolemFleeGoal extends FleeEntityGoal<HostileEntity> {

    private static final double slowSpeed = 0.7D;
    private static final double fastSpeed = 1.0D;

    private final EntityStrawGolem strawGolem;

    public GolemFleeGoal(EntityStrawGolem entityIn) {
        super(entityIn, HostileEntity.class, 12.0F, slowSpeed, fastSpeed);
        strawGolem = entityIn;
    }

    @Override
    public boolean canStart() {
        return !strawGolem.getHunger().isHungry() && super.canStart();
    }

    @Override
    public void start() {
        this.fleeingEntityNavigation.startMovingAlong(this.fleePath, slowSpeed * strawGolem.getHunger().getPercentage());
        if (StrawgolemConfig.Miscellaneous.isSoundsEnabled()) strawGolem.playSound(EntityStrawGolem.GOLEM_SCARED, 1.0F, 1.0F);
    }

    @Override
    public void tick() {
        if (this.mob.squaredDistanceTo(this.targetEntity) < 49.0D) {
            this.mob.getNavigation().setSpeed(fastSpeed * strawGolem.getHunger().getPercentage());
        } else {
            this.mob.getNavigation().setSpeed(slowSpeed * strawGolem.getHunger().getPercentage());
        }

    }

}
