package com.t2pellet.strawgolem.entity.ai;

import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Monster;

public class GolemFleeGoal extends AvoidEntityGoal<Monster> {
    private static final double SLOW_SPEED = 0.8D;
    private static final double FAST_SPEED = 1.1D;

    private final EntityStrawGolem strawGolem;

    public GolemFleeGoal(EntityStrawGolem entityIn) {
        super(entityIn, Monster.class, 12.0F, SLOW_SPEED, FAST_SPEED);
        strawGolem = entityIn;
    }

    @Override
    public boolean canUse() {
        return !strawGolem.getHunger().isHungry() && super.canUse();
    }

    @Override
    public void start() {
        this.pathNav.moveTo(this.path, SLOW_SPEED * strawGolem.getHunger().getPercentage());
        if (StrawgolemConfig.Miscellaneous.isSoundsEnabled())
            strawGolem.playSound(EntityStrawGolem.GOLEM_SCARED, 1.0F, 1.0F);
    }

    @Override
    public void tick() {
        if (this.mob.distanceToSqr(this.toAvoid) < 49.0D) {
            this.mob.getNavigation().setSpeedModifier(FAST_SPEED * strawGolem.getHunger().getPercentage());
        } else {
            this.mob.getNavigation().setSpeedModifier(SLOW_SPEED * strawGolem.getHunger().getPercentage());
        }

    }

}
