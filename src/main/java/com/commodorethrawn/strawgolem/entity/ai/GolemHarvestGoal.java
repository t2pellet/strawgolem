package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.commodorethrawn.strawgolem.network.PacketHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.WorldView;

import java.util.List;
import java.util.UUID;

public class GolemHarvestGoal extends MoveToTargetPosGoal {
    private final EntityStrawGolem strawgolem;

    public GolemHarvestGoal(EntityStrawGolem strawgolem, double speedIn) {
        super(strawgolem, speedIn, ConfigHelper.getSearchRangeHorizontal(), ConfigHelper.getSearchRangeVertical());
        this.strawgolem = strawgolem;
    }

    @Override
    public boolean canStart() {
        /* Checks for position set by the event handler (set when a block grows nearby) */
        if (strawgolem.isHandEmpty() && !strawgolem.getHunger().isHungry() && !strawgolem.getHarvestPos().equals(BlockPos.ORIGIN)) {
            targetPos = strawgolem.getHarvestPos();
            this.cooldown = getInterval(this.mob);
            strawgolem.clearHarvestPos();
            return strawgolem.shouldHarvestBlock(strawgolem.world, targetPos)
                    && strawgolem.canSeeBlock(strawgolem.world, targetPos);
        }
        /* Based off the vanilla code of shouldExecute, with additional check to ensure the golems hand is empty */
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        } else {
            this.cooldown = getInterval(this.mob);
            return strawgolem.isHandEmpty() && this.findTargetPos()
                    && strawgolem.canSeeBlock(strawgolem.world, targetPos);
        }
    }

    @Override
    protected int getInterval(PathAwareEntity mob) {
        return 360;
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && !strawgolem.hasVehicle();
    }

    /* Almost copied from the vanilla tick() method, just calling doHarvest when it gets to the block and some tweaks for different kinds of blocks */
    @Override
    public void tick() {
        this.strawgolem.getLookControl().lookAt(
                this.targetPos.getX() + 0.5D,
                this.targetPos.getY(),
                this.targetPos.getZ() + 0.5D,
                10.0F,
                this.strawgolem.getLookPitchSpeed());
        double targetDistance = getDesiredSquaredDistanceToTarget();
        Block targetPosType = this.strawgolem.world.getBlockState(targetPos).getBlock();
        if (targetPosType instanceof GourdBlock) targetDistance += 0.2D;
        if (targetPosType instanceof PlantBlock) targetDistance += 0.55D;
        if (!this.targetPos.isWithinDistance(this.mob.getPos(), targetDistance)) {
            ++this.tryingTime;
            if (this.shouldResetPath()) {
                this.mob.getNavigation().startMovingTo(this.targetPos.getX() + 0.5D, this.targetPos.getY() + 1D, this.targetPos.getZ() + 0.5D, this.speed);
            }
        } else {
            --this.tryingTime;
            harvestCrop();
        }
    }

    @Override
    protected boolean isTargetPos(WorldView worldIn, BlockPos pos) {
        return strawgolem.shouldHarvestBlock(worldIn, pos) && strawgolem.isHandEmpty();
    }

    /**
     * Handles the harvesting logic
     * Destroys the target crop, picking it up if delivery is enabled, replanting if enabled
     */
    private void harvestCrop() {
        ServerWorld worldIn = (ServerWorld) this.strawgolem.world;
        BlockPos pos = this.targetPos;
        BlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();
        /* If its the right block to harvest */
        if (isTargetPos(worldIn, pos)) {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_CROP_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            doPickup(worldIn, pos, state, block);
            doReplant(worldIn, pos, state, block);
        }
    }

    /**
     * Handles the logic for picking up the harvests
     * @param worldIn : the world
     * @param pos : the position of the crop
     * @param state : the BlockState of the crop
     * @param block : the Block of the crop
     */
    private void doPickup(ServerWorld worldIn, BlockPos pos, BlockState state, Block block) {
        if (ConfigHelper.isDeliveryEnabled()) {
            if (block instanceof GourdBlock) {
                strawgolem.playSound(EntityStrawGolem.GOLEM_STRAINED, 1.0F, 1.0F);
                strawgolem.getInventory().addStack(new ItemStack(Item.BLOCK_ITEMS.getOrDefault(block, Items.AIR)));
            } else if (block instanceof CropBlock || block instanceof NetherWartBlock) {
                List<ItemStack> drops = Block.getDroppedStacks(state, worldIn, pos, worldIn.getBlockEntity(pos));
                for (ItemStack drop : drops) {
                    if (isCropDrop(drop)) {
                        strawgolem.getInventory().addStack(drop);
                        if (drop.getItem() == Items.POISONOUS_POTATO) strawgolem.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 10, 1));
                    } else if (drop.getItem() instanceof BlockItem && !(drop.getItem() instanceof AliasedBlockItem)) {
                        strawgolem.playSound(EntityStrawGolem.GOLEM_STRAINED, 1.0F, 1.0F);
                        strawgolem.getInventory().addStack(drop);
                        break;
                    }
                }
            } else fakeRightClick(worldIn, pos, state); //Bushes
        }
        PacketHandler.sendHoldingPacket(strawgolem);
    }

    /**
     * Handles the replanting logic
     * @param worldIn : the world
     * @param pos : the position of the crop
     * @param state : the BlockState of the crop
     * @param block : the Block of the crop
     */
    private void doReplant(ServerWorld worldIn, BlockPos pos, BlockState state, Block block) {
        if (ConfigHelper.isReplantEnabled()) {
            if (block instanceof CropBlock) {
                CropBlock crop = (CropBlock) block;
                worldIn.setBlockState(pos, crop.getDefaultState());
            } else if (block instanceof NetherWartBlock) {
                worldIn.setBlockState(pos, block.getDefaultState().with(NetherWartBlock.AGE, 0));
            } else if (state.contains(Properties.AGE_3) && block instanceof PlantBlock) { // Bushes

                worldIn.setBlockState(pos, block.getDefaultState().with(Properties.AGE_3, 2));
            } else {
                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        } else {
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
        worldIn.updateListeners(pos.up(), state, worldIn.getBlockState(pos), 3);
    }

    /**
     * Performs a simulated player right click on the given block at position pos, with BlockState state,
     * in the world worldIn
     *
     * @param worldIn the world
     * @param pos     the position
     * @param state   the BlockState
     */
    private void fakeRightClick(ServerWorld worldIn, BlockPos pos, BlockState state) {
        GameProfile fakeProfile = new GameProfile(UUID.randomUUID(), mob.getEntityName());
        ServerPlayerEntity fake = new ServerPlayerEntity(worldIn.getServer(), worldIn, fakeProfile, new ServerPlayerInteractionManager(worldIn));
        fake.setPos(mob.getX(), mob.getY(), mob.getZ());
        BlockHitResult result = new BlockHitResult(strawgolem.getPos(),
                strawgolem.getHorizontalFacing().getOpposite(),
                pos,
                false);
        try {
            state.onUse(worldIn, fake, Hand.MAIN_HAND, result);
            List<ItemEntity> itemList = worldIn.getEntitiesByClass(ItemEntity.class, new Box(pos).expand(2.5F), e -> true);
            for (ItemEntity item : itemList) {
                strawgolem.getInventory().addStack(item.getStack());
                item.remove();
            }
        } catch (NullPointerException ex) {
            Strawgolem.logger.info(String.format("Golem could not harvest block at: %s", pos));
        }
        fake.remove();
    }

    /**
     * Determines whether the given drop is a normal crop to be picked up
     *
     * @param drop : the drop in question
     * @return if the drop is a normal crop to pick up
     */
    private boolean isCropDrop(ItemStack drop) {
        return !(drop.getItem() instanceof BlockItem)
                || drop.getUseAction() == UseAction.EAT
                || drop.getItem() == Items.NETHER_WART;
    }

}
