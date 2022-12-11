package com.t2pellet.strawgolem.util.struct;

import net.minecraft.core.Vec3i;

import java.util.Iterator;

public interface PosTree {

    /**
     * Create a new PosTree instance
     * @return the instance
     */
    static PosTree create() {
        return new OctTree(Integer.MIN_VALUE + 1, Integer.MIN_VALUE + 1, Integer.MIN_VALUE + 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Insert a BlockPos into the tree
     *
     * @param pos the position to insert
     */
    void insert(final Vec3i pos);

    /**
     * Delete a BlockPos from the tree, or do nothing if not present
     *
     * @param pos the position to delete
     */
    void delete(final Vec3i pos);

    /**
     * Finds the nearest BlockPos to the given position, returns null if empty
     *
     * @param pos the query position
     * @return the nearest position
     */
    Vec3i findNearest(Vec3i pos, int maxRange);

    /**
     * Returns iterator of BlockPos in the tree
     * @return the iterator
     */
    Iterator<Vec3i> iterator();
}
