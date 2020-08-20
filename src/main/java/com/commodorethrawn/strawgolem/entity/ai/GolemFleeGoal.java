package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.monster.MonsterEntity;

public class GolemFleeGoal extends AvoidEntityGoal<MonsterEntity> {

    private final EntityStrawGolem strawGolem;

    public GolemFleeGoal(EntityStrawGolem entityIn) {
        super(entityIn, MonsterEntity.class, 10.0F, 0.6D, 0.75D);
        strawGolem = entityIn;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        if (ConfigHelper.isSoundsEnabled()) strawGolem.playSound(EntityStrawGolem.GOLEM_SCARED, 1.0F, 1.0F);
    }
}
