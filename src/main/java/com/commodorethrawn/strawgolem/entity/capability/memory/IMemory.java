package com.commodorethrawn.strawgolem.entity.capability.memory;

import net.minecraft.util.math.BlockPos;

import java.util.Set;

public interface IMemory {

    /**
     * Returns the set of all remembered chest positions
     *
     * @return the positions
     */
    Set<BlockPos> getPositions();

    /**
     * Retrieves and returns the closest chest to the BlockPos pos
     *
     * @param pos
     * @return the closest position
     */
    BlockPos getDeliveryChest(BlockPos pos);

    /**
     * Adds pos to the remembered positions
     *
     * @param pos
     */
    void addPosition(BlockPos pos);

    /**
     * Removes pos from the remembered positions
     *
     * @param pos
     */
    void removePosition(BlockPos pos);

    /**
     * @return the priority chest
     */
    BlockPos getPriorityChest();

    /**
     * Sets the priority chest to pos
     *
     * @param pos
     */
    void setPriorityChest(BlockPos pos);
}
