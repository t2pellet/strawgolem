package com.t2pellet.strawgolem.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

import java.util.Iterator;

public interface CropHandler {

    CropHandler INSTANCE = new CropHandlerImpl();

    /**
     * Reset / clear
     */
    void reset();

    /**
     * Add a crop
     * @param world the world the crop is in
     * @param pos the position of the crop
     */
    void addCrop(Level world, BlockPos pos);

    /**
     * Remove a crop, or do nothing if there is no such crop
     * @param world the world the crop is in
     * @param pos the position of the crop
     */
    void removeCrop(Level world, BlockPos pos);

    /**
     * Get the nearest crop
     * @param world the world to check
     * @param pos the position to check
     * @param maxRange the maximum acceptable range
     * @return the nearest BlockPos, null if none apply
     */
    BlockPos getNearestCrop(Level world, BlockPos pos, int maxRange);

    /**
     * Gets iterator for all the crops in that world
     * @param world the given world
     * @return the iterator for crops in that world
     */
    Iterator<BlockPos> getCrops(Level world);
}
