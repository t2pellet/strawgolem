package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.commodorethrawn.strawgolem.entity.strawgolem.EntityStrawGolem;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class GolemDeliverGoal extends MoveToBlockGoal {
    private final EntityStrawGolem strawGolem;
    private Boolean deliveringBlock;

    public GolemDeliverGoal(EntityStrawGolem strawGolem, double speedIn) {
        super(strawGolem, speedIn, StrawgolemConfig.getSearchRangeHorizontal(), StrawgolemConfig.getSearchRangeVertical());
        this.strawGolem = strawGolem;
    }

    @Override
    public boolean shouldExecute() {
        if (!strawGolem.isHandEmpty()) return this.searchForDestination();
        return false;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !strawGolem.isHandEmpty() && super.shouldContinueExecuting();
    }

    @Override
    protected boolean searchForDestination() {
        BlockPos pos = strawGolem.getMemory().getDeliveryChest(strawGolem.getPosition());
        if (shouldMoveTo(strawGolem.world, pos)) {
            this.destinationBlock = pos;
            return true;
        }
        if (strawGolem.getMemory().getPriorityChest().equals(pos))
            strawGolem.getMemory().setPriorityChest(BlockPos.ZERO);
        strawGolem.getMemory().removePosition(pos);
        return super.searchForDestination();
    }

    @Override
    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        Vec3d posVec = strawGolem.getPositionVec().add(0, 1, 0);
        if (posVec.getY() % 1 > 0.01)
            posVec = posVec.add(0, 1, 0); // Used to patch the ray trace colliding with non-full-height blocks
        RayTraceContext ctx = new RayTraceContext(posVec, new Vec3d(pos), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, strawGolem);
        if (worldIn.getTileEntity(pos) instanceof ChestTileEntity && worldIn.rayTraceBlocks(ctx).getPos().equals(pos)) {
            strawGolem.getMemory().addPosition(pos);
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        if (deliveringBlock == null) {
            deliveringBlock = strawGolem.holdingBlockCrop();
        }
        this.strawGolem.getLookController().setLookPosition(
                this.destinationBlock.getX() + 0.5D,
                this.destinationBlock.getY(),
                this.destinationBlock.getZ() + 0.5D,
                10.0F,
                this.strawGolem.getVerticalFaceSpeed());
        if (!this.destinationBlock.withinDistance(this.creature.getPositionVec(), this.getTargetDistanceSq() + 0.2D)) {
            ++this.timeoutCounter;
            if (this.shouldMove()) {
                this.creature.getNavigator().tryMoveToXYZ(this.destinationBlock.getX() + 0.5D, this.destinationBlock.getY() + 1D, this.destinationBlock.getZ() + 0.5D, movementSpeed);
            }
        } else {
            timeoutCounter = 0;
            doDeposit();
        }
    }

    private void doDeposit() {
        ServerWorld worldIn = (ServerWorld) this.strawGolem.world;
        BlockPos pos = this.destinationBlock;
        ChestTileEntity chest = (ChestTileEntity) worldIn.getTileEntity(pos);
        IItemHandler chestInv = chest.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElseThrow(() -> new NullPointerException("Chest IItemhandler cannot be null"));
        ItemStack insertStack = this.strawGolem.inventory.extractItem(0, 64, false);
        for (int i = 0; i < chestInv.getSlots(); ++i) {
            if (chestInv.getStackInSlot(i).getItem() == Items.AIR
                    || (chestInv.getStackInSlot(i).getItem() == insertStack.getItem() && chestInv.getStackInSlot(i).getCount() < chestInv.getSlotLimit(0))) {
                chestInv.insertItem(i, insertStack, false);
                break;
            }
        }
        worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        strawGolem.getNavigator().clearPath();
    }

    @Override
    public double getTargetDistanceSq() {
        return super.getTargetDistanceSq() + 0.15D;
    }
}
