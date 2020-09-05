package com.commodorethrawn.strawgolem.entity.capability.memory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class Memory implements IMemory {

    private final Set<Pair<RegistryKey<World>, BlockPos>> posList;
    // location of preferred chest for delivery
    private BlockPos priority;
    // location of default wander anchor - where do we go when we've wandered enough?
    private BlockPos anchor;

    public Memory() {
        posList = new HashSet<>();
        priority = BlockPos.ZERO;
        anchor = BlockPos.ZERO;
    }

    @Override
    public Set<Pair<RegistryKey<World>, BlockPos>> getPositions() {
        return posList;
    }

    @Override
    public BlockPos getDeliveryChest(World world, BlockPos pos) {
        if (!priority.equals(BlockPos.ZERO)) {
            return priority;
        }
        BlockPos closest = BlockPos.ZERO;
        for (Pair<RegistryKey<World>, BlockPos> chestPos : posList) {
            if (!chestPos.getFirst().equals(world.func_234923_W_())) continue;
            if (pos.distanceSq(closest) >= pos.distanceSq(chestPos.getSecond())) {
                closest = chestPos.getSecond();
            }
        }
        return closest;
    }

    @Override
    public void addPosition(World world, BlockPos pos) {
        posList.add(Pair.of(world.func_234923_W_(), pos));
    }

    @Override
    public void removePosition(World world, BlockPos pos) {
        if (priority.equals(pos)) priority = BlockPos.ZERO;
        posList.remove(Pair.of(world.func_234923_W_(), pos));
    }

    @Override
    public BlockPos getPriorityChest() {
        return priority;
    }

    @Override
    public void setPriorityChest(BlockPos pos) {
        priority = pos;
    }

    @Override
    public BlockPos getAnchorPos() { return anchor; }

    @Override
    public void setAnchorPos(BlockPos pos) { anchor = pos; }

}
