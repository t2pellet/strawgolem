package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.commodorethrawn.strawgolem.entity.strawgolem.EntityStrawGolem;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;

public class GolemTemptGoal extends TemptGoal {

    private EntityStrawGolem strawGolem;

    public GolemTemptGoal(EntityStrawGolem creatureIn) {
        super(creatureIn, 0.7D, false, Ingredient.fromItems(Items.APPLE));
        strawGolem = creatureIn;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        if (StrawgolemConfig.isSoundsEnabled()) strawGolem.playSound(strawGolem.GOLEM_INTERESTED, 1.0F, 1.0F);
    }
}
