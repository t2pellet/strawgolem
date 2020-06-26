package com.commodorethrawn.strawgolem.entity.capability.memory;

import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class Memory implements IMemory {

    private final Set<BlockPos> posList;
    private BlockPos priority;

    public Memory() {
        posList = new HashSet<>();
        priority = BlockPos.ZERO;
    }

    @Override
    public Set<BlockPos> getPositions() {
        return posList;
    }

    @Override
    public BlockPos getDeliveryChest(BlockPos pos) {
        if (priority != BlockPos.ZERO) return priority;
        if (posList.size() == 0) return BlockPos.ZERO;
        BlockPos closest = BlockPos.ZERO;
        for (BlockPos chestPos : posList) {
            if (pos.distanceSq(closest) >= pos.distanceSq(chestPos)) {
                closest = chestPos;
            }
        }
        return closest;
    }

    @Override
    public void addPosition(BlockPos pos) {
        posList.add(pos);
    }

    @Override
    public void removePosition(BlockPos pos) {
        if (priority.equals(pos)) priority = BlockPos.ZERO;
        posList.remove(pos);
    }

    @Override
    public BlockPos getPriorityChest() {
        return priority;
    }

    @Override
    public void setPriorityChest(BlockPos pos) {
        priority = pos;
    }

}
