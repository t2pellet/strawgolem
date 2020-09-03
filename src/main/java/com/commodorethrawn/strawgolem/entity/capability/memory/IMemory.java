package com.commodorethrawn.strawgolem.entity.capability.memory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.SpriteAwareVertexBuilder;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.World;

import java.util.Set;

public interface IMemory {
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

    /**
     * @return the anchor location
     */
    BlockPos getAnchorPos();

    /**
     * Sets the anchor position
     * @param pos
     */
    void setAnchorPos(BlockPos pos);
}
