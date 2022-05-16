package com.t2pellet.strawgolem.entity.ai;

import com.mojang.authlib.GameProfile;
import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.crop.CropHandler;
import com.t2pellet.strawgolem.crop.CropRegistry;
import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import com.t2pellet.strawgolem.network.HoldingPacket;
import com.t2pellet.strawgolem.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;
import java.util.UUID;

public class GolemHarvestGoal extends MoveToBlockGoal {

    private final EntityStrawGolem strawgolem;

    public GolemHarvestGoal(EntityStrawGolem strawgolem) {
        super(strawgolem, 0.7D, StrawgolemConfig.Harvest.getSearchRange(), StrawgolemConfig.Harvest.getSearchRange());
        this.strawgolem = strawgolem;
    }

    @Override
    public boolean canUse() {
        /* Checks for cooldown period, then checks for any nearby crops */
        if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        } else if (strawgolem.isHandEmpty() && !strawgolem.getHunger().isHungry()) {
            blockPos = CropHandler.INSTANCE.getNearestCrop(strawgolem.level, strawgolem.blockPosition(), StrawgolemConfig.Harvest.getSearchRange());
            if (blockPos != null) {
                BlockState state = strawgolem.level.getBlockState(blockPos);
                BlockEntity entity = strawgolem.level.getBlockEntity(blockPos);
                if (CropRegistry.INSTANCE.isGrownCrop(state) || CropRegistry.INSTANCE.isGrownCrop(entity)) return true;
                CropHandler.INSTANCE.removeCrop(strawgolem.level, blockPos);
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.nextStartTick = nextStartTick(this.mob);
        CropHandler.INSTANCE.removeCrop(strawgolem.level, blockPos); // Remove crop from handler, as its being harvested now
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
        CropHandler.INSTANCE.addCrop(strawgolem.level, blockPos);
    }

    /* Almost copied from the vanilla tick() method, just calling harvestCrop when it gets to the block and some tweaks for different kinds of blocks */
    @Override
    public void tick() {
        this.strawgolem.getLookControl().setLookAt(
                this.blockPos.getX() + 0.5D,
                this.blockPos.getY(),
                this.blockPos.getZ() + 0.5D,
                10.0F,
                this.strawgolem.getYHeadRot());
        double targetDistance = acceptedDistance();
        Block blockPosType = this.strawgolem.level.getBlockState(blockPos).getBlock();
        if (blockPosType instanceof StemGrownBlock) targetDistance += 0.2D;
        if (blockPosType instanceof BushBlock) targetDistance += 0.55D;
        if (!this.blockPos.closerToCenterThan(this.mob.position(), targetDistance)) {
            ++this.tryTicks;
            if (this.shouldRecalculatePath()) {
                double speed = this.speedModifier * strawgolem.getHunger().getPercentage();
                this.mob.getNavigation().moveTo(this.blockPos.getX() + 0.5D, this.blockPos.getY() + 1D, this.blockPos.getZ() + 0.5D, speed);
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
        BlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();
        /* If its the right block to harvest */
        if (isValidTarget(worldIn, pos)) {
            worldIn.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (StrawgolemConfig.Harvest.isDeliveryEnabled()) pickupCrop(worldIn, pos, state, block);
            if (StrawgolemConfig.Harvest.isReplantEnabled()) replantCrop(worldIn, pos);
            else worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }

    /**
     * Handles the logic for picking up the harvests
     *
     * @param worldIn : the world
     * @param pos     : the position of the crop
     * @param state   : the BlockState of the crop
     * @param block   : the Block of the crop
     */
    private void pickupCrop(ServerLevel worldIn, BlockPos pos, BlockState state, Block block) {
        if (block instanceof StemGrownBlock) {
            strawgolem.playSound(EntityStrawGolem.GOLEM_STRAINED, 1.0F, 1.0F);
            strawgolem.getInventory().addItem(new ItemStack(Item.BY_BLOCK.getOrDefault(block, Items.AIR)));
        } else if (block instanceof CropBlock || block instanceof NetherWartBlock) {
            List<ItemStack> drops = Block.getDrops(state, worldIn, pos, worldIn.getBlockEntity(pos));
            for (ItemStack drop : drops) {
                if (isCropDrop(drop)) {
                    strawgolem.getInventory().addItem(drop);
                    if (drop.getItem() == Items.POISONOUS_POTATO)
                        strawgolem.addEffect(new MobEffectInstance(MobEffects.POISON, 10, 1));
                } else if (drop.getItem() instanceof BlockItem && !(drop.getItem() instanceof ItemNameBlockItem)) {
                    strawgolem.playSound(EntityStrawGolem.GOLEM_STRAINED, 1.0F, 1.0F);
                    strawgolem.getInventory().addItem(drop);
                    break;
                }
            }
        } else fakeRightClick(worldIn, pos, state); //Bushes
        Services.PACKETS.sendInRange(new HoldingPacket(strawgolem), strawgolem, 25.0F);
    }

    /**
     * Handles the replanting logic
     *
     * @param worldIn : the world
     * @param pos     : the position of the crop
     */
    private void replantCrop(ServerLevel worldIn, BlockPos pos) {
        CropRegistry.INSTANCE.handleReplant(worldIn, pos);
    }

    /**
     * Performs a simulated player right click on the given block at position pos, with BlockState state,
     * in the world worldIn
     *
     * @param worldIn the world
     * @param pos     the position
     * @param state   the BlockState
     */
    private void fakeRightClick(ServerLevel worldIn, BlockPos pos, BlockState state) {
        GameProfile fakeProfile = new GameProfile(UUID.randomUUID(), mob.getScoreboardName());
        ServerPlayer fake = new ServerPlayer(worldIn.getServer(), worldIn, fakeProfile);
        fake.setPos(mob.getX(), mob.getY(), mob.getZ());
        BlockHitResult result = new BlockHitResult(strawgolem.position(),
                strawgolem.getDirection().getOpposite(),
                pos,
                false);
        try {
            state.use(worldIn, fake, InteractionHand.MAIN_HAND, result);
            List<ItemEntity> itemList = worldIn.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(2.5F), e -> true);
            for (ItemEntity item : itemList) {
                strawgolem.getInventory().addItem(item.getItem());
                item.remove(Entity.RemovalReason.DISCARDED);
            }
        } catch (NullPointerException ex) {
            StrawgolemCommon.LOG.error(String.format("Golem could not harvest block at: %s", pos));
        }
        fake.remove(Entity.RemovalReason.DISCARDED);
    }

    /**
     * Determines whether the given drop is a normal crop to be picked up
     *
     * @param drop : the drop in question
     * @return if the drop is a normal crop to pick up
     */
    private boolean isCropDrop(ItemStack drop) {
        return !(drop.getItem() instanceof BlockItem)
                || drop.getUseAnimation() == UseAnim.EAT
                || drop.getItem() == Items.NETHER_WART;
    }

}
