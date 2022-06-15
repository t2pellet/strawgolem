package com.t2pellet.strawgolem.entity.ai;

import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.crop.CropHandler;
import com.t2pellet.strawgolem.crop.CropRegistry;
import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import com.t2pellet.strawgolem.network.HoldingPacket;
import com.t2pellet.strawgolem.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.t2pellet.strawgolem.registry.CommonRegistry.Sounds.GOLEM_INTERESTED;
import static com.t2pellet.strawgolem.registry.CommonRegistry.Sounds.GOLEM_STRAINED;

public class GolemHarvestGoal extends MoveToBlockGoal {

    private final EntityStrawGolem strawgolem;

    public GolemHarvestGoal(EntityStrawGolem strawgolem) {
        super(strawgolem, 0.7D, StrawgolemConfig.Harvest.getSearchRange(), StrawgolemConfig.Harvest.getSearchRange());
        this.strawgolem = strawgolem;
    }

    // TODO : Check for valid path
    @Override
    public boolean canUse() {
        /* Checks for cooldown period, then checks for any nearby crops */
        if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        } else if (strawgolem.isHandEmpty() && !strawgolem.getHunger().isHungry()) {
            BlockPos harvestPos = strawgolem.harvestPos != null
                    ? strawgolem.harvestPos
                    : CropHandler.INSTANCE.getNearestCrop(strawgolem.level, strawgolem.blockPosition(), StrawgolemConfig.Harvest.getSearchRange());
            if (harvestPos != null) {
                BlockState state = strawgolem.level.getBlockState(harvestPos);
                BlockEntity entity = strawgolem.level.getBlockEntity(harvestPos);
                if (CropRegistry.INSTANCE.isGrownCrop(state) || CropRegistry.INSTANCE.isGrownCrop(entity)) {
                    if (strawgolem.canReachBlock(strawgolem.level, harvestPos)) {
                        blockPos = harvestPos;
                        strawgolem.harvestPos = harvestPos;
                        return true;
                    } else if (harvestPos == strawgolem.harvestPos) {
                        // Put crop we're trying to resume harvesting back into the system if we can't reach it
                        CropHandler.INSTANCE.addCrop(strawgolem.level, harvestPos);
                    }
                } else {
                    CropHandler.INSTANCE.removeCrop(strawgolem.level, harvestPos);
                }
            }
        }
        strawgolem.harvestPos = null;
        return false;
    }

    @Override
    public void start() {
        this.nextStartTick = nextStartTick(this.mob);
        CropHandler.INSTANCE.removeCrop(strawgolem.level, blockPos); // Remove crop from handler, as its being harvested now
        strawgolem.playSound(GOLEM_INTERESTED, 1.0F, 1.0F);
    }

    @Override
    protected int nextStartTick(PathfinderMob mob) {
        return 120 + mob.getRandom().nextInt(121);
    }

    @Override
    public boolean canContinueToUse() {
        return strawgolem.getVehicle() == null && super.canContinueToUse();
    }


    @Override
    public void stop() {
        // If we stopped while still harvesting, restore the crop and clear harvesting flag
        BlockState state = strawgolem.level.getBlockState(blockPos);
        BlockEntity entity = strawgolem.level.getBlockEntity(blockPos);
        if (CropRegistry.INSTANCE.isGrownCrop(state) || CropRegistry.INSTANCE.isGrownCrop(entity)) {
            CropHandler.INSTANCE.addCrop(strawgolem.level, blockPos);
        }
        strawgolem.harvestPos = null;
    }

    /* Almost copied from the vanilla tick() method, just calling harvestCrop when it gets to the block and some tweaks for different kinds of blocks */
    @Override
    public void tick() {
        if (!strawgolem.isRunningGoal(GolemLookAtPlayerGoal.class)) {
            this.strawgolem.getLookControl().setLookAt(Vec3.atCenterOf(this.blockPos));
        }
        double targetDistance = acceptedDistance();
        Block blockPosType = this.strawgolem.level.getBlockState(blockPos).getBlock();
        if (blockPosType instanceof StemGrownBlock) targetDistance += 0.2D;
        if (blockPosType instanceof BushBlock) targetDistance += 0.55D;
        if (!this.blockPos.closerToCenterThan(this.mob.position(), targetDistance)) {
            ++this.tryTicks;
            if (this.shouldRecalculatePath()) {
                this.mob.getNavigation().moveTo(this.blockPos.getX() + 0.5D, this.blockPos.getY() + 1D, this.blockPos.getZ() + 0.5D, speedModifier);
            }
        } else {
            --this.tryTicks;
            harvestCrop();
        }
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        BlockState state = worldIn.getBlockState(pos);
        BlockEntity entity = worldIn.getBlockEntity(pos);
        return (CropRegistry.INSTANCE.isGrownCrop(state) || CropRegistry.INSTANCE.isGrownCrop(entity)) && strawgolem.isHandEmpty();
    }

    /**
     * Handles the harvesting logic
     * Destroys the target crop, picking it up if delivery is enabled, replanting if enabled
     */
    private void harvestCrop() {
        ServerLevel worldIn = (ServerLevel) this.strawgolem.level;
        BlockPos pos = this.blockPos;
        /* If its the right block to harvest */
        if (isValidTarget(worldIn, pos)) {
            worldIn.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (StrawgolemConfig.Delivery.isDeliveryEnabled()) pickupCrop(worldIn, pos);
            if (StrawgolemConfig.Harvest.isReplantEnabled()) replantCrop(worldIn, pos);
            else worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }

    /**
     * Handles the logic for picking up the harvests
     *
     * @param worldIn the world
     * @param pos     the position of the crop
     */
    private void pickupCrop(ServerLevel worldIn, BlockPos pos) {
        List<ItemStack> drops = CropRegistry.INSTANCE.handleHarvest(worldIn, strawgolem, pos);
        for (ItemStack drop : drops) {
            strawgolem.getInventory().addItem(drop);
            if (drop.getItem() == Items.POISONOUS_POTATO) {
                strawgolem.addEffect(new MobEffectInstance(MobEffects.POISON, 10, 1));
            } else if (drop.getItem() instanceof BlockItem && !(drop.getItem() instanceof ItemNameBlockItem)) {
                strawgolem.playSound(GOLEM_STRAINED, 1.0F, 1.0F);
                break;
            }
        }
        Services.PACKETS.sendInRange(new HoldingPacket(strawgolem), strawgolem, 25.0F);
    }

    /**
     * Handles the replanting logic
     *
     * @param worldIn the world
     * @param pos     the position of the crop
     */
    private void replantCrop(ServerLevel worldIn, BlockPos pos) {
        CropRegistry.INSTANCE.handleReplant(worldIn, pos);
    }

}
