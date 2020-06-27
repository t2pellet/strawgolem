package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.block.*;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

public class GolemHarvestGoal extends MoveToBlockGoal {
    private final EntityStrawGolem strawgolem;

    public GolemHarvestGoal(EntityStrawGolem strawgolem, double speedIn) {
        super(strawgolem, speedIn, StrawgolemConfig.getSearchRangeHorizontal(), StrawgolemConfig.getSearchRangeVertical());
        this.strawgolem = strawgolem;
    }

    @Override
    public boolean shouldExecute() {
        /* Checks for position set by the event handler (set when a block grows nearby) */
        if (strawgolem.isHandEmpty() && shouldMoveTo(strawgolem.world, strawgolem.getHarvestPos())) {
            destinationBlock = strawgolem.getHarvestPos();
            this.runDelay = this.getRunDelay(this.creature);
            return true;
        }
        strawgolem.clearHarvestPos();
        /* Based off the vanilla code of shouldExecute, with additional check to ensure the golems hand is empty */
        if (this.runDelay > 0) {
            --this.runDelay;
            return false;
        } else {
            this.runDelay = this.getRunDelay(this.creature);
            return strawgolem.isHandEmpty() && this.searchForDestination();
        }
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
    }

    /* Almost copied from the vanilla tick() method, just calling doHarvest when it gets to the block and some tweaks for different kinds of blocks */
    @Override
    public void tick() {
        this.strawgolem.getLookController().setLookPosition(
                this.destinationBlock.getX() + 0.5D,
                this.destinationBlock.getY(),
                this.destinationBlock.getZ() + 0.5D,
                10.0F,
                this.strawgolem.getVerticalFaceSpeed());
        double targetDistance = getTargetDistanceSq();
        Block destinationBlockType = this.strawgolem.world.getBlockState(destinationBlock).getBlock();
        if (destinationBlockType instanceof StemGrownBlock) targetDistance += 0.2D;
        if (destinationBlockType instanceof BushBlock) targetDistance += 0.55D;
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
            if (block.getBlock() instanceof CropsBlock)
                return ((CropsBlock) block.getBlock()).isMaxAge(block);
            else if (block.getBlock() instanceof StemGrownBlock)
                return true;
            else if (block.getBlockState() == Blocks.NETHER_WART.getDefaultState().with(NetherWartBlock.AGE, 3))
                return true;
            else if (block.getBlock() instanceof BushBlock && block.getBlock() instanceof IGrowable)
                return block.has(BlockStateProperties.AGE_0_3) && block.get(BlockStateProperties.AGE_0_3) == 3;
        }
        return false;
	}

	/* Handles the logic for harvesting */
    private void doHarvest() {
        ServerWorld worldIn = (ServerWorld) this.strawgolem.world;
        BlockPos pos = this.destinationBlock;
        BlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();
        /* If its the right block to harvest */
        if (shouldMoveTo(worldIn, pos)
                && worldIn.destroyBlock(pos, false)
                && StrawgolemConfig.isReplantEnabled()) {
            if (block instanceof StemGrownBlock && StrawgolemConfig.isDeliveryEnabled()) { // Handle replanting gourd blocks
                strawgolem.inventory.insertItem(0, new ItemStack(Item.BLOCK_TO_ITEM.getOrDefault(block, Items.AIR)), false);
                List<ItemEntity> dropList = worldIn.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos).grow(1.0F));
                for (ItemEntity drop : dropList) {
                    drop.remove(false);
                }
            }
            else if (block instanceof CropsBlock || block instanceof NetherWartBlock) {// Handle replanting most non-gourd blocks
                worldIn.setBlockState(pos, block.getDefaultState());
                if (block instanceof CropsBlock) {
                    CropsBlock crop = (CropsBlock) block;
                    pickupDrops(worldIn, crop.withAge(crop.getMaxAge()), pos);
                } else pickupDrops(worldIn, state.with(NetherWartBlock.AGE, 3), pos);
            } else if (state.has(BlockStateProperties.AGE_0_3)) { // Handle anything else (I'm assuming that its going to be a bush of some sort)
                worldIn.setBlockState(pos, block.getDefaultState().with(BlockStateProperties.AGE_0_3, 2));
                pickupDrops(worldIn, state.with(BlockStateProperties.AGE_0_3, 3), pos);
            }
        }
    }

    private void pickupDrops(ServerWorld worldIn, BlockState state, BlockPos pos) {
        if (StrawgolemConfig.isDeliveryEnabled()) {
            List<ItemStack> drops = Block.getDrops(state, worldIn, pos, worldIn.getTileEntity(pos));
            for (ItemStack drop : drops) {
                if (!(drop.getItem() instanceof BlockNamedItem) || drop.getUseAction() == UseAction.EAT || drop.getItem() == Items.NETHER_WART) {
                    this.strawgolem.inventory.insertItem(0, drop, false);
                }
            }
        }
    }

}
