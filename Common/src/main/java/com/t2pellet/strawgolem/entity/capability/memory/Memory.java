package com.t2pellet.strawgolem.entity.capability.memory;

import com.mojang.datafixers.util.Pair;
import com.t2pellet.strawgolem.entity.capability.Capability;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Set;

public interface Memory extends Capability {

    static Memory getInstance() {
        return new MemoryImpl();
    }

    /**
     * Returns the set of all remembered chest positions
     * @return the positions
     */
    Set<Pair<ResourceKey<Level>, BlockPos>> getPositions();

    /**
     * Retrieves and returns the closest chest to the BlockPos pos
     * @param pos
     * @return the closest position
     */
    BlockPos getDeliveryChest(Level world, BlockPos pos);

    /**
     * Adds pos to the remembered positions
     * @param pos
     */
    void addPosition(Level world, BlockPos pos);

    /**
     * Removes pos from the remembered positions
     *
     * @param pos
     */
    void removePosition(Level world, BlockPos pos);

    /**
     * @return the priority chest
     */
    BlockPos getPriorityChest();

    /**
     * Sets the priority chest to pos
     * @param pos
     */
    void setPriorityChest(BlockPos pos);

}
