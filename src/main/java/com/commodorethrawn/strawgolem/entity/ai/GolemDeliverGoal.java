package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class GolemDeliverGoal extends MoveToBlockGoal {
    private final EntityStrawGolem strawGolem;
    private Boolean deliveringBlock;

    public GolemDeliverGoal(EntityStrawGolem strawGolem, double speedIn) {
        super(strawGolem, speedIn, ConfigHelper.getSearchRangeHorizontal(), ConfigHelper.getSearchRangeVertical());
        this.strawGolem = strawGolem;
    }

    @Override
    public boolean shouldExecute() {
        if (!strawGolem.isHandEmpty()) return this.searchForDestination();
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !strawGolem.isHandEmpty() && super.shouldContinueExecuting();
    }

    @Override
    protected boolean searchForDestination() {
        BlockPos pos = strawGolem.getMemory().getDeliveryChest(strawGolem.getEntityWorld(), strawGolem.getPosition());
        if (shouldMoveTo(strawGolem.world, pos)) {
            this.destinationBlock = pos;
            return true;
        }
        if (strawGolem.getMemory().getPriorityChest().equals(pos))
            strawGolem.getMemory().setPriorityChest(BlockPos.ZERO);
        strawGolem.getMemory().removePosition(strawGolem.world, pos);
        return (super.searchForDestination() && strawGolem.canSeeBlock(strawGolem.world, destinationBlock));
    }

    @Override
    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        return (worldIn.getBlockState(pos).getBlock() != Blocks.AIR
                && worldIn.getTileEntity(pos) != null
                && worldIn.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).isPresent());
    }

    @Override
    public void tick() {
        if (deliveringBlock == null) {
            deliveringBlock = strawGolem.holdingFullBlock();
        }
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
            doDeposit();
        }
    }

    /**
     * Handles the logic for deposits
     * Finds first empty/compatible slot in the chest and puts the golem's held item there
     */
    private void doDeposit() {
        strawGolem.getMemory().addPosition(strawGolem.world, destinationBlock);
        ServerWorld worldIn = (ServerWorld) this.strawGolem.world;
        BlockPos pos = this.destinationBlock;
        IItemHandler chestInv = worldIn.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
                .orElseThrow(() -> new NullPointerException("Chest IItemhandler cannot be null"));
        ItemStack insertStack = this.strawGolem.getInventory().extractItem(0, 64, false);
        boolean chestFull = true;
        for (int i = 0; i < chestInv.getSlots(); ++i) {
            if (chestInv.getStackInSlot(i).getItem() == Items.AIR
                    || (chestInv.getStackInSlot(i).getItem() == insertStack.getItem() && chestInv.getStackInSlot(i).getCount() < chestInv.getSlotLimit(0))) {
                chestInv.insertItem(i, insertStack, false);
                chestFull = false;
                break;
            }
        }
        if (chestFull) {
            ItemEntity item = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ());
            item.setItem(insertStack);
            worldIn.addEntity(item);
        }
        worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        strawGolem.getNavigator().clearPath();
    }

    @Override
    public double getTargetDistanceSq() {
        return super.getTargetDistanceSq() + 0.5D;
    }
}
