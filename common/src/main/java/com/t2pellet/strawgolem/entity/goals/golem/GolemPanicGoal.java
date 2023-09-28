package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class GolemPanicGoal extends PanicGoal {

    private final StrawGolem golem;

    public GolemPanicGoal(StrawGolem golem) {
        super(golem, 0.8D);
        this.golem = golem;
    }

    @Override
    public boolean canUse() {
        if (StrawgolemConfig.Behaviour.golemsPanicWhenHurt.get()) {
            return super.canUse();
        }
        return false;
    }

    @Override
    public void start() {
        super.start();
        // Drops item in hand
        ItemStack heldItem = golem.getHeldItem().get();
        golem.getHeldItem().set(ItemStack.EMPTY);
        ItemEntity itemEntity = new ItemEntity(golem.level, golem.getX(), golem.getY() + 1, golem.getZ(), heldItem);
        golem.level.addFreshEntity(itemEntity);
    }
}
