package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.entity.strawgolem.EntityStrawGolem;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;

public class GolemLookRandomlyGoal extends LookRandomlyGoal {

    private EntityStrawGolem strawGolem;

    public GolemLookRandomlyGoal(EntityStrawGolem entitylivingIn) {
        super(entitylivingIn);
        strawGolem = entitylivingIn;
    }

    @Override
    public boolean shouldExecute() {
        return !strawGolem.isPassenger() && super.shouldExecute();
    }
}
