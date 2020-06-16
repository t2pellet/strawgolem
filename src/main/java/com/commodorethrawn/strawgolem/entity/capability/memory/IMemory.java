package com.commodorethrawn.strawgolem.entity.capability.memory;

import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface IMemory {

    List<BlockPos> getPositionList();

    BlockPos getClosestPosition(BlockPos pos);

    boolean containsPosition(BlockPos pos);

    void addPosition(BlockPos pos);

    void removePosition(BlockPos pos);
}
