package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class GolemPoutGoal extends Goal {

    private final EntityStrawGolem golem;
    private final double originalSpeed;

    public GolemPoutGoal(EntityStrawGolem golem) {
        this.golem = golem;
        originalSpeed = golem.getMoveControl().getSpeed();
    }

    @Override
    public boolean canStart() {
        return golem.getHunger().isHungry();
    }

    @Override
    public boolean shouldContinue() {
        return golem.getHunger().isHungry();
    }

    @Override
    public void start() {
        golem.setMovementSpeed(0.0F);
    }

    @Override
    public void tick() {
        Optional<PlayerEntity> player = golem.world.getEntitiesByClass(PlayerEntity.class, golem.getBoundingBox().expand(10.0F), e -> true)
                                        .stream().filter(p -> p.getMainHandStack().getItem() == Items.APPLE).findFirst();
        if (player.isPresent()) {
            golem.getLookControl().lookAt(player.get().getPos().add(0, 2.0F, 0));
        } else {
            Vector3f facingVector = golem.getHorizontalFacing().getUnitVector();
            Vec3d facingPos = golem.getPos().add(facingVector.getX(), facingVector.getY(), facingVector.getZ());
            golem.getLookControl().lookAt(facingPos);
        }
        super.tick();
    }

    @Override
    public void stop() {
        golem.setMovementSpeed((float) originalSpeed);
    }
}
