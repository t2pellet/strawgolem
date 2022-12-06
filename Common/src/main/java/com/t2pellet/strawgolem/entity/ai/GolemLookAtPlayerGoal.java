package com.t2pellet.strawgolem.entity.ai;

import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;

public class GolemLookAtPlayerGoal extends LookAtPlayerGoal {

    private final StrawGolem strawGolem;

    public GolemLookAtPlayerGoal(StrawGolem entityIn, float maxDistance) {
        super(entityIn, Player.class, maxDistance);
        strawGolem = entityIn;
    }

    @Override
    public boolean canUse() {
        return strawGolem.getVehicle() == null && super.canUse();
    }

}
