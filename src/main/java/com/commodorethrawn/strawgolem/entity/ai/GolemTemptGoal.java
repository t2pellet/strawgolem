package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.commodorethrawn.strawgolem.network.GreedyPacket;
import com.commodorethrawn.strawgolem.network.PacketHandler;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GolemTemptGoal extends TemptGoal {

    private final EntityStrawGolem strawGolem;

    public GolemTemptGoal(EntityStrawGolem creatureIn) {
        super(creatureIn, 0.7D, false, Ingredient.ofItems(Items.APPLE));
        strawGolem = creatureIn;
    }

    @Override
    public boolean canStart() {
        return !strawGolem.getHunger().isHungry() && super.canStart();
    }

    @Override
    public void start() {
        super.start();
        if (ConfigHelper.isSoundsEnabled()) strawGolem.playSound(EntityStrawGolem.GOLEM_INTERESTED, 1.0F, 1.0F);
        PacketHandler.INSTANCE.sendInRange(new GreedyPacket(strawGolem, true), strawGolem, 25.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (ConfigHelper.isTetherEnabled() && ConfigHelper.doesTemptResetTether()) {
            BlockPos golemPos = strawGolem.getBlockPos();
            World golemWorld = strawGolem.world;
            double d = strawGolem.getTether().distanceTo(golemWorld, golemPos);
            if (d > ConfigHelper.getTetherMaxRange()) {
                Strawgolem.logger.debug(strawGolem.getEntityId() + " setting new anchor " + golemPos);
                strawGolem.getTether().set(golemWorld, golemPos);
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        PacketHandler.INSTANCE.sendInRange(new GreedyPacket(strawGolem, false), strawGolem, 25.0F);
    }
}
