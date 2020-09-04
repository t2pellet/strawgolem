package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.block.Blocks;
import net.minecraft.client.particle.HeartParticle;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IWorldReader;

public class GolemTetherGoal extends MoveToBlockGoal {
    private final EntityStrawGolem strawGolem;

    public GolemTetherGoal(EntityStrawGolem strawGolem, double speedIn) {
        super(strawGolem, speedIn, ConfigHelper.getSearchRangeHorizontal(), ConfigHelper.getSearchRangeVertical());
        this.strawGolem = strawGolem;
    }

    private double getTetherDistance() {
        final BlockPos anchor = strawGolem.getMemory().getAnchorPos();
        final BlockPos golemPos = strawGolem.getPosition();
        if (anchor == BlockPos.ZERO) {
            // if anchor is unset, this is a new golem, set it
            Strawgolem.logger.debug( strawGolem.getEntityId() + " has no anchor, setting " + golemPos );
            strawGolem.getMemory().setAnchorPos( golemPos );
            return 0.0;
        } else {
            return golemPos.manhattanDistance(anchor);
        }
    }

    @Override
    public void startExecuting() {
        if (ConfigHelper.isSoundsEnabled()) strawGolem.playSound(EntityStrawGolem.GOLEM_SCARED, 1.0F, 1.0F);
        super.startExecuting();
    }

    @Override
    public boolean shouldExecute() {
        final double d = getTetherDistance();
        if( d > ConfigHelper.getTetherMaxRange()
                && super.shouldExecute() ) {
            this.destinationBlock = strawGolem.getMemory().getAnchorPos();
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        final double d = getTetherDistance();
        return d < ConfigHelper.getTetherMinRange()
                && super.shouldContinueExecuting();
    }

    @Override
    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getBlock() != Blocks.AIR;
    }

    @Override
    public void tick() {
        this.strawGolem.getLookController().setLookPosition(
                this.destinationBlock.getX() + 0.5D,
                this.destinationBlock.getY(),
                this.destinationBlock.getZ() + 0.5D,
                10.0F,
                this.strawGolem.getVerticalFaceSpeed());
        if (!this.destinationBlock.withinDistance(this.creature.getPositionVec(), this.getTargetDistanceSq())) {
            ++this.timeoutCounter;
            if (this.shouldMove()) {
                this.creature.getNavigator().tryMoveToXYZ(this.destinationBlock.getX() + 0.5D, this.destinationBlock.getY() + 1D, this.destinationBlock.getZ() + 0.5D, movementSpeed);
            }
        } else {
            timeoutCounter = 0;
        }
    }

}
