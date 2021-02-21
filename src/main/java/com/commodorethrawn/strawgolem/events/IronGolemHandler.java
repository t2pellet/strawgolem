package com.commodorethrawn.strawgolem.events;

import com.commodorethrawn.strawgolem.entity.ai.PickupGolemGoal;
import com.commodorethrawn.strawgolem.mixin.GoalSelectorAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.server.MinecraftServer;

public class IronGolemHandler {

    public static void stopHolding(MinecraftServer server) {
        server.getWorlds().forEach(world -> {
            world.getEntitiesByType(EntityType.IRON_GOLEM, e -> true).forEach(golem -> {
                GoalSelector goalSelector = ((GoalSelectorAccessor) golem).goalSelector();
                goalSelector.getRunningGoals()
                        .filter(goal -> goal.getGoal() instanceof PickupGolemGoal)
                        .forEach(PrioritizedGoal::stop);
            });
        });
    }

}
