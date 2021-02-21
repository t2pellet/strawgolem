package com.commodorethrawn.strawgolem.entity.capability.memory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

class MemoryImpl implements Memory {

    private final Set<Pair<RegistryKey<World>, BlockPos>> posList;
    // location of preferred chest for delivery
    private BlockPos priority;

    public MemoryImpl() {
        posList = new HashSet<>();
        priority = BlockPos.ORIGIN;
    }

    @Override
    public Set<Pair<RegistryKey<World>, BlockPos>> getPositions() {
        return posList;
    }

    @Override
    public BlockPos getDeliveryChest(World world, BlockPos pos) {
        if (!priority.equals(BlockPos.ORIGIN)) {
            return priority;
        }
        BlockPos closest = BlockPos.ORIGIN;
        for (Pair<RegistryKey<World>, BlockPos> chestPos : posList) {
            if (!chestPos.getFirst().equals(world.getRegistryKey())) continue;
            if (pos.getSquaredDistance(closest) >= pos.getSquaredDistance(chestPos.getSecond())) {
                closest = chestPos.getSecond();
            }
        }
        return closest;
    }

    @Override
    public void addPosition(World world, BlockPos pos) {
        posList.add(Pair.of(world.getRegistryKey(), pos));
    }

    @Override
    public void removePosition(World world, BlockPos pos) {
        if (priority.equals(pos)) priority = BlockPos.ORIGIN;
        posList.remove(Pair.of(world.getRegistryKey(), pos));
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
