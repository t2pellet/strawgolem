package com.t2pellet.strawgolem.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.Collections;

public interface CropRegistry {

    CropRegistry INSTANCE = new CropRegistryImpl();

    /**
     * Register a Block as a crop
     * @param id the block to register
     */
    <T extends BlockState> void register(Block id, IHarvestData<T> harvestData);

    /**
     * Register a Block as a crop
     *
     * @param id          the block to register
     * @param harvestData the data to register for the block
     */
    <T extends BlockEntity> void register(T id, IHarvestData<T> harvestData);

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
     * Harvest data for a registered crop
     * @param <T>
     */
    interface IHarvestData<T> {

        /**
         * @param input the input data
         * @return whether the crop is mature, based on the input
         */
        boolean isMature(T input);

        /**
         * Replant logic for the registered crop
         *
         * @param level the world
         * @param pos   the crop position
         * @param input input data
         */
        void doReplant(Level level, BlockPos pos, T input);
    }

    /**
     * Default implementation of IHarvestData, for convenience with blocks that use IntegerProperty for age
     */
    class DefaultHarvestData implements IHarvestData<BlockState> {

        private final IntegerProperty property;
        private final int harvestValue;
        private final int replantValue;

        public DefaultHarvestData(IntegerProperty property) {
            this(property, Collections.max(property.getPossibleValues()), Collections.min(property.getPossibleValues()));
        }

        public DefaultHarvestData(IntegerProperty property, int maxValue, int replantValue) {
            this.property = property;
            this.harvestValue = maxValue;
            this.replantValue = replantValue;
        }

        @Override
        public boolean isMature(BlockState input) {
            return input.getValue(property).equals(harvestValue);
        }

        @Override
        public void doReplant(Level level, BlockPos pos, BlockState input) {
            level.setBlockAndUpdate(pos, input.getBlock().defaultBlockState().setValue(property, replantValue));
        }
    }


}
