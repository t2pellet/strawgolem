package com.t2pellet.strawgolem.entity.ai;

import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Monster;

import static com.t2pellet.strawgolem.registry.CommonRegistry.Sounds.GOLEM_SCARED;

public class GolemFleeGoal extends AvoidEntityGoal<Monster> {
    private static final double SLOW_SPEED = 0.8D;
    private static final double FAST_SPEED = 1.1D;

    private final StrawGolem strawGolem;

    public GolemFleeGoal(StrawGolem entityIn) {
        super(entityIn, Monster.class, 12.0F, SLOW_SPEED, FAST_SPEED);
        strawGolem = entityIn;
    }

    @Override
    public boolean canUse() {
        return !strawGolem.getHunger().isHungry() && super.canUse();
    }

    @Override
    public void start() {
        this.pathNav.moveTo(this.path, SLOW_SPEED);
        if (StrawgolemConfig.Miscellaneous.isSoundsEnabled())
            strawGolem.playSound(GOLEM_SCARED, 1.0F, 1.0F);
    }

    @Override
    public void tick() {
        if (this.mob.distanceToSqr(this.toAvoid) < 49.0D) {
            this.mob.getNavigation().setSpeedModifier(FAST_SPEED);
        } else {
            this.mob.getNavigation().setSpeedModifier(SLOW_SPEED);
        }

    }

}
