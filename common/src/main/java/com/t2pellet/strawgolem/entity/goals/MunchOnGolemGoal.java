package com.t2pellet.strawgolem.entity.goals;

import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.Vec3;

public class MunchOnGolemGoal extends Goal {

    private static final TargetingConditions predicate = TargetingConditions.forNonCombat().selector(e -> e instanceof StrawGolem);

    private Animal animal;
    private final double speedModifier;
    private final float within;
    private StrawGolem target;

    public MunchOnGolemGoal(Animal animal, double speedModifier) {
        this.animal = animal;
        this.speedModifier = speedModifier;
        this.within = 1.0F;
    }


    @Override
    public boolean canUse() {
        if (this.animal.getRandom().nextInt(200) != 0) return false;
        this.target = this.animal.level.getNearestEntity(StrawGolem.class, predicate, animal, animal.getX(), animal.getY(), animal.getZ(), animal.getBoundingBox().inflate(within));
        if (this.target == null) {
            return false;
        } else return true;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.animal.getNavigation().isDone() && this.target.isAlive() && this.target.distanceToSqr(this.animal) < (double)(this.within * this.within);
    }

    @Override
    public void stop() {
        this.animal.playSound(SoundEvents.HORSE_EAT, 1.0F, 1.0F);
        this.target.hurt(DamageSource.mobAttack(animal), 0.5F);
        this.target = null;
    }

    @Override
    public void start() {
        this.animal.getNavigation().moveTo(this.target, this.speedModifier);
    }


}
