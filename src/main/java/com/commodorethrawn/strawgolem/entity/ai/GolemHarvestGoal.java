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
    private final EntityStrawGolem strawgolem;
    private Block destinationBlockType;

    public GolemHarvestGoal(EntityStrawGolem strawgolem, double speedIn) {
        super(strawgolem, speedIn, StrawgolemConfig.getSearchRangeHorizontal(), StrawgolemConfig.getSearchRangeVertical());
        this.strawgolem = strawgolem;
    }

    @Override
    public boolean shouldExecute() {
        if (super.searchForDestination() && strawgolem.isHandEmpty()) {
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
        double targetDistance = getTargetDistanceSq();
        if (destinationBlockType instanceof StemGrownBlock) targetDistance += 0.2D;
        if (destinationBlockType instanceof SweetBerryBushBlock) targetDistance += 0.55D;
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
        Vec3d posVec = strawgolem.getPositionVec();
        if (posVec.getY() % 1 > 0.01) posVec = posVec.add(0, 1, 0);
        RayTraceContext ctx = new RayTraceContext(posVec, new Vec3d(pos), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, strawgolem);
        if (!worldIn.rayTraceBlocks(ctx).getPos().equals(pos)) return false;
        BlockState block = worldIn.getBlockState(pos);
        if (StrawgolemConfig.blockHarvestAllowed(block.getBlock())) {
            if (block.getBlock() instanceof CropsBlock && ((CropsBlock) block.getBlock()).isMaxAge(block)) {
                destinationBlockType = block.getBlock();
                return true;
            } else if (block.getBlock() instanceof StemGrownBlock) {
                destinationBlockType = block.getBlock();
                return true;
            } else if (block.getBlockState() == Blocks.NETHER_WART.getDefaultState().with(NetherWartBlock.AGE, 3)) {
                destinationBlockType = block.getBlock();
                return true;
            } else if (worldIn.getBlockState(pos) == Blocks.SWEET_BERRY_BUSH.getDefaultState().with(SweetBerryBushBlock.AGE, 3)) {
                destinationBlockType = block.getBlock();
                return true;
            } else {
                return false;
            }
        }
        return false;
	}

    private void doHarvest() {
        ServerWorld worldIn = (ServerWorld) this.strawgolem.world;
        BlockPos pos = this.destinationBlock;
        Block block = worldIn.getBlockState(pos).getBlock();
        if (shouldMoveTo(worldIn, pos)
                && worldIn.destroyBlock(pos, true)
                && StrawgolemConfig.isReplantEnabled()) {
            if (!(block instanceof StemGrownBlock)) {
                worldIn.setBlockState(pos, block.getDefaultState());
                List<ItemEntity> dropList = worldIn.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos).grow(1.0F));
                for (ItemEntity drop : dropList) {
                    if (StrawgolemConfig.isDeliveryEnabled() && !(drop.getItem().getItem() instanceof BlockNamedItem) || drop.getItem().getUseAction() == UseAction.EAT) {
                        this.strawgolem.inventory.insertItem(0, drop.getItem(), false);
                    }
                    drop.remove(false);
                }
            } else {
                if (StrawgolemConfig.isDeliveryEnabled()) {
                    strawgolem.inventory.insertItem(0, new ItemStack(Item.BLOCK_TO_ITEM.getOrDefault(block, Items.AIR)), false);
                }
                List<ItemEntity> dropList = worldIn.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos).grow(1.0F));
                for (ItemEntity drop : dropList) {
                    drop.remove(false);
                }
            }
        }
    }

}
