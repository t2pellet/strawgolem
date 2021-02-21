package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;

public class GolemLookAtPlayerGoal extends LookAtEntityGoal {

    private final EntityStrawGolem strawGolem;

    public GolemLookAtPlayerGoal(EntityStrawGolem entityIn, float maxDistance) {
        super(entityIn, PlayerEntity.class, maxDistance);
        strawGolem = entityIn;
    }

    @Override
    public boolean canStart() {
        return !strawGolem.hasVehicle() && super.canStart();
    }

}
