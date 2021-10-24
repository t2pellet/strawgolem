package com.commodorethrawn.strawgolem.crop;

import com.commodorethrawn.strawgolem.util.struct.PosTree;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

class CropHandlerImpl implements CropHandler {
    
    HashMap<RegistryKey<World>, PosTree> treeMap;

    CropHandlerImpl() {
        treeMap = new HashMap<>();
    }

    @Override
    public void reset() {
        treeMap = new HashMap<>();
    }

    @Override
    public void addCrop(World world, BlockPos pos) {
        if (treeMap.containsKey(world.getRegistryKey())) {
            treeMap.get(world.getRegistryKey()).insert(pos);
        } else {
            PosTree tree = PosTree.create();
            tree.insert(pos);
            treeMap.put(world.getRegistryKey(), tree);
        }
    }

    @Override
    public void removeCrop(World world, BlockPos pos) {
        if (treeMap.containsKey(world.getRegistryKey())) {
            treeMap.get(world.getRegistryKey()).delete(pos);
        }
    }

    @Override
    public BlockPos getNearestCrop(World world, BlockPos pos, int maxRange) {
        if (treeMap.containsKey(world.getRegistryKey())) {
            BlockPos closest = treeMap.get(world.getRegistryKey()).findNearest(pos);
            if (closest == null || pos.getManhattanDistance(closest) > maxRange) return null;
            return closest;
        }
        // Half range for scanning, improves performance
        return scanForNearestCrop(world, pos, maxRange / 2);
    }

    @Override
    public Iterator<BlockPos> getCrops(World world) {
        if (treeMap.containsKey(world.getRegistryKey())) return treeMap.get(world.getRegistryKey()).iterator();
        return Collections.emptyIterator();
    }

    private BlockPos scanForNearestCrop(World world, BlockPos pos, int maxRange) {
        BlockPos currPos;
        for (int i = -maxRange; i < maxRange; ++i) {
            for (int j = -maxRange; j < maxRange; ++j) {
                for (int k = -maxRange; k < maxRange; ++k) {
                    currPos = pos.add(i, j, k);
                    if (CropValidator.isGrownCrop(world.getBlockState(currPos))) {
                        return currPos;
                    }
                }
            }
        }
        return null;
    }
}
