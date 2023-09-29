package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.raid.Raider;

public class GolemFleeEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {

    private final StrawGolem golem;

    public GolemFleeEntityGoal(StrawGolem mob, Class<T> clazz, float distance, double walkSpeed, double sprintSpeed) {
        super(mob, clazz, distance, walkSpeed, sprintSpeed);
        this.golem = mob;
    }

    @Override
    public boolean canUse() {
        if (Monster.class.isAssignableFrom(avoidClass)) {
            return StrawgolemConfig.Behaviour.golemsRunFromMonsters.get() && super.canUse();
        } else if (Raider.class.isAssignableFrom(avoidClass)) {
            return StrawgolemConfig.Behaviour.golemsRunFromRaiders.get() && super.canUse();
        } else if (Animal.class.isAssignableFrom(avoidClass)) {
            return StrawgolemConfig.Behaviour.golemsRunFromLivestock.get() && super.canUse();
        }
        return super.canUse();
    }

    @Override
    public void start() {
        super.start();
        golem.setScared(true);
    }

    @Override
    public void stop() {
        super.stop();
        golem.setScared(false);
    }
}
