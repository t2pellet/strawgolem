package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.commodorethrawn.strawgolem.entity.EntityStrawngGolem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.EnumSet;
import java.util.List;

public class TrackStrawngGolemTargetGoal extends TrackTargetGoal {
    private final EntityStrawngGolem golem;
    private LivingEntity target;
    private final TargetPredicate targetPredicate = (new TargetPredicate()).setBaseMaxDistance(64.0D);

    public TrackStrawngGolemTargetGoal(EntityStrawngGolem golem) {
        super(golem, false, true);
        this.golem = golem;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    public boolean canStart() {
        Box box = this.golem.getBoundingBox().expand(10.0D, 8.0D, 10.0D);
        List<LivingEntity> list = this.golem.world.getTargets(EntityStrawGolem.class, this.targetPredicate, this.golem, box);

        for (LivingEntity livingEntity : list) {
            EntityStrawGolem strawGolem = (EntityStrawGolem) livingEntity;
            if (strawGolem.getRecentDamageSource() != null && strawGolem.getRecentDamageSource().getAttacker() instanceof PlayerEntity) {
                this.target = (LivingEntity) strawGolem.getRecentDamageSource().getAttacker();
            }

        }

        if (this.target == null) {
            return false;
        } else return !(this.target instanceof PlayerEntity) || !this.target.isSpectator() && !((PlayerEntity) this.target).isCreative();
    }

    public void start() {
        this.golem.setTarget(this.target);
        super.start();
    }
}
