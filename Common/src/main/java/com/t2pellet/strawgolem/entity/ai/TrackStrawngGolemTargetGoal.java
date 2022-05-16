package com.t2pellet.strawgolem.entity.ai;

import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import com.t2pellet.strawgolem.entity.EntityStrawngGolem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

public class TrackStrawngGolemTargetGoal extends TargetGoal {
    private final EntityStrawngGolem golem;
    private LivingEntity target;
    private final TargetingConditions targetPredicate = TargetingConditions.forNonCombat().range(48.0D);

    public TrackStrawngGolemTargetGoal(EntityStrawngGolem golem) {
        super(golem, false, true);
        this.golem = golem;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        AABB box = this.golem.getBoundingBox().inflate(10.0D, 8.0D, 10.0D);
        List<EntityStrawGolem> list = this.golem.level.getNearbyEntities(EntityStrawGolem.class, this.targetPredicate, this.golem, box);

        for (EntityStrawGolem strawGolem : list) {
            if (strawGolem.getLastDamageSource() != null && strawGolem.getLastDamageSource().getEntity() instanceof LivingEntity) {
                this.target = (LivingEntity) strawGolem.getLastDamageSource().getEntity();
            }
        }

        if (this.target == null) {
            return false;
        } else
            return !(this.target instanceof Player) || !this.target.isSpectator() && !((Player) this.target).isCreative();
    }

    public void start() {
        this.golem.setTarget(this.target);
        super.start();
    }
}
