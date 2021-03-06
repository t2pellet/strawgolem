package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.GolemEntity;

import java.util.EnumSet;

/**
 * Task responsible for iron golems picking up straw golems
 */
public class PickupGolemGoal extends Goal {

    private static final TargetPredicate predicate = new TargetPredicate().setPredicate(e -> e instanceof EntityStrawGolem).setBaseMaxDistance(10.0D).includeTeammates();
    private final double speed;
    private final GolemEntity golemEntity;
    private EntityStrawGolem strawGolem;
    private int pickupTime;
    private int cooldownTime;

    public PickupGolemGoal(GolemEntity creature, double speedIn) {
        golemEntity = creature;
        this.speed = speedIn;
        this.cooldownTime = 0;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (this.golemEntity.world.isDay()
                && this.golemEntity.getRandom().nextInt(6000) == 0
                && !this.golemEntity.hasVehicle()) {
            if (cooldownTime > 0) {
                --cooldownTime;
                return false;
            }
            strawGolem = golemEntity.world.getClosestEntity(EntityStrawGolem.class, predicate, this.golemEntity, this.golemEntity.getX(), this.golemEntity.getY(), this.golemEntity.getZ(), this.golemEntity.getBoundingBox().expand(10.0D, 4.0D, 10.0D));
            return strawGolem != null && strawGolem.isHandEmpty() && !strawGolem.hasVehicle();
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        long attackedDelta = golemEntity.getEntityWorld().getTime() - golemEntity.getLastAttackedTime();
        if (attackedDelta < 10) return false;
        return strawGolem.isAlive() && strawGolem.isHandEmpty() && golemEntity.world.isDay() && pickupTime > 0;
    }

    @Override
    public void start() {
        pickupTime = golemEntity.getRandom().nextInt(101) + 80;
    }

    @Override
    public void tick() {
        golemEntity.getLookControl().lookAt(strawGolem, 0.0F, 0.0F);
        strawGolem.getLookControl().lookAt(golemEntity, 0.0F, 0.0F);
        if (strawGolem.hasVehicle()) {
            golemEntity.getNavigation().recalculatePath();
            if (pickupTime < 1) stop();
            --pickupTime;
        } else {
            if (strawGolem.distanceTo(golemEntity) > 2.1D) golemEntity.getNavigation().startMovingTo(strawGolem, speed);
            else {
                strawGolem.startRiding(golemEntity);
                strawGolem.setInvulnerable(true);
            }
        }
    }

    @Override
    public void stop() {
        strawGolem.stopRiding();
        strawGolem.setInvulnerable(false);
        this.cooldownTime = 2400;
    }
}
