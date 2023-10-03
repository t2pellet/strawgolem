package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class GolemBeShyGoal extends AvoidEntityGoal<Player> {

    public GolemBeShyGoal(StrawGolem golem) {
        super(golem, Player.class, 2.5F, 0.5D, 0.7D);
    }

    @Override
    public boolean canUse() {
        this.toAvoid = this.mob.level.getNearestPlayer(this.mob, maxDist);
        if (this.toAvoid == null || toAvoid.isHolding(StrawGolem.REPAIR_ITEM)) {
            return false;
        } else {
            Vec3 $$0 = DefaultRandomPos.getPosAway(this.mob, 4, 2, this.toAvoid.position());
            if ($$0 == null) {
                return false;
            } else if (this.toAvoid.distanceToSqr($$0.x, $$0.y, $$0.z) < this.toAvoid.distanceToSqr(this.mob)) {
                return false;
            } else {
                this.path = this.pathNav.createPath($$0.x, $$0.y, $$0.z, 0);
                return this.path != null;
            }
        }
    }
}
