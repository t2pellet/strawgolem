package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.util.math.BlockPos;

public class GolemLookRandomlyGoal extends LookRandomlyGoal {

    private final EntityStrawGolem strawGolem;

    public GolemLookRandomlyGoal(EntityStrawGolem entitylivingIn) {
        super(entitylivingIn);
        strawGolem = entitylivingIn;
    }

    @Override
    public boolean shouldExecute() {
        return !strawGolem.isPassenger()
                && strawGolem.getHarvestPos().equals(BlockPos.ZERO)
                && super.shouldExecute();
    }
}
