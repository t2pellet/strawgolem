package com.commodorethrawn.strawgolem.entity.capability.memory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Set;

public interface Memory {

    public static Memory create() {
        return new MemoryImpl();
    }

    /**
     * Returns the set of all remembered chest positions
     * @return the positions
     */
    Set<Pair<RegistryKey<World>, BlockPos>> getPositions();

    /**
     * Retrieves and returns the closest chest to the BlockPos pos
     * @param pos
     * @return the closest position
     */
    BlockPos getDeliveryChest(World world, BlockPos pos);

    /**
     * Adds pos to the remembered positions
     * @param pos
     */
    void addPosition(World world, BlockPos pos);

    /**
     * Removes pos from the remembered positions
     *
     * @param pos
     */
    void removePosition(World world, BlockPos pos);

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
