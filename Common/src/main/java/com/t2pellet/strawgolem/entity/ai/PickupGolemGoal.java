package com.t2pellet.strawgolem.entity.ai;

import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.AbstractGolem;

import java.util.EnumSet;

/**
 * Task responsible for iron golems picking up straw golems
 */
public class PickupGolemGoal extends Goal {

    private static final TargetingConditions predicate = TargetingConditions.forNonCombat().selector(e -> e instanceof StrawGolem).range(10.0D);
    private final double speed;
    private final AbstractGolem golemEntity;
    private StrawGolem strawGolem;
    private int pickupTime;
    private int cooldownTime;

    public PickupGolemGoal(AbstractGolem creature, double speedIn) {
        golemEntity = creature;
        this.speed = speedIn;
        this.cooldownTime = 0;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (StrawgolemConfig.Miscellaneous.isGolemInteract()
                && this.golemEntity.level.isDay()
                && this.golemEntity.getRandom().nextInt(3000) == 0
                && this.golemEntity.getVehicle() == null) {
            if (cooldownTime > 0) {
                --cooldownTime;
                return false;
            }
            strawGolem = golemEntity.level.getNearestEntity(StrawGolem.class, predicate, this.golemEntity, this.golemEntity.getX(), this.golemEntity.getY(), this.golemEntity.getZ(), this.golemEntity.getBoundingBox().inflate(10.0D, 4.0D, 10.0D));
            return strawGolem != null && strawGolem.isHandEmpty() && strawGolem.getVehicle() == null;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        long attackedDelta = golemEntity.level.getGameTime() - golemEntity.getLastHurtMobTimestamp();
        if (attackedDelta < 10) return false;
        return strawGolem.isAlive() && strawGolem.isHandEmpty() && golemEntity.level.isDay() && pickupTime > 0;
    }

    @Override
    public void start() {
        pickupTime = golemEntity.getRandom().nextInt(101) + 80;
    }

    @Override
    public void tick() {
        golemEntity.getLookControl().setLookAt(strawGolem, 0.0F, 0.0F);
        strawGolem.getLookControl().setLookAt(golemEntity, 0.0F, 0.0F);
        if (strawGolem.getVehicle() != null) {
            golemEntity.getNavigation().recomputePath();
            if (pickupTime < 1) stop();
            --pickupTime;
        } else {
            if (strawGolem.distanceTo(golemEntity) > 2.1D) golemEntity.getNavigation().moveTo(strawGolem, speed);
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
