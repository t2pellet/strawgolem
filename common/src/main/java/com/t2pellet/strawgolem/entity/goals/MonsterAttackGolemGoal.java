package com.t2pellet.strawgolem.entity.goals;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class MonsterAttackGolemGoal extends NearestAttackableTargetGoal<StrawGolem> {

    public MonsterAttackGolemGoal(Mob mob) {
        super(mob, StrawGolem.class, true);
    }

    @Override
    public boolean canUse() {
        if (StrawgolemConfig.Behaviour.monstersAttackGolems.get()) {
            return super.canUse();
        }
        return false;
    }
}
