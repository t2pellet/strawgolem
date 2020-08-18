package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.player.PlayerEntity;

public class GolemLookAtPlayerGoal extends LookAtGoal {

    private final EntityStrawGolem strawGolem;

    public GolemLookAtPlayerGoal(EntityStrawGolem entityIn, float maxDistance) {
        super(entityIn, PlayerEntity.class, maxDistance);
        strawGolem = entityIn;
    }

    @Override
    public boolean shouldExecute() {
        return !strawGolem.isPassenger() && super.shouldExecute();
    }
}
