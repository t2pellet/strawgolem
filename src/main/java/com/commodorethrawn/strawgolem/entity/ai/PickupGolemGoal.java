package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.entity.strawgolem.EntityStrawGolem;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.IronGolemEntity;

import java.util.EnumSet;

public class PickupGolemGoal extends Goal {

    private final double speed;
    private EntityStrawGolem strawGolem;
    private final IronGolemEntity ironGolem;
    private static final EntityPredicate predicate = (new EntityPredicate()).setDistance(15.0D).allowFriendlyFire().setLineOfSiteRequired();
    private int pickupTime;

    public PickupGolemGoal(IronGolemEntity creature, double speedIn) {
        ironGolem = creature;
        this.speed = speedIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean shouldExecute() {
        if (!this.ironGolem.world.isDaytime() || ironGolem.getPassengers().size() != 0) {
            return false;
        } else if (this.ironGolem.getRNG().nextInt(2000) != 0) {
            return false;
        } else {
            this.strawGolem = this.ironGolem.world.getClosestEntityWithinAABB(EntityStrawGolem.class, predicate, this.ironGolem, this.ironGolem.getPosX(), this.ironGolem.getPosY(), this.ironGolem.getPosZ(), this.ironGolem.getBoundingBox().grow(15.0D, 4.0D, 15.0D));
            return strawGolem != null && strawGolem.isHandEmpty() && !strawGolem.isPassenger();
        }
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
        strawGolem.getLookController().setLookPositionWithEntity(strawGolem, 0.0F, 0.0F);
        if (strawGolem.getRidingEntity() != ironGolem && strawGolem.getDistance(ironGolem) > 2.2D) {
            ironGolem.getNavigator().tryMoveToEntityLiving(strawGolem, speed);
        } else {
            if (!strawGolem.isPassenger()) {
                strawGolem.startRiding(ironGolem);
                strawGolem.setInvulnerable(true);
            }
            strawGolem.getLookController().setLookPositionWithEntity(ironGolem, 10.0F, strawGolem.getVerticalFaceSpeed());
            if(pickupTime == 1) {
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
