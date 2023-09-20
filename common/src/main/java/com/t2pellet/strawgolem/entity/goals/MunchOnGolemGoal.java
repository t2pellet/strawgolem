package com.t2pellet.strawgolem.entity.goals;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;

public class MunchOnGolemGoal extends Goal {

    private static final TargetingConditions predicate = TargetingConditions.forNonCombat().selector(e -> e instanceof StrawGolem);

    private final Animal animal;
    private final double speedModifier;
    private StrawGolem target;
    private boolean reachedTarget = false;
    private int tryTicks = 0;
    private int nextStartTick = 0;

    public MunchOnGolemGoal(Animal animal, double speedModifier) {
        this.animal = animal;
        this.speedModifier = speedModifier;
    }


    @Override
    public boolean canUse() {
        if (!StrawgolemConfig.Behaviour.livestockEatGolems.get()) return false;
        if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        } else {
            // Occurs every 1-3 minutes
            this.nextStartTick = reducedTickDelay(600 + this.animal.getRandom().nextInt(3200));
            this.target = this.animal.level.getNearestEntity(StrawGolem.class, predicate, animal, animal.getX(), animal.getY(), animal.getZ(), animal.getBoundingBox().inflate(24.0D));
            return this.target != null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !reachedTarget && this.target.isAlive();
    }

    @Override
    public void stop() {
        if (this.target.isAlive() && this.animal.distanceToSqr(this.target) <= 1.0F) {
            this.animal.playSound(SoundEvents.HORSE_EAT, 1.0F, 1.0F);
            this.target.hurt(DamageSource.mobAttack(animal), 0.5F);
        }
        this.target = null;
    }

    @Override
    public void start() {
        this.tryTicks = 0;
    }


    @Override
    public void tick() {
        if (this.target.distanceToSqr(this.animal) > 1.0F) {
            this.reachedTarget = false;
            this.animal.getLookControl().setLookAt(this.target);
            if (this.shouldRecalculatePath()) {
                this.animal.getNavigation().moveTo(target, this.speedModifier);
            }
            ++this.tryTicks;
        } else {
            --this.tryTicks;
            this.reachedTarget = true;
        }
    }

    public boolean shouldRecalculatePath() {
        return this.tryTicks % 40 == 0;
    }
}
