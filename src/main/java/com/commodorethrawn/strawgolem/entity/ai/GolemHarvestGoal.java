package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.block.*;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class GolemHarvestGoal extends MoveToBlockGoal {
	private EntityStrawGolem strawgolem;

    public GolemHarvestGoal(EntityStrawGolem strawgolem, double speedIn) {
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
                this.destinationBlock.getY(),
                this.destinationBlock.getZ() + 0.5D,
                10.0F,
                this.strawgolem.getVerticalFaceSpeed());
        double targetDistance = strawgolem.world.getBlockState(destinationBlock).getBlock() instanceof StemGrownBlock ? getTargetDistanceSq() + 0.2D : getTargetDistanceSq();
        if (!this.destinationBlock.withinDistance(this.creature.getPositionVec(), targetDistance)) {
            ++this.timeoutCounter;
            if (this.shouldMove()) {
                this.creature.getNavigator().tryMoveToXYZ(this.destinationBlock.getX() + 0.5D, this.destinationBlock.getY() + 1D, this.destinationBlock.getZ() + 0.5D, this.movementSpeed);
            }
        } else {
            --this.timeoutCounter;
            doHarvest();
        }
    }

    @Override
	protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        RayTraceContext ctx = new RayTraceContext(strawgolem.getPositionVec(), new Vec3d(pos), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, strawgolem);
        if (!worldIn.rayTraceBlocks(ctx).getPos().equals(pos)) return false;
        BlockState block = worldIn.getBlockState(pos);
        if (StrawgolemConfig.blockHarvestAllowed(block.getBlock())) {
            if (block.getBlock() instanceof CropsBlock) {
                return ((CropsBlock) block.getBlock()).isMaxAge(block);
            } else if (block.getBlock() instanceof StemGrownBlock) {
                return true;
            } else if (block.getBlock() == Blocks.NETHER_WART) {
                return block.getBlockState() == Blocks.NETHER_WART.getDefaultState().with(NetherWartBlock.AGE, 3);
            }
        }
        return false;
	}

    private void doHarvest() {
        ServerWorld worldIn = (ServerWorld) this.strawgolem.world;
        BlockPos pos = this.destinationBlock;
        Block block = worldIn.getBlockState(pos).getBlock();
        if (shouldMoveTo(worldIn, this.destinationBlock)
                && worldIn.destroyBlock(pos, true)
                && StrawgolemConfig.isHarvestEnabled()) {
            if (!(block instanceof StemGrownBlock)) {
                worldIn.setBlockState(pos, block.getDefaultState());
                List<ItemEntity> dropList = worldIn.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos).grow(1.0F));
                for (ItemEntity drop : dropList) {
                    if (!(drop.getItem().getItem() instanceof BlockNamedItem) || drop.getItem().getUseAction() == UseAction.EAT) {
                        this.strawgolem.inventory.insertItem(0, drop.getItem(), false);
                    }
                    drop.remove();
                }
            } else {
                strawgolem.inventory.insertItem(0, new ItemStack(Item.BLOCK_TO_ITEM.getOrDefault(block, Items.AIR)), false);
                List<ItemEntity> dropList = worldIn.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos).grow(1.0F));
                for (ItemEntity drop : dropList) {
                    drop.remove();
                }
            }
        }
    }

}
