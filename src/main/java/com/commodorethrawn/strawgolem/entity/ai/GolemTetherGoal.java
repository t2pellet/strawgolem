package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class GolemTetherGoal extends MoveToTargetPosGoal {
    private final EntityStrawGolem strawGolem;

    public GolemTetherGoal(EntityStrawGolem strawGolem, double speedIn) {
        super(strawGolem, speedIn, ConfigHelper.getSearchRangeHorizontal(), ConfigHelper.getSearchRangeVertical());
        this.strawGolem = strawGolem;
    }

    private double getTetherDistance() {
        final BlockPos anchor = strawGolem.getMemory().getAnchorPos();
        final BlockPos golemPos = strawGolem.getBlockPos();
        if (anchor == BlockPos.ORIGIN) {
            // if anchor is unset, this is a new golem, set it
            Strawgolem.logger.debug( strawGolem.getEntityId() + " has no anchor, setting " + golemPos );
            strawGolem.getMemory().setAnchorPos( golemPos );
            return 0.0;
        } else {
            return golemPos.getManhattanDistance(anchor);
        }
    }

    @Override
    public void start() {
        if (ConfigHelper.isSoundsEnabled()) strawGolem.playSound(EntityStrawGolem.GOLEM_SCARED, 1.0F, 1.0F);
        super.start();
    }

    @Override
    public boolean canStart() {
        final double d = getTetherDistance();
        if( d > ConfigHelper.getTetherMaxRange()
                && super.canStart() ) {
            this.targetPos = strawGolem.getMemory().getAnchorPos();
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        final double d = getTetherDistance();
        return d > ConfigHelper.getTetherMaxRange();
    }

    @Override
    protected boolean isTargetPos(WorldView worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getBlock() != Blocks.AIR;
    }

    @Override
    public void tick() {
        this.strawGolem.getLookControl().lookAt(
                this.targetPos.getX() + 0.5D,
                this.targetPos.getY(),
                this.targetPos.getZ() + 0.5D,
                10.0F,
                this.strawGolem.getLookPitchSpeed());
        if (!this.targetPos.isWithinDistance(this.mob.getPos(), this.getDesiredSquaredDistanceToTarget())) {
            ++this.tryingTime;
            if (this.shouldResetPath()) {
                this.mob.getNavigation().startMovingTo(
                        this.targetPos.getX() + 0.5D,
                        this.targetPos.getY() + 1D
                        , this.targetPos.getZ() + 0.5D,
                        0.8F);
            }
        } else {
            tryingTime = 0;
        }
    }

}
