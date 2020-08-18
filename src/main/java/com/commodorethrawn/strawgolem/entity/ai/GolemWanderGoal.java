package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.util.math.BlockPos;

public class GolemWanderGoal extends WaterAvoidingRandomWalkingGoal {

    private final EntityStrawGolem strawGolem;

    public GolemWanderGoal(EntityStrawGolem creature, double speedIn) {
        super(creature, speedIn);
        strawGolem = creature;
    }

    @Override
    public boolean shouldExecute() {
        return strawGolem.isHandEmpty()
                && strawGolem.getHarvestPos().equals(BlockPos.ZERO)
                && super.shouldExecute();
    }
}
