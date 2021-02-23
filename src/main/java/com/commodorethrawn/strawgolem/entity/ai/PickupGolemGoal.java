package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.IronGolemEntity;

import java.util.EnumSet;

/**
 * Task responsible for iron golems picking up straw golems
 */
public class PickupGolemGoal extends Goal {

    private static final TargetPredicate predicate = new TargetPredicate().setPredicate(e -> e instanceof EntityStrawGolem).setBaseMaxDistance(10.0D).includeTeammates();
    private final double speed;
    private final IronGolemEntity ironGolem;
    private EntityStrawGolem strawGolem;
    private int pickupTime;
    private int cooldownTime;

    public PickupGolemGoal(IronGolemEntity creature, double speedIn) {
        ironGolem = creature;
        this.speed = speedIn;
        this.cooldownTime = 0;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (this.ironGolem.world.isDay()
                && this.ironGolem.getRandom().nextInt(6000) == 0
                && this.ironGolem.getPassengerList().isEmpty()) {
            if (cooldownTime > 0) {
                --cooldownTime;
                return false;
            }
            strawGolem = ironGolem.world.getClosestEntity(EntityStrawGolem.class, predicate, this.ironGolem, this.ironGolem.getX(), this.ironGolem.getY(), this.ironGolem.getZ(), this.ironGolem.getBoundingBox().expand(10.0D, 4.0D, 10.0D));
            return strawGolem != null && strawGolem.isHandEmpty() && !strawGolem.hasVehicle();
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        long attackedDelta = ironGolem.getEntityWorld().getTime() - ironGolem.getLastAttackedTime();
        if (attackedDelta < 10) return false;
        return strawGolem.isAlive() && strawGolem.isHandEmpty() && ironGolem.world.isDay() && pickupTime > 0;
    }

    @Override
    public void start() {
        pickupTime = ironGolem.getRandom().nextInt(101) + 80;
    }

    @Override
    public void tick() {
        if (!strawGolem.hasVehicle() && strawGolem.distanceTo(ironGolem) > 2.2D) {
            strawGolem.getLookControl().lookAt(strawGolem, 0.0F, 0.0F);
            ironGolem.getNavigation().startMovingTo(strawGolem, speed);
        } else {
            if (!strawGolem.hasVehicle()) {
                strawGolem.startRiding(ironGolem);
                strawGolem.setInvulnerable(true);
            }
            strawGolem.lookAtEntity(ironGolem, 360, 360);
            ironGolem.getNavigation().recalculatePath();
            if (pickupTime < 1) {
                strawGolem.stopRiding();
                strawGolem.setInvulnerable(false);
            }
            --pickupTime;
        }
    }

    @Override
    public void stop() {
        strawGolem.stopRiding();
        strawGolem.setInvulnerable(false);
        this.cooldownTime = 2400;
    }
}
