package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.commodorethrawn.strawgolem.entity.capability.hunger.IHasHunger;
import com.commodorethrawn.strawgolem.entity.capability.tether.IHasTether;
import com.commodorethrawn.strawgolem.entity.capability.tether.Tether;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class GolemTetherGoal<T extends PathAwareEntity & IHasTether> extends MoveToTargetPosGoal {

    private final T entity;

    public GolemTetherGoal(T entity, double speed) {
        super(entity, speed, StrawgolemConfig.Harvest.getSearchRange(), StrawgolemConfig.Harvest.getSearchRange());
        this.entity = entity;
    }


    @Override
    public boolean canStart() {
        final double d = getTetherDistance();
        if(d > StrawgolemConfig.Tether.getTetherMaxRange() && super.canStart()) {
            if (entity instanceof IHasHunger) {
                if (((IHasHunger) entity).getHunger().isHungry()) return false;
            }
            this.targetPos = entity.getTether().get().getPos();
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        if (StrawgolemConfig.Miscellaneous.isSoundsEnabled()) entity.playSound(EntityStrawGolem.GOLEM_SCARED, 1.0F, 1.0F);
        super.start();
    }

    @Override
    public boolean shouldContinue() {
        final double d = getTetherDistance();
        return d > StrawgolemConfig.Tether.getTetherMaxRange();
    }

    @Override
    protected boolean isTargetPos(WorldView worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getBlock() != Blocks.AIR;
    }

    @Override
    public void tick() {
        this.entity.getLookControl().lookAt(
                this.targetPos.getX() + 0.5D,
                this.targetPos.getY(),
                this.targetPos.getZ() + 0.5D,
                10.0F,
                this.entity.getLookPitchSpeed());
        if (!this.targetPos.isWithinDistance(this.mob.getPos(), this.getDesiredSquaredDistanceToTarget())) {
            ++this.tryingTime;
            if (this.shouldResetPath()) {
                double speed = this.speed;
                if (entity instanceof IHasHunger) speed *= ((IHasHunger) entity).getHunger().getPercentage();
                this.mob.getNavigation().startMovingTo(
                        this.targetPos.getX() + 0.5D,
                        this.targetPos.getY() + 1D,
                        this.targetPos.getZ() + 0.5D,
                        speed);
            }
        } else {
            tryingTime = 0;
        }
    }

    private double getTetherDistance() {
        final Tether.TetherPos anchor = entity.getTether().get();
        final World golemWorld = entity.world;
        final BlockPos golemPos = entity.getBlockPos();
        if (anchor == Tether.TetherPos.ORIGIN) {
            // if anchor is unset, this is a new golem, set it
            Strawgolem.logger.debug( entity.getEntityId() + " has no anchor, setting " + golemPos );
            entity.getTether().set(golemWorld, golemPos);
            return 0.0;
        } else {
            return entity.getTether().distanceTo(golemWorld, golemPos);
        }
    }

}
