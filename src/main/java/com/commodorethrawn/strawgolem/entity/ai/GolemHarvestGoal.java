package com.commodorethrawn.strawgolem.entity.ai;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.*;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nonnull;
import java.util.List;

public class GolemHarvestGoal extends MoveToBlockGoal {
    private final EntityStrawGolem strawgolem;

    public GolemHarvestGoal(EntityStrawGolem strawgolem, double speedIn) {
        super(strawgolem, speedIn, ConfigHelper.getSearchRangeHorizontal(), ConfigHelper.getSearchRangeVertical());
        this.strawgolem = strawgolem;
    }

    @Override
    public boolean shouldExecute() {
        /* Checks for position set by the event handler (set when a block grows nearby) */
        if (strawgolem.isHandEmpty() && !strawgolem.getHarvestPos().equals(BlockPos.ZERO)) {
            destinationBlock = strawgolem.getHarvestPos();
            this.runDelay = getRunDelay(this.creature);
            strawgolem.clearHarvestPos();
            return strawgolem.shouldHarvestBlock(strawgolem.world, destinationBlock)
                    && strawgolem.canSeeBlock(strawgolem.world, destinationBlock);
        }
        /* Based off the vanilla code of shouldExecute, with additional check to ensure the golems hand is empty */
        if (this.runDelay > 0) {
            --this.runDelay;
            return false;
        } else {
            this.runDelay = getRunDelay(this.creature);
            return strawgolem.isHandEmpty() && this.searchForDestination()
                    && strawgolem.canSeeBlock(strawgolem.world, destinationBlock);
        }
    }

    @Override
    protected int getRunDelay(@Nonnull CreatureEntity creatureIn) {
        return 360;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting() && !strawgolem.isPassenger();
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
            harvestCrop();
        }
    }

    @Override
    protected boolean shouldMoveTo(@Nonnull IWorldReader worldIn, @Nonnull BlockPos pos) {
        return strawgolem.shouldHarvestBlock(worldIn, pos) && strawgolem.isHandEmpty();
    }

    /**
     * Handles the harvesting logic
     * Destroys the target crop, picking it up if delivery is enabled, replanting if enabled
     */
    private void harvestCrop() {
        ServerWorld worldIn = (ServerWorld) this.strawgolem.world;
        BlockPos pos = this.destinationBlock;
        BlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();
        /* If its the right block to harvest */
        if (shouldMoveTo(worldIn, pos)) {
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
            if (block instanceof StemGrownBlock) {
                strawgolem.playSound(EntityStrawGolem.GOLEM_STRAINED, 1.0F, 1.0F);
                strawgolem.getInventory().insertItem(0, new ItemStack(Item.BLOCK_TO_ITEM.getOrDefault(block, Items.AIR)), false);
            } else if (block instanceof CropsBlock || block instanceof NetherWartBlock) {
                List<ItemStack> drops = Block.getDrops(state, worldIn, pos, worldIn.getTileEntity(pos));
                for (ItemStack drop : drops) {
                    if (isCropDrop(drop)) {
                        strawgolem.getInventory().insertItem(0, drop, false);
                    } else if (drop.getItem() instanceof BlockItem && !(drop.getItem() instanceof BlockNamedItem)) {
                        strawgolem.playSound(EntityStrawGolem.GOLEM_STRAINED, 1.0F, 1.0F);
                        strawgolem.getInventory().insertItem(0, drop, false);
                        break;
                    }
                }
            } else fakeRightClick(worldIn, pos, state); //Bushes
        }
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
            if (block instanceof CropsBlock) {
                CropsBlock crop = (CropsBlock) block;
                worldIn.setBlockState(pos, crop.getDefaultState());
            } else if (block instanceof NetherWartBlock) {
                worldIn.setBlockState(pos, block.getDefaultState().with(NetherWartBlock.AGE, 0));
            } else if (state.func_235901_b_(BlockStateProperties.AGE_0_3) && block instanceof BushBlock) { // Bushes
                worldIn.setBlockState(pos, block.getDefaultState().with(BlockStateProperties.AGE_0_3, 2));
            } else {
                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        } else {
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
        worldIn.notifyBlockUpdate(pos.up(), state, worldIn.getBlockState(pos), 3);
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
        PlayerEntity fake = FakePlayerFactory.get(worldIn, new GameProfile(null, "golem"));
        BlockRayTraceResult result = new BlockRayTraceResult(strawgolem.getPositionVec(),
                strawgolem.getHorizontalFacing().getOpposite(),
                pos,
                false);
        try {
            state.onBlockActivated(worldIn, fake, Hand.MAIN_HAND, result);
            MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickBlock(fake,
                    Hand.MAIN_HAND,
                    pos,
                    strawgolem.getHorizontalFacing().getOpposite()));
            List<ItemEntity> itemList = worldIn.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos).grow(2.5F));
            for (ItemEntity item : itemList) {
                strawgolem.getInventory().insertItem(0, item.getItem(), false);
                item.remove();
            }
        } catch (NullPointerException ex) {
            Strawgolem.logger.info(String.format("Golem could not harvest block at: %s", pos));
        }
        fake.remove(false);
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
