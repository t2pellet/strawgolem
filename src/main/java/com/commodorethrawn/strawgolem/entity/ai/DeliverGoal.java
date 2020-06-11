package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class DeliverGoal extends MoveToBlockGoal {
    private EntityStrawGolem strawGolem;
    private boolean deposited;

    public DeliverGoal(EntityStrawGolem strawGolem, double speedIn) {
        super(strawGolem, speedIn, 24);
        this.strawGolem = strawGolem;
        this.deposited = false;
    }

    @Override
    public boolean shouldExecute() {
        if (super.shouldExecute() && !strawGolem.isHandEmpty()) {
            this.runDelay = 0;
            return true;
        }
        return false;
    }

    @Override
    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.up()).getBlock().getTags().contains(Tags.Blocks.CHESTS.getId());
    }

    @Override
    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting() && !deposited;
    }

    @Override
    public void tick() {
        this.strawGolem.getLookController().setLookPosition(
                this.destinationBlock.getX() + 0.5D,
                this.destinationBlock.getY() + 1.0D,
                this.destinationBlock.getZ() + 0.5D,
                10.0F,
                this.strawGolem.getVerticalFaceSpeed());
        if (!this.destinationBlock.up().withinDistance(this.creature.getPositionVec(), this.getTargetDistanceSq())) {
            ++this.timeoutCounter;
            if (this.shouldMove()) {
                this.creature.getNavigator().tryMoveToXYZ(this.destinationBlock.getX() + 0.5D, this.destinationBlock.getY() + 2D, this.destinationBlock.getZ() + 0.5D, this.movementSpeed);
            }
        } else {
            timeoutCounter = 0;
            doDeposit();
        }
    }

    private void doDeposit() {
        ServerWorld worldIn = (ServerWorld) this.strawGolem.world;
        BlockPos pos = this.destinationBlock.up();
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
        deposited = true;
    }

}
