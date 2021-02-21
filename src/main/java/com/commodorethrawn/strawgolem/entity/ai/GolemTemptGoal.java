package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.math.BlockPos;

public class GolemTemptGoal extends TemptGoal {

    private final EntityStrawGolem strawGolem;

    public GolemTemptGoal(EntityStrawGolem creatureIn) {
        super(creatureIn, 0.7D, false, Ingredient.ofItems(Items.APPLE));
        strawGolem = creatureIn;
    }

    @Override
    public void start() {
        super.start();
        if (ConfigHelper.isSoundsEnabled()) strawGolem.playSound(EntityStrawGolem.GOLEM_INTERESTED, 1.0F, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (ConfigHelper.isTetherEnabled() && ConfigHelper.doesTemptResetTether()) {
            BlockPos golemPos = strawGolem.getBlockPos();
            double d = golemPos.getManhattanDistance(strawGolem.getMemory().getAnchorPos());
            if (d > ConfigHelper.getTetherMaxRange()) {
                Strawgolem.logger.debug(strawGolem.getEntityId() + " setting new anchor " + golemPos);
                strawGolem.getMemory().setAnchorPos(golemPos);
            }
        }
    }
}
