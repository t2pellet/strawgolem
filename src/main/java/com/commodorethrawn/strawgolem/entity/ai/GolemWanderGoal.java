package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.ai.goal.WanderAroundPointOfInterestGoal;
import net.minecraft.util.math.BlockPos;

public class GolemWanderGoal extends WanderAroundPointOfInterestGoal {

    private final EntityStrawGolem strawGolem;

    public GolemWanderGoal(EntityStrawGolem creature, double speedIn) {
        super(creature, speedIn, true);
        strawGolem = creature;
    }

    @Override
    public boolean canStart() {
        return strawGolem.isHandEmpty()
                && strawGolem.getHarvestPos().equals(BlockPos.ZERO)
                && !strawGolem.getHunger().isHungry()
                && super.canStart();
    }
}
