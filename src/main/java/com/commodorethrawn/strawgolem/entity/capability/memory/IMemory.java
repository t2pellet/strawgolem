package com.commodorethrawn.strawgolem.entity.capability.memory;

import net.minecraft.util.math.BlockPos;

import java.util.Set;

public interface IMemory {

    Set<BlockPos> getPositions();

    BlockPos getDeliveryChest(BlockPos pos);

    void addPosition(BlockPos pos);

    void removePosition(BlockPos pos);

    BlockPos getPriorityChest();

    void setPriorityChest(BlockPos pos);
}
