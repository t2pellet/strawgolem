package com.t2pellet.strawgolem.crop;

import com.t2pellet.strawgolem.util.struct.PosTree;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

class CropHandlerImpl implements CropHandler {

    HashMap<ResourceKey<Level>, PosTree> treeMap;

    CropHandlerImpl() {
        treeMap = new HashMap<>();
    }

    @Override
    public void reset() {
        treeMap = new HashMap<>();
    }

    @Override
    public void addCrop(Level world, BlockPos pos) {
        if (treeMap.containsKey(world.dimension())) {
            treeMap.get(world.dimension()).insert(pos);
        } else {
            PosTree tree = PosTree.create();
            tree.insert(pos);
            treeMap.put(world.dimension(), tree);
        }
    }

    @Override
    public void removeCrop(Level world, BlockPos pos) {
        if (treeMap.containsKey(world.dimension())) {
            treeMap.get(world.dimension()).delete(pos);
        }
    }

    @Override
    public BlockPos getNearestCrop(Level world, BlockPos pos, int maxRange) {
        if (treeMap.containsKey(world.dimension())) {
            BlockPos closest = treeMap.get(world.dimension()).findNearest(pos);
            if (closest == null || pos.distSqr(closest) > maxRange * maxRange) return null;
            return closest;
        }
        return null;
    }

    @Override
    public Iterator<BlockPos> getCrops(Level world) {
        if (treeMap.containsKey(world.dimension())) return treeMap.get(world.dimension()).iterator();
        return Collections.emptyIterator();
    }
}
