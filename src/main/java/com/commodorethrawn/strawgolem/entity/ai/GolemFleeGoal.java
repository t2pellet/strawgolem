package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.HostileEntity;

public class GolemFleeGoal extends FleeEntityGoal<HostileEntity> {

    private final EntityStrawGolem strawGolem;

    public GolemFleeGoal(EntityStrawGolem entityIn) {
        super(entityIn, HostileEntity.class, 10.0F, 0.6D, 0.75D);
        strawGolem = entityIn;
    }

    @Override
    public boolean canStart() {
        return !strawGolem.getHunger().isHungry() && super.canStart();
    }

    @Override
    public void start() {
        super.start();
        if (StrawgolemConfig.Miscellaneous.isSoundsEnabled()) strawGolem.playSound(EntityStrawGolem.GOLEM_SCARED, 1.0F, 1.0F);
    }

}
