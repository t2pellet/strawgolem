package com.t2pellet.strawgolem.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.Iterator;

public interface WorldCrops {

    String DATA_KEY = "WorldCrops";

    static WorldCrops of(Level level) {
        DimensionDataStorage dataStorage = ((ServerLevel) level).getDataStorage();
        return dataStorage.computeIfAbsent(tag -> WorldCropsImpl.load(level, tag), () -> new WorldCropsImpl(level), DATA_KEY);
    }

    /**
     * Reset / clear
     */
    void reset();

    /**
     * Add a crop
     *
     * @param pos the position of the crop
     */
    void addCrop(BlockPos pos);

    /**
     * Remove a crop, or do nothing if there is no such crop
     *
     * @param pos   the position of the crop
     */
    void removeCrop(BlockPos pos);

    /**
     * Get the nearest crop
     *
     * @param pos      the position to check
     * @param maxRange the maximum acceptable range
     * @return the nearest BlockPos, null if none apply
     */
    BlockPos getNearestCrop(BlockPos pos, int maxRange);

    /**
     * Gets iterator for all the crops in that world
     *
     * @return the iterator for crops in that world
     */
    Iterator<BlockPos> getCrops();
}
