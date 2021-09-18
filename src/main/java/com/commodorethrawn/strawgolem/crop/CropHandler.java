package com.commodorethrawn.strawgolem.crop;

import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

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
    void addCrop(World world, BlockPos pos);

    /**
     * Remove a crop, or do nothing if there is no such crop
     * @param world the world the crop is in
     * @param pos the position of the crop
     */
    void removeCrop(World world, BlockPos pos);

    /**
     * Get the nearest crop
     * @param world the world to check
     * @param pos the position to check
     * @param maxRange the maximum acceptable range
     * @return the nearest BlockPos, null if none apply
     */
    BlockPos getNearestCrop(World world, BlockPos pos, int maxRange);

    /**
     * Iterate through all worlds and corresponding blockposes
     * @return the iterator
     */
    Iterator<Pair<RegistryKey<World>, Iterator<BlockPos>>> iterator();
}
