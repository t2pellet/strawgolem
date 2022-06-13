package com.t2pellet.strawgolem.entity.ai;

import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class GolemPoutGoal extends Goal {

    private final EntityStrawGolem golem;
    private final double originalSpeed;

    public GolemPoutGoal(EntityStrawGolem golem) {
        this.golem = golem;
        originalSpeed = golem.getMoveControl().getSpeedModifier();
    }

    @Override
    public boolean canUse() {
        return golem.getHunger().isHungry();
    }

    @Override
    public boolean canContinueToUse() {
        return golem.getHunger().isHungry();
    }

    @Override
    public void start() {
        golem.setSpeed(0.0F);
    }

    @Override
    public void tick() {
        Optional<Player> player = golem.level.getEntitiesOfClass(Player.class, golem.getBoundingBox().inflate(10.0F), e -> true)
                .stream().filter(p -> p.getMainHandItem().getItem() == Items.APPLE).findFirst();
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
