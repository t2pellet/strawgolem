package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.util.math.BlockPos;

public class GolemLookRandomlyGoal extends LookAroundGoal {

    private final EntityStrawGolem strawGolem;

    public GolemLookRandomlyGoal(EntityStrawGolem entitylivingIn) {
        super(entitylivingIn);
        strawGolem = entitylivingIn;
    }

    @Override
    public boolean canStart() {
        return !strawGolem.hasVehicle()
                && strawGolem.getHarvestPos().equals(BlockPos.ZERO)
                && !strawGolem.getHunger().isHungry()
                && super.canStart();
    }
}
