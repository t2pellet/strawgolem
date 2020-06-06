package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;

import java.util.List;

public class HarvestGoal extends MoveToBlockGoal {
	private EntityStrawGolem strawgolem;

	public HarvestGoal(EntityStrawGolem strawgolem, double speedIn) {
		super(strawgolem, speedIn, 16);
		this.strawgolem = strawgolem;
	}

	@Override
	public boolean shouldExecute() {
        if (super.shouldExecute() && strawgolem.isHandEmpty()) {
			this.runDelay = 0;
			return true;
		} else return false;
	}

    @Override
	public void tick() {
        this.strawgolem.getLookController().setLookPosition(
                this.destinationBlock.getX() + 0.5D,
                this.destinationBlock.getY() + 1.0D,
                this.destinationBlock.getZ() + 0.5D,
                10.0F,
                this.strawgolem.getVerticalFaceSpeed());
        if (!this.destinationBlock.withinDistance(this.creature.getPositionVec(), this.getTargetDistanceSq())) {
            ++this.timeoutCounter;
            if (this.shouldMove()) {
                this.creature.getNavigator().tryMoveToXYZ(this.destinationBlock.getX() + 0.5D, this.destinationBlock.getY() + 2D, this.destinationBlock.getZ() + 0.5D, this.movementSpeed);
            }
        } else {
            --this.timeoutCounter;
            doHarvest();
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

    private void doHarvest() {
        ServerWorld worldIn = (ServerWorld) this.strawgolem.world;
        BlockPos pos = this.destinationBlock.up();
        CropsBlock block = (CropsBlock) worldIn.getBlockState(pos).getBlock();
        if (shouldMoveTo(worldIn, this.destinationBlock)
                && worldIn.destroyBlock(pos, true)
                && StrawgolemConfig.isHarvestEnabled()) {
            worldIn.setBlockState(pos, block.getDefaultState());
            List<ItemEntity> dropList = worldIn.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos).grow(1.0F));
            for (ItemEntity drop : dropList) {
                if (Tags.Items.CROPS.contains(drop.getItem().getItem())) {
                    this.strawgolem.inventory.insertItem(0, drop.getItem(), false);
                }
                drop.remove();
            }
        }
    }

}
