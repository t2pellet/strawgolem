package com.commodorethrawn.strawgolem.entity;

import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import net.minecraft.block.*;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class HarvestGoal extends MoveToBlockGoal {
	private EntityStrawGolem strawgolem;

	public HarvestGoal(EntityStrawGolem strawgolem, double speedIn) {
		super(strawgolem, speedIn, 16);
		this.strawgolem = strawgolem;
	}

	@Override
	public boolean shouldExecute() {
		if (super.shouldExecute()) {
			this.runDelay = 0;
			return true;
		} else return false;
	}

    @Override
    public double getTargetDistanceSq() {
        return 1.0D;
    }

    @Override
	public void tick() {
        this.strawgolem.getLookController().setLookPosition(
                this.destinationBlock.getX() + 0.5D,
                this.destinationBlock.getY() + 1.0D,
                this.destinationBlock.getZ() + 0.5D,
                10.0F,
                this.strawgolem.getVerticalFaceSpeed());
        if (!this.destinationBlock.up().withinDistance(this.creature.getPositionVec(), this.getTargetDistanceSq())) {
            ++this.timeoutCounter;
            if (this.shouldMove()) {
                System.out.println(this.destinationBlock.getX() + this.destinationBlock.getY() + this.destinationBlock.getZ());
                this.creature.getNavigator().tryMoveToXYZ(this.destinationBlock.getX() + 0.5D, this.destinationBlock.getY() + 2D, this.destinationBlock.getZ() + 0.5D, this.movementSpeed);
            }
        } else {
            --this.timeoutCounter;
            World worldIn = this.strawgolem.world;
            BlockPos pos = this.destinationBlock.up();
            Block block = worldIn.getBlockState(pos).getBlock();
            if (shouldMoveTo(worldIn, this.destinationBlock) && worldIn.destroyBlock(pos, true)
                    && StrawgolemConfig.isReplantEnabled())
                worldIn.setBlockState(pos, block.getDefaultState());
        }
    }

    @Override
	protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
		BlockState block = worldIn.getBlockState(pos.up());
        if (block.getBlock() instanceof CropsBlock && StrawgolemConfig.blockHarvestAllowed(block.getBlock())) {
            return ((CropsBlock) block.getBlock()).isMaxAge(block);
        } else if (block.getBlock() == Blocks.NETHER_WART && StrawgolemConfig.blockHarvestAllowed(block.getBlock())) {
            return block.getBlockState() == Blocks.NETHER_WART.getDefaultState().with(NetherWartBlock.AGE, 3);
        }
		return false;
	}

}
