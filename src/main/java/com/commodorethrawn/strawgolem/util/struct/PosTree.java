package com.commodorethrawn.strawgolem.util.struct;

import net.minecraft.util.math.BlockPos;

import java.util.Iterator;

public interface PosTree {

    /**
     * Create a new PosTree instance
     * @return the instance
     */
    static PosTree create() {
        return new OctTree();
    }

    /**
     * Return whether the tree is empty
     * @return true if it has no nodes, false otherwise
     */
    boolean isEmpty();

    /**
     * Insert a BlockPos into the tree
     * @param pos the position to insert
     */
    void insert(BlockPos pos);

    /**
     * Delete a BlockPos from the tree, or do nothing if not present
     * @param pos the position to delete
     */
    void delete(BlockPos pos);

    /**
     * Finds the nearest BlockPos to the given position, returns null if empty
     * @param pos the query position
     * @return the nearest position
     */
    BlockPos findNearest(BlockPos pos);

    /**
     * Returns iterator of BlockPos in the tree
     * @return the iterator
     */
    Iterator<BlockPos> iterator();
}
