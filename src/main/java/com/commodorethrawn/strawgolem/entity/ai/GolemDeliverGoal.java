package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.commodorethrawn.strawgolem.network.HoldingPacket;
import com.commodorethrawn.strawgolem.network.PacketHandler;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class GolemDeliverGoal extends MoveToTargetPosGoal {
    private final EntityStrawGolem strawGolem;
    private Boolean deliveringBlock;

    public GolemDeliverGoal(EntityStrawGolem strawGolem, double speedIn) {
        super(strawGolem, speedIn, ConfigHelper.getSearchRangeHorizontal(), ConfigHelper.getSearchRangeVertical());
        this.strawGolem = strawGolem;
    }

    @Override
    public boolean canStart() {
        return !strawGolem.isHandEmpty() && findTargetPos();
    }

    @Override
    public boolean shouldContinue() {
        return !strawGolem.isHandEmpty() && super.shouldResetPath();
    }

    @Override
    protected boolean findTargetPos() {
        BlockPos pos = strawGolem.getMemory().getDeliveryChest(strawGolem.getEntityWorld(), strawGolem.getBlockPos());
        if (isTargetPos(strawGolem.world, pos)) {
            this.targetPos = pos;
            return true;
        }
        if (strawGolem.getMemory().getPriorityChest().equals(pos))
            strawGolem.getMemory().setPriorityChest(BlockPos.ORIGIN);
        strawGolem.getMemory().removePosition(strawGolem.world, pos);
        return (super.findTargetPos() && strawGolem.canSeeBlock(strawGolem.world, targetPos));
    }

    @Override
    protected boolean isTargetPos(WorldView worldIn, BlockPos pos) {
        return (worldIn.getBlockState(pos).getBlock() != Blocks.AIR &&
                (worldIn.getBlockEntity(pos) instanceof LootableContainerBlockEntity));
    }

    @Override
    public void tick() {
        if (deliveringBlock == null) {
            deliveringBlock = strawGolem.holdingFullBlock();
        }
        this.strawGolem.getLookControl().lookAt(
                this.targetPos.getX() + 0.5D,
                this.targetPos.getY(),
                this.targetPos.getZ() + 0.5D,
                10.0F,
                this.strawGolem.getLookPitchSpeed());
        if (!this.targetPos.isWithinDistance(this.mob.getPos(), this.getDesiredSquaredDistanceToTarget())) {
            ++this.tryingTime;
            if (this.shouldContinue()) {
                double moveSpeed = strawGolem.holdingFullBlock() ? speed * 2/3F : speed;
                this.mob.getNavigation().startMovingTo(this.targetPos.getX() + 0.5D, this.targetPos.getY() + 1D, this.targetPos.getZ() + 0.5D, moveSpeed);
            }
        } else {
            tryingTime = 0;
            doDeposit();
        }
    }

    /**
     * Handles the logic for deposits
     * Finds first empty/compatible slot in the chest and puts the golem's held item there
     */
    private void doDeposit() {
        strawGolem.getMemory().addPosition(strawGolem.world, targetPos);
        ServerWorld worldIn = (ServerWorld) this.strawGolem.world;
        BlockPos pos = this.targetPos;
        LockableContainerBlockEntity invBlock = (LockableContainerBlockEntity) worldIn.getBlockEntity(pos);
        ItemStack insertStack = this.strawGolem.getInventory().removeStack(0);
        boolean chestFull = true;
        for (int i = 0; i < invBlock.size(); ++i) {
            if (invBlock.getStack(i).getItem() == Items.AIR
                    || (invBlock.getStack(i).getItem() == insertStack.getItem() && invBlock.getStack(i).getCount() < invBlock.getStack(i).getMaxCount())) {
                insertStack.setCount(insertStack.getCount() + invBlock.getStack(i).getCount());
                invBlock.setStack(i, insertStack);
                chestFull = false;
                break;
            }
        }
        if (chestFull) {
            ItemEntity item = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ());
            item.setStack(insertStack);
            worldIn.spawnEntity(item);
        }
        worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        strawGolem.getNavigation().recalculatePath();
        PacketHandler.INSTANCE.sendInRange(new HoldingPacket(strawGolem), strawGolem, 25.0F);
    }

    @Override
    public double getDesiredSquaredDistanceToTarget() {
        return super.getDesiredSquaredDistanceToTarget() + 0.5D;
    }

}
