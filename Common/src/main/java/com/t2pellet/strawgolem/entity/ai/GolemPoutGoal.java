package com.t2pellet.strawgolem.entity.ai;

import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class GolemPoutGoal extends Goal {

    private final StrawGolem golem;
    private final double originalSpeed;

    public GolemPoutGoal(StrawGolem golem) {
        this.golem = golem;
        originalSpeed = golem.getMoveControl().getSpeedModifier();
    }

    @Override
    public boolean canUse() {
        return !golem.isHarvesting() && golem.isHandEmpty() && golem.getHunger().isHungry();
    }

    @Override
    public boolean canContinueToUse() {
        return golem.isHandEmpty() && golem.getHunger().isHungry();
    }

    @Override
    public void start() {
        golem.setSpeed(0.0F);
    }

    @Override
    public void tick() {
        Optional<Player> player = golem.level.getEntitiesOfClass(Player.class, golem.getBoundingBox().inflate(10.0F), e -> true)
                .stream().filter(p -> p.getMainHandItem().getItem() == StrawgolemConfig.Health.getFoodItem()).findFirst();
        if (player.isPresent()) {
            golem.getLookControl().setLookAt(player.get());
        } else {
            Vec3i facingVector = golem.getDirection().getNormal();
            Vec3 facingPos = golem.position().add(facingVector.getX(), facingVector.getY(), facingVector.getZ());
            golem.getLookControl().setLookAt(facingPos);
        }
        super.tick();
    }

    @Override
    public void stop() {
        golem.setSpeed((float) originalSpeed);
    }
}
