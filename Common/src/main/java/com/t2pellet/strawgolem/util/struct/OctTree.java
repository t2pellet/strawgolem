package com.t2pellet.strawgolem.util.struct;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

import java.util.Iterator;
import java.util.Stack;


class OctTree implements PosTree {

    private static final int NUM_CHILDREN = 8;

    private Vec3i point;
    private Vec3i bottomCorner;
    private Vec3i topCorner;
    private OctTree[] children;
    private int size;

    private OctTree() {
    }

    private OctTree(Vec3i point) {
        this.point = point;
        size = 1;
    }

    public OctTree(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        if (maxX < minX || maxY < minY || maxZ < minZ) {
            throw new IllegalArgumentException("Invalid OctTree boundaries");
        }
        bottomCorner = new Vec3i(minX, minY, minZ);
        topCorner = new Vec3i(maxX, maxY, maxZ);
        point = null;
        children = new OctTree[NUM_CHILDREN];
        for (int i = 0; i < NUM_CHILDREN; ++i) {
            children[i] = new OctTree();
        }
    }

    @Override
    public void insert(Vec3i pos) {
        if (comparePoints(pos, bottomCorner) != 7 || comparePoints(pos, topCorner) != 0) {
            throw new IllegalArgumentException("Attempting to insert point outside of boundary: " + pos);
        }

        Vec3i center = midPoint();
        int childLoc = comparePoints(pos, center);

        if (children[childLoc].size == 0) {
            // empty, turn into leaf
            children[childLoc] = new OctTree(pos);
        } else if (children[childLoc].point == null) {
            // region, pass along
            children[childLoc].insert(pos);
        } else {
            // leaf, turn into region
            Vec3i childPoint = children[childLoc].point;
            if (childPoint.equals(pos)) return;
            switch (childLoc) {
                case 7 ->
                        children[childLoc] = new OctTree(center.getX(), center.getY(), center.getZ(), topCorner.getX(), topCorner.getY(), topCorner.getZ());
                case 6 ->
                        children[childLoc] = new OctTree(center.getX(), center.getY(), bottomCorner.getZ(), topCorner.getX(), topCorner.getY(), center.getZ());
                case 5 ->
                        children[childLoc] = new OctTree(center.getX(), bottomCorner.getY(), center.getZ(), topCorner.getX(), center.getY(), topCorner.getZ());
                case 4 ->
                        children[childLoc] = new OctTree(center.getX(), bottomCorner.getY(), bottomCorner.getZ(), topCorner.getX(), center.getY(), center.getZ());
                case 3 ->
                        children[childLoc] = new OctTree(bottomCorner.getX(), center.getY(), center.getZ(), center.getX(), topCorner.getY(), topCorner.getZ());
                case 2 ->
                        children[childLoc] = new OctTree(bottomCorner.getX(), center.getY(), bottomCorner.getZ(), center.getX(), topCorner.getY(), center.getZ());
                case 1 ->
                        children[childLoc] = new OctTree(bottomCorner.getX(), bottomCorner.getY(), center.getZ(), center.getX(), center.getY(), topCorner.getZ());
                case 0 ->
                        children[childLoc] = new OctTree(bottomCorner.getX(), bottomCorner.getY(), bottomCorner.getZ(), center.getX(), center.getY(), center.getZ());
            }
            children[childLoc].insert(childPoint);
            children[childLoc].insert(pos);
        }
        ++size;
    }

    @Override
    public void delete(Vec3i pos) {
        if (comparePoints(pos, bottomCorner) != 7 || comparePoints(pos, topCorner) != 0) {
            throw new IllegalArgumentException("Attempting to delete point outside of boundary: " + pos);
        }

        Vec3i center = midPoint();
        int childLoc = comparePoints(pos, center);

        if (children[childLoc].size > 0) {
            if (children[childLoc].point == null) {
                // region, pass along
                children[childLoc].delete(pos);
            } else {
                // leaf, check and delete if match
                if (children[childLoc].point.equals(pos)) {
                    children[childLoc] = new OctTree();
                }
            }
            --size;
        }
    }

    @Override
    public BlockPos findNearest(Vec3i pos) {
        if (comparePoints(pos, bottomCorner) != 7 || comparePoints(pos, topCorner) != 0) {
            throw new IllegalArgumentException("Attempting to find nearest point outside of boundary: " + pos);
        }

        return (BlockPos) findNearest(this, pos);
    }

    static Vec3i findNearest(OctTree current, Vec3i pos) {
        Vec3i center = current.midPoint();
        int childLoc = comparePoints(pos, center);

        if (current.children[childLoc].size == 0) {
            // child is empty, check neighbours
            Vec3i candidate = null;
            double candidateDistance = Integer.MAX_VALUE;
            for (OctTree child : current.children) {
                if (child.size > 0) {
                    Vec3i newCandidate = findNearest(child, pos);
                    double newCandidateDistance = pos.distSqr(newCandidate);
                    if (newCandidateDistance < candidateDistance) {
                        candidate = newCandidate;
                        candidateDistance = newCandidateDistance;
                    }
                }
            }
            return candidate;
        } else if (current.children[childLoc].point == null) {
            // child is region, pass along
            return findNearest(current.children[childLoc], pos);
        } else {
            // child is leaf, return its point
            return current.children[childLoc].point;
        }
    }


    @Override
    public Iterator<BlockPos> iterator() {
        Stack<Vec3i> stack = new Stack<>();
        buildStack(stack, this);

        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return !stack.empty();
            }

            @Override
            public BlockPos next() {
                return (BlockPos) stack.pop();
            }
        };
    }

    private static void buildStack(Stack<Vec3i> stack, OctTree current) {
        for (OctTree child : current.children) {
            if (child.size > 0) {
                if (child.point != null) {
                    stack.push(child.point);
                } else {
                    buildStack(stack, child);
                }
            }
        }
    }

    private static int comparePoints(Vec3i point1, Vec3i point2) {
        if (point1.getX() < point2.getX()) {
            if (point1.getY() < point2.getY()) {
                return point1.getZ() < point2.getZ() ? 0 : 1;
            } else {
                return point1.getZ() < point2.getZ() ? 2 : 3;
            }
        } else {
            if (point1.getY() < point2.getY()) {
                return point1.getZ() < point2.getZ() ? 4 : 5;
            } else {
                return point1.getZ() < point2.getZ() ? 6 : 7;
            }
        }
    }

    private Vec3i midPoint() {
        int midX = bottomCorner.getX() + (topCorner.getX()) >> 1;
        int midY = bottomCorner.getY() + (topCorner.getY()) >> 1;
        int midZ = bottomCorner.getZ() + (topCorner.getZ()) >> 1;

        return new Vec3i(midX, midY, midZ);
    }
}
