package com.t2pellet.strawgolem.entity.ai;

import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;

public class GolemLookAtPlayerGoal extends LookAtPlayerGoal {

    private final EntityStrawGolem strawGolem;

    public GolemLookAtPlayerGoal(EntityStrawGolem entityIn, float maxDistance) {
        super(entityIn, Player.class, maxDistance);
        strawGolem = entityIn;
    }

    @Override
    public boolean canUse() {
        return strawGolem.getVehicle() == null && !strawGolem.getHunger().isHungry() && super.canUse();
    }

}
