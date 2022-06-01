package com.t2pellet.strawgolem.crop;

import com.mojang.authlib.GameProfile;
import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface CropRegistry {

    CropRegistry INSTANCE = new CropRegistryImpl();

    /**
     * Register a Block as a crop
     *
     * @param id the block to register
     */
    <T extends BlockState> void register(Block id, IHarvestChecker<T> harvestChecker, IHarvestLogic<T> harvestLogic, IReplantLogic<T> replantLogic);

    /**
     * Register a Block as a crop
     *
     * @param id the block to register
     */
    <T extends BlockEntity> void register(BlockEntityType<T> id, IHarvestChecker<T> harvestChecker, IHarvestLogic<T> harvestLogic, IReplantLogic<T> replantLogic);

    /**
     * Determine if crop is grown
     * @param state the state to check
     * @return whether the BlockState represents a grown crop
     */
    <T extends BlockState> boolean isGrownCrop(T state);

    /**
     * Determine if crop is grown
     * @param block the blockentity to check
     * @return whether the BlockEntity represents a grown crop
     */
    <T extends BlockEntity> boolean isGrownCrop(T block);

    /**
     * Handle replant for the given crop, if registered
     *
     * @param level the world
     * @param pos   the crop position
     */
    void handleReplant(Level level, BlockPos pos);

    /**
     * Handle harvest for the given crop, if registered
     *
     * @param level the world
     * @param golem the harvesting golem
     * @param pos   the crop position
     * @return the items to harvest
     */
    List<ItemStack> handleHarvest(ServerLevel level, EntityStrawGolem golem, BlockPos pos);

    /**
     * Predicate for checking if the registered crop T is mature
     *
     * @param <T> the registered crop
     */
    interface IHarvestChecker<T> {

        static IHarvestChecker<BlockState> getDefault(IntegerProperty property) {
            return getDefault(property, Collections.max(property.getPossibleValues()));
        }

        static IHarvestChecker<BlockState> getDefault(IntegerProperty property, int harvestValue) {
            return input -> input.getValue(property).equals(harvestValue);
        }

        /**
         * @param input the input data
         * @return whether the crop is mature, based on the input
         */
        boolean isMature(T input);
    }

    /**
     * Replant logic for the registered crop T
     *
     * @param <T> the registered crop
     */
    interface IReplantLogic<T> {

        static IReplantLogic<BlockState> getDefault(IntegerProperty property) {
            return getDefault(property, 0);
        }

        static IReplantLogic<BlockState> getDefault(IntegerProperty property, int replantValue) {
            return (level, pos, input) -> level.setBlockAndUpdate(pos, input.getBlock().defaultBlockState().setValue(property, replantValue));
        }

        /**
         * @param level the world
         * @param pos   the crop position
         * @param input input data
         */
        void doReplant(Level level, BlockPos pos, T input);
    }

    /**
     * Harvest logic for the registered crop T
     *
     * @param <T> the registered crop
     */
    interface IHarvestLogic<T> {

        IHarvestLogic<BlockState> DEFAULT = (worldIn, golem, pos, state) -> {
            List<ItemStack> drops = Block.getDrops(state, worldIn, pos, worldIn.getBlockEntity(pos));
            return drops.stream().filter(CropRegistry::isCropDrop).collect(Collectors.toList());
        };

        IHarvestLogic<BlockState> RIGHT_CLICK = (worldIn, golem, pos, state) -> {
            GameProfile fakeProfile = new GameProfile(UUID.randomUUID(), golem.getScoreboardName());
            ServerPlayer fake = new ServerPlayer(worldIn.getServer(), worldIn, fakeProfile);
            fake.setPos(golem.getX(), golem.getY(), golem.getZ());
            BlockHitResult result = new BlockHitResult(golem.position(),
                    golem.getDirection().getOpposite(),
                    pos,
                    false);
            try {
                state.use(worldIn, fake, InteractionHand.MAIN_HAND, result);
                List<ItemEntity> itemList = worldIn.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(2.5F), e -> true);
                return itemList.stream().map(ItemEntity::getItem).collect(Collectors.toList());
            } catch (NullPointerException ex) {
                StrawgolemCommon.LOG.error(String.format("Golem could not harvest block at: %s", pos));
            } finally {
                fake.remove(Entity.RemovalReason.DISCARDED);
            }
            return null;
        };

        /**
         * @param level the world
         * @param pos   the crop position
         * @param input the crop
         * @return the list of items to harvest
         */
        List<ItemStack> doHarvest(ServerLevel level, EntityStrawGolem golem, BlockPos pos, T input);
    }

    private static boolean isCropDrop(ItemStack drop) {
        return !(drop.getItem() instanceof BlockItem)
                || drop.getUseAnimation() == UseAnim.EAT
                || drop.getItem() == Items.NETHER_WART;
    }


}
