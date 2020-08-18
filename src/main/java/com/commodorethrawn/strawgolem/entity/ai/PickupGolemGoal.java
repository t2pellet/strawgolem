package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.IronGolemEntity;

import java.util.EnumSet;

/**
 * Task responsible for iron golems picking up straw golems
 */
public class PickupGolemGoal extends Goal {

    private static final EntityPredicate predicate = (new EntityPredicate()).setDistance(7.5D).allowFriendlyFire().setLineOfSiteRequired();
    private final double speed;
    private final IronGolemEntity ironGolem;
    private EntityStrawGolem strawGolem;
    private int pickupTime;

    public PickupGolemGoal(IronGolemEntity creature, double speedIn) {
        ironGolem = creature;
        this.speed = speedIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean shouldExecute() {
        if (this.ironGolem.world.isDaytime()
                && this.ironGolem.getRNG().nextInt(6000) == 0
                && this.ironGolem.getPassengers().size() == 0) {
            strawGolem = ironGolem.world.getClosestEntityWithinAABB(EntityStrawGolem.class, predicate, this.ironGolem, this.ironGolem.getPosX(), this.ironGolem.getPosY(), this.ironGolem.getPosZ(), this.ironGolem.getBoundingBox().grow(7.5D, 2.0D, 7.5D));
            return strawGolem != null && strawGolem.isHandEmpty() && !strawGolem.isPassenger();
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return strawGolem.isAlive() && strawGolem.isHandEmpty() && ironGolem.world.isDaytime() && pickupTime > 0;
    }

    @Override
    public void startExecuting() {
        pickupTime = ironGolem.getRNG().nextInt(101) + 80;

    }

    @Override
    public void tick() {
        if (strawGolem.getRidingEntity() != ironGolem && strawGolem.getDistance(ironGolem) > 2.2D) {
            strawGolem.getLookController().setLookPositionWithEntity(strawGolem, 0.0F, 0.0F);
            ironGolem.getNavigator().tryMoveToEntityLiving(strawGolem, speed);
        } else {
            if (!strawGolem.isPassenger()) {
                strawGolem.startRiding(ironGolem);
                strawGolem.setInvulnerable(true);
            }
            strawGolem.getLookController().setLookPositionWithEntity(ironGolem, 10.0F, strawGolem.getVerticalFaceSpeed());
            ironGolem.getNavigator().clearPath();
            if (pickupTime == 1) {
                strawGolem.stopRiding();
                strawGolem.setInvulnerable(false);
            }
            --pickupTime;
        }
    }

    @Override
    public void resetTask() {
        strawGolem.stopRiding();
    }
}
