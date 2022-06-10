package com.t2pellet.strawgolem.entity.ai;

import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import com.t2pellet.strawgolem.network.HoldingPacket;
import com.t2pellet.strawgolem.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class GolemDeliverGoal extends MoveToBlockGoal {
    private final EntityStrawGolem strawGolem;
    private Boolean deliveringBlock;

    public GolemDeliverGoal(EntityStrawGolem strawGolem) {
        super(strawGolem, 0.7D, StrawgolemConfig.Harvest.getSearchRange(), StrawgolemConfig.Harvest.getSearchRange());
        this.strawGolem = strawGolem;
    }

    @Override
    public boolean canUse() {
        return !strawGolem.isHandEmpty() && findNearestBlock();
    }

    @Override
    public boolean canContinueToUse() {
        return !strawGolem.isHandEmpty() && super.canContinueToUse();
    }

    @Override
    protected boolean findNearestBlock() {
        BlockPos pos = strawGolem.getMemory().getDeliveryChest(strawGolem.getLevel(), strawGolem.blockPosition());
        if (isValidTarget(strawGolem.level, pos)) {
            this.blockPos = pos;
            return true;
        }
        if (strawGolem.getMemory().getPriorityChest().equals(pos))
            strawGolem.getMemory().setPriorityChest(BlockPos.ZERO);
        strawGolem.getMemory().removePosition(strawGolem.level, pos);
        return (super.findNearestBlock() && strawGolem.canReachBlock(strawGolem.level, blockPos));
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        Block b = worldIn.getBlockState(pos).getBlock();
        return be instanceof Container && StrawgolemConfig.Delivery.isDeliveryAllowed(b);
    }

    @Override
    public void tick() {
        if (deliveringBlock == null) {
            deliveringBlock = strawGolem.holdingFullBlock();
        }
        this.strawGolem.getLookControl().setLookAt(
                this.blockPos.getX() + 0.5D,
                this.blockPos.getY(),
                this.blockPos.getZ() + 0.5D,
                10.0F,
                this.strawGolem.getYHeadRot());
        if (!this.blockPos.closerToCenterThan(this.mob.position(), this.acceptedDistance())) {
            ++this.tryTicks;
            if (this.canContinueToUse()) {
                double moveSpeed = strawGolem.holdingFullBlock() ? speedModifier * 2 / 3F : speedModifier;
                moveSpeed *= strawGolem.getHunger().getPercentage();
                this.mob.getNavigation().moveTo(this.blockPos.getX() + 0.5D, this.blockPos.getY() + 1D, this.blockPos.getZ() + 0.5D, moveSpeed);
            }
        } else {
            tryTicks = 0;
            doDeposit();
        }
    }

    /**
     * Handles the logic for deposits
     * Finds first empty/compatible slot in the chest and puts the golem's held item there
     */
    private void doDeposit() {
        strawGolem.getMemory().addPosition(strawGolem.level, blockPos);
        ServerLevel worldIn = (ServerLevel) this.strawGolem.level;
        BlockPos pos = this.blockPos;
        BaseContainerBlockEntity invBlock = (BaseContainerBlockEntity) worldIn.getBlockEntity(pos);
        ItemStack insertStack = this.strawGolem.getInventory().removeItem(0, 1);
        boolean chestFull = true;
        for (int i = 0; i < invBlock.getContainerSize(); ++i) {
            if (invBlock.getItem(i).getItem() == Items.AIR
                    || (invBlock.getItem(i).getItem() == insertStack.getItem() && invBlock.getItem(i).getCount() < invBlock.getItem(i).getMaxStackSize())) {
                insertStack.setCount(insertStack.getCount() + invBlock.getItem(i).getCount());
                invBlock.setItem(i, insertStack);
                chestFull = false;
                break;
            }
        }
        if (chestFull) {
            ItemEntity item = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), insertStack);
            worldIn.addFreshEntity(item);
        }
        worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
        strawGolem.getNavigation().recomputePath();
        Services.PACKETS.sendInRange(new HoldingPacket(strawGolem), strawGolem, 25.0F);
    }

    @Override
    public double acceptedDistance() {
        return super.acceptedDistance() + 0.5D;
    }

}
