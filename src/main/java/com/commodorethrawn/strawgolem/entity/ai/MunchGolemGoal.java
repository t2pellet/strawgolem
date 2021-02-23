package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.sound.SoundEvents;

public class MunchGolemGoal extends Goal {

    private static final TargetPredicate predicate = new TargetPredicate().setPredicate(e -> e instanceof EntityStrawGolem).setBaseMaxDistance(10.0D).includeTeammates();
    private final double speed;
    private final AnimalEntity animal;
    private EntityStrawGolem strawGolem;
    private int cooldownTime;
    private boolean munched;

    public MunchGolemGoal(AnimalEntity mob, double speed) {
        this.animal = mob;
        this.speed = speed;
        this.cooldownTime = 0;
        this.munched = false;
    }

    @Override
    public boolean canStart() {
        if (animal.getRandom().nextInt(12000) == 0) {
            if (cooldownTime > 0) {
                --cooldownTime;
                return false;
            }
            strawGolem = animal.world.getClosestEntity(EntityStrawGolem.class, predicate, this.animal, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getBoundingBox().expand(10.0D, 4.0D, 10.0D));
            return strawGolem != null;
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        return !this.munched;
    }

    @Override
    public void tick() {
        animal.getLookControl().lookAt(strawGolem.getPos());
        if (strawGolem.distanceTo(animal) > 1.1D) {
            animal.getNavigation().startMovingTo(strawGolem, speed);
        } else {
            animal.playSound(SoundEvents.ENTITY_HORSE_EAT, 1.0F, 1.0F);
            strawGolem.damage(DamageSource.mob(animal), 0.5F);
            strawGolem.playSound(EntityStrawGolem.GOLEM_HURT, 1.0F, 1.0F);
            this.munched = true;
        }
    }

    @Override
    public void stop() {
        this.cooldownTime = 7200;
    }
}
