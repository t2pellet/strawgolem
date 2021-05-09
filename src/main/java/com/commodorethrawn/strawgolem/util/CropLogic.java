package com.commodorethrawn.strawgolem.util;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
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

import java.util.List;
import java.util.Map;

public class CropLogic {

    private static final Map<ResourceLocation, Impl> LOGIC_MAP = Maps.newIdentityHashMap();

    public static final Impl DEFAULT = new Impl() {
        @Override
        public boolean isFullyGrown(BlockState state, IWorldReader world, BlockPos pos) {
            if (state.getBlock() instanceof CropsBlock) {
                CropsBlock crop = (CropsBlock) state.getBlock();
                return crop.isMaxAge(state);
            } else if (state.getBlock() instanceof StemGrownBlock) {
                return true;
            } else if (state.getBlock() instanceof NetherWartBlock) {
                return state.get(NetherWartBlock.AGE) == 3;
            } else if (state.getBlock() instanceof BushBlock
                    && state.getBlock() instanceof IGrowable
                    && state.func_235901_b_(BlockStateProperties.AGE_0_3)) {
                return state.get(BlockStateProperties.AGE_0_3) == 3;
            }
            return false;
        }

        @Override
        public void handleHarvest(EntityStrawGolem golem, ServerWorld world, BlockPos pos, BlockState state) {
            world.playSound(null, pos, SoundEvents.BLOCK_CROP_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            doPickup(golem, world, pos, state, state.getBlock());
            doReplant(world, pos, state, state.getBlock());
        }
    };

    public static void registerLogic(Block block, Impl logic)  {
        LOGIC_MAP.put(block.getRegistryName(), logic);
    }

    public static Impl getLogic(BlockState state) {
        return LOGIC_MAP.getOrDefault(state.getBlock().getRegistryName(), DEFAULT);
    }

    public interface Impl {
        boolean isFullyGrown(BlockState state, IWorldReader world, BlockPos pos);

        void handleHarvest(EntityStrawGolem golem, ServerWorld world, BlockPos pos, BlockState state);
    }

    /**
     * Handles the logic for picking up the harvests
     * @param worldIn : the world
     * @param pos : the position of the crop
     * @param state : the BlockState of the crop
     * @param block : the Block of the crop
     */
    private static void doPickup(EntityStrawGolem golem, ServerWorld worldIn, BlockPos pos, BlockState state, Block block) {
        if (ConfigHelper.isDeliveryEnabled()) {
            if (block instanceof StemGrownBlock) {
                golem.playSound(EntityStrawGolem.GOLEM_STRAINED, 1.0F, 1.0F);
                golem.getInventory().insertItem(0, new ItemStack(Item.BLOCK_TO_ITEM.getOrDefault(block, Items.AIR)), false);
            } else if (block instanceof CropsBlock || block instanceof NetherWartBlock) {
                List<ItemStack> drops = Block.getDrops(state, worldIn, pos, worldIn.getTileEntity(pos));
                for (ItemStack drop : drops) {
                    if (isCropDrop(drop)) {
                        golem.getInventory().insertItem(0, drop, false);
                    } else if (drop.getItem() instanceof BlockItem && !(drop.getItem() instanceof BlockNamedItem)) {
                        golem.playSound(EntityStrawGolem.GOLEM_STRAINED, 1.0F, 1.0F);
                        golem.getInventory().insertItem(0, drop, false);
                        break;
                    }
                }
            } else fakeRightClick(golem, worldIn, pos, state); //Bushes
        }
    }

    /**
     * Handles the replanting logic
     * @param worldIn : the world
     * @param pos : the position of the crop
     * @param state : the BlockState of the crop
     * @param block : the Block of the crop
     */
    private static void doReplant(ServerWorld worldIn, BlockPos pos, BlockState state, Block block) {
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
    private static void fakeRightClick(EntityStrawGolem golem, ServerWorld worldIn, BlockPos pos, BlockState state) {
        PlayerEntity fake = FakePlayerFactory.get(worldIn, new GameProfile(null, "golem"));
        BlockRayTraceResult result = new BlockRayTraceResult(golem.getPositionVec(),
                golem.getHorizontalFacing().getOpposite(),
                pos,
                false);
        try {
            state.onBlockActivated(worldIn, fake, Hand.MAIN_HAND, result);
            MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickBlock(fake,
                    Hand.MAIN_HAND,
                    pos,
                    golem.getHorizontalFacing().getOpposite()));
            List<ItemEntity> itemList = worldIn.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos).grow(2.5F));
            for (ItemEntity item : itemList) {
                golem.getInventory().insertItem(0, item.getItem(), false);
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
    private static boolean isCropDrop(ItemStack drop) {
        return !(drop.getItem() instanceof BlockItem)
                || drop.getUseAction() == UseAction.EAT
                || drop.getItem() == Items.NETHER_WART;
    }
}
