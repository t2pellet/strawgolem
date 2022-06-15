package com.t2pellet.strawgolem.entity.ai;

import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;

import static com.t2pellet.strawgolem.registry.CommonRegistry.Sounds.GOLEM_HURT;

public class MunchGolemGoal extends Goal {

    private static final TargetingConditions predicate = TargetingConditions.forNonCombat().selector(e -> e instanceof EntityStrawGolem).range(10.0D);
    private final double speed;
    private final Animal animal;
    private EntityStrawGolem strawGolem;
    private int cooldownTime;
    private boolean munched;

    public MunchGolemGoal(Animal mob, double speed) {
        this.animal = mob;
        this.speed = speed;
        this.cooldownTime = 0;
        this.munched = false;
    }

    @Override
    public boolean canUse() {
        if (StrawgolemConfig.Miscellaneous.isGolemMunch() && animal.getRandom().nextInt(3000) == 0) {
            if (cooldownTime > 0) {
                --cooldownTime;
                return false;
            }
            strawGolem = animal.level.getNearestEntity(EntityStrawGolem.class, predicate, this.animal, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getBoundingBox().inflate(10.0D, 4.0D, 10.0D));
            return strawGolem != null;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.munched && !strawGolem.isDeadOrDying();
    }

    @Override
    public void tick() {
        animal.getLookControl().setLookAt(strawGolem);
        if (strawGolem.distanceTo(animal) > 1.1D) {
            animal.getNavigation().moveTo(strawGolem, speed);
        } else {
            animal.playSound(SoundEvents.HORSE_EAT, 1.0F, 1.0F);
            strawGolem.hurt(DamageSource.mobAttack(animal), 0.5F);
            strawGolem.playSound(GOLEM_HURT, 1.0F, 1.0F);
            this.munched = true;
        }
    }

    @Override
    public void stop() {
        this.cooldownTime = 7200;
    }
}
