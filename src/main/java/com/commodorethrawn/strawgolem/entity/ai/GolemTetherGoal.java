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
    // TODO: populate these from config
    public static final double TETHER_MAX_RANGE = 24.0;
    public static final double TETHER_MIN_RANGE = 4.0;

    private final EntityStrawGolem strawGolem;

    public GolemTetherGoal(EntityStrawGolem strawGolem, double speedIn) {
        super(strawGolem, speedIn, ConfigHelper.getSearchRangeHorizontal(), ConfigHelper.getSearchRangeVertical());
        this.strawGolem = strawGolem;
    }

    private double getTetherDistance() {
        final BlockPos anchor = strawGolem.getMemory().getAnchorPos();
        final BlockPos golemPos = strawGolem.getPosition();
        if (anchor == BlockPos.ZERO) {
            // if anchor is unset, set it
            Strawgolem.logger.debug( strawGolem.getEntityId() + " has no anchor, setting " + golemPos );
            strawGolem.getMemory().setAnchorPos( golemPos );
            return 0.0;
        } else {
            return golemPos.manhattanDistance(anchor);
        }
    }

    @Override
    public void startExecuting() {
        //Strawgolem.logger.debug( strawGolem.getEntityId() + " is starting to run home");
        if (ConfigHelper.isSoundsEnabled()) strawGolem.playSound(EntityStrawGolem.GOLEM_SCARED, 1.0F, 1.0F);
        super.startExecuting();
    }

    @Override
    public boolean shouldExecute() {
        final double d = getTetherDistance();
        if( d > TETHER_MAX_RANGE
                && super.shouldExecute() ) {
            //Strawgolem.logger.debug( strawGolem.getEntityId() + " is "+d+" > "+TETHER_MAX_RANGE+", tethering");
            this.destinationBlock = strawGolem.getMemory().getAnchorPos();
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        final double d = getTetherDistance();
        if ( d < TETHER_MIN_RANGE ) {
            //Strawgolem.logger.debug( strawGolem.getEntityId() + " is "+d+" < "+TETHER_MIN_RANGE+", stopped tethering");
            return false;
        } else {
            return super.shouldContinueExecuting();
        }
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
