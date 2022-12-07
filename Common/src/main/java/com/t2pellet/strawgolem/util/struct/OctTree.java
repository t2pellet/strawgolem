package com.t2pellet.strawgolem.util.struct;

import com.t2pellet.strawgolem.StrawgolemCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

import java.util.*;

class OctTree implements PosTree {

    private static class OctPoint {
        private final int x;
        private final int y;
        private final int z;

        private OctPoint(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        private OctPoint(Vec3i point) {
            this.x = point.getX();
            this.y = point.getY();
            this.z = point.getZ();
        }

        BlockPos toBlockPos() {
            return new BlockPos(x, y, z);
        }

        int distApprox(OctPoint point) {
            int xDist = Math.abs(point.x - x);
            int yDist = Math.abs(point.y - y);
            int zDist = Math.abs(point.z - z);
            return xDist + yDist + zDist;
        }
        
        boolean isInRange(OctPoint point, int range) {
            int xDist = Math.abs(point.x - x);
            int yDist = Math.abs(point.y - y);
            int zDist = Math.abs(point.z - z);
            return xDist <= range && yDist <= range && zDist <= range;
        }
    }

    private static class OctBox {

        enum Octant {
            D_S_E,
            D_S_W,
            D_N_E,
            D_N_W,
            U_S_E,
            U_S_W,
            U_N_E,
            U_N_W
        }

        private final OctPoint bottomCorner;
        private final OctPoint topCorner;
        private final OctPoint midPoint;

        private OctBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            this(new OctPoint(minX, minY, minZ), new OctPoint(maxX, maxY, maxZ));
        }

        private OctBox(OctPoint bottomCorner, OctPoint topCorner) {
            if (topCorner.x < bottomCorner.x || topCorner.y < bottomCorner.y || topCorner.z < bottomCorner.z) {
                throw new IllegalArgumentException("Invalid OctTree boundaries");
            }

            this.bottomCorner = bottomCorner;
            this.topCorner = topCorner;
            this.midPoint = new OctPoint(
                    average(bottomCorner.x, topCorner.x),
                    average(bottomCorner.y, topCorner.y),
                    average(bottomCorner.z, topCorner.z));
        }

        private int average(int low, int high) {
            return low + ((high - low) >>> 1);
        }

        boolean isInBoundary(OctPoint point) {
            return bottomCorner.x <= point.x && bottomCorner.y <= point.y && bottomCorner.z <= point.z
                    && topCorner.x > point.x && topCorner.y > point.y && topCorner.z > point.z;
        }

        Octant getOctant(OctPoint point) {
            if (point.y >= midPoint.y) { // U
                if (point.z >= midPoint.z) { // S
                    if (point.x >= midPoint.x) { // E
                        return Octant.U_S_E;
                    } else { // W
                        return Octant.U_S_W;
                    }
                } else { // N
                    if (point.x >= midPoint.x) { // E
                        return Octant.U_N_E;
                    } else { // W
                        return Octant.U_N_W;
                    }
                }
            } else { // D
                if (point.z >= midPoint.z) { // S
                    if (point.x >= midPoint.x) { // E
                        return Octant.D_S_E;
                    } else { // W
                        return Octant.D_S_W;
                    }
                } else { // N
                    if (point.x >= midPoint.x) { // E
                        return Octant.D_N_E;
                    } else { // W
                        return Octant.D_N_W;
                    }
                }
            }
        }

        boolean isSubsetOf(OctBox other) {
            return other.isInBoundary(bottomCorner) && other.isInBoundary(topCorner);
        }

        boolean isDiscreteWith(OctBox other) {
            return other.bottomCorner.x > topCorner.x || other.bottomCorner.y > topCorner.y || other.bottomCorner.z > topCorner.z
                    || bottomCorner.x > other.topCorner.x || bottomCorner.y > other.topCorner.y || bottomCorner.z > other.topCorner.z;
        }

        OctBox getOctantBox(Octant octant) {
            OctPoint bottomCorner;
            OctPoint topCorner;
            switch (octant) {
                case D_N_E -> {
                    bottomCorner = new OctPoint(this.midPoint.x, this.bottomCorner.y, this.bottomCorner.z);
                    topCorner = new OctPoint(this.topCorner.x, this.midPoint.y, this.midPoint.z);
                }
                case D_N_W -> {
                    bottomCorner = this.bottomCorner;
                    topCorner = this.midPoint;
                }
                case D_S_E -> {
                    bottomCorner = new OctPoint(this.midPoint.x, this.bottomCorner.y, this.midPoint.z);
                    topCorner = new OctPoint(this.topCorner.x, this.midPoint.y, this.topCorner.z);
                }
                case D_S_W -> {
                    bottomCorner = new OctPoint(this.bottomCorner.x, this.bottomCorner.y, this.midPoint.z);
                    topCorner = new OctPoint(this.midPoint.x, this.midPoint.y, this.topCorner.z);
                }
                case U_N_E -> {
                    bottomCorner = new OctPoint(this.midPoint.x, this.midPoint.y, this.bottomCorner.z);
                    topCorner = new OctPoint(this.topCorner.x, this.topCorner.y, this.midPoint.z);
                }
                case U_N_W -> {
                    bottomCorner = new OctPoint(this.bottomCorner.x, this.midPoint.y, this.bottomCorner.z);
                    topCorner = new OctPoint(this.midPoint.x, this.topCorner.y, this.midPoint.z);
                }
                case U_S_E -> {
                    bottomCorner = this.midPoint;
                    topCorner = this.topCorner;
                }
                default -> { // U_S_W
                    bottomCorner = new OctPoint(this.bottomCorner.x, this.midPoint.y, this.midPoint.z);
                    topCorner = new OctPoint(this.midPoint.x, this.topCorner.y, this.topCorner.z);
                }
            }
            return new OctBox(bottomCorner, topCorner);
        }
    }

    private final OctBox boundary;
    private final Map<OctBox.Octant, Object> children;

    private OctTree(OctBox boundary) {
        this.boundary = boundary;
        this.children = new HashMap<>();
        for (OctBox.Octant octant : OctBox.Octant.values()) {
            this.children.put(octant, null);
        }
    }

    public OctTree(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this(new OctBox(minX, minY, minZ, maxX, maxY, maxZ));
    }

    @Override
    public void insert(final Vec3i vec3i) {
        insert(new OctPoint(vec3i));
    }

    private void insert(final OctPoint pos) {
        if (pos == null) {
            StrawgolemCommon.LOG.error("Tried inserting null BlockPos into OctTree, ignoring");
            return;
        }
        if (!boundary.isInBoundary(pos)) {
            StrawgolemCommon.LOG.error("Attempting to insert point outside of boundary: " + pos);
            return;
        }

        OctBox.Octant childOctant = boundary.getOctant(pos);
        Object child = children.get(childOctant);
        if (child == null) { // empty
            children.put(childOctant, pos);
        } else if (child instanceof OctPoint point) { // point node
            OctTree tree = new OctTree(boundary.getOctantBox(childOctant));
            children.put(childOctant, tree);
            tree.insert(point);
            tree.insert(pos);
        } else { // region node
            OctTree childTree = (OctTree) child;
            childTree.insert(pos);
        }
    }

    @Override
    public void delete(final Vec3i pos) {
        delete(new OctPoint(pos));
    }

    private void delete(final OctPoint pos) {
        if (pos == null) {
            StrawgolemCommon.LOG.error("Tried deleting null pos from OctTree, ignoring");
            return;
        }
        if (!boundary.isInBoundary(pos)) {
            StrawgolemCommon.LOG.error("Attempting to delete point outside of boundary: " + pos);
            return;
        }

        OctBox.Octant childOctant = boundary.getOctant(pos);
        Object child = children.get(childOctant);
        if (child == null) { // empty
            StrawgolemCommon.LOG.error("Tried deleting pos from null child tree, ignoring");
        } else if (child instanceof OctPoint) { // point node
            children.put(childOctant, null);
        } else { // region node
            OctTree childTree = (OctTree) child;
            childTree.delete(pos);
        }
    }

    @Override
    public BlockPos findNearest(Vec3i pos, int maxRange) {
        OctPoint result = findNearest(new OctPoint(pos), maxRange);
        return result == null ? null : result.toBlockPos();
    }

    private OctPoint findNearest(OctPoint pos, int maxRange) {
        if (pos == null) {
            StrawgolemCommon.LOG.error("Tried to find nearest pos to null query - returning null");
            return null;
        }
        if (!boundary.isInBoundary(pos)) {
            StrawgolemCommon.LOG.error("Attempting to find nearest point outside of boundary: " + pos);
            return null;
        }

        OctBox rangeBoundary = new OctBox(
                pos.x - maxRange, pos.y - maxRange, pos.z - maxRange,
                pos.x + maxRange + 1, pos.y + maxRange + 1, pos.z + maxRange + 1);
        Queue<OctPoint> pointsInRange = rangeSearch(rangeBoundary);
        if (pointsInRange != null) {
            OctPoint closest = null;
            int closestDistance = Integer.MAX_VALUE;
            while (!pointsInRange.isEmpty()) {
                OctPoint current = pointsInRange.poll();
                if (closest == null || current.distApprox(pos) < closestDistance) {
                    closestDistance = current.distApprox(pos);
                    closest = current;
                }
            }
            return closest;
        }
        return null;
    }

    private Queue<OctPoint> rangeSearch(OctBox rangeBoundary) {
        if (boundary.isSubsetOf(rangeBoundary)) {
            // report all
            Queue<OctPoint> octQueue = new ArrayDeque<>();
            return buildQueue(octQueue, this);
        } else if (boundary.isDiscreteWith(rangeBoundary)) {
            // nothing to report
            return null;
        }

        Queue<OctPoint> result = new ArrayDeque<>();
        for (OctBox.Octant octant : OctBox.Octant.values()) {
            Object child = children.get(octant);
            if (child instanceof OctPoint point) {
                if (rangeBoundary.isInBoundary(point)) {
                    result.offer(point);
                }
            } else if (child != null) {
                OctTree tree = (OctTree) child;
                Queue<OctPoint> childQueue = tree.rangeSearch(rangeBoundary);
                if (childQueue != null) result.addAll(childQueue);
            }
        }
        return result;
    }


    @Override
    public Iterator<BlockPos> iterator() {
        Queue<OctPoint> queue = new ArrayDeque<>();
        buildQueue(queue, this);

        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return !queue.isEmpty();
            }

            @Override
            public BlockPos next() {
                OctPoint point = queue.poll();
                return point == null ? null : point.toBlockPos();
            }
        };
    }

    private static Queue<OctPoint> buildQueue(Queue<OctPoint> stack, OctTree current) {
        for (OctBox.Octant octant : OctBox.Octant.values()) {
            Object child = current.children.get(octant);
            if (child instanceof OctPoint point) {
                stack.offer(point);
            } else if (child != null) {
                OctTree tree = (OctTree) child;
                buildQueue(stack, tree);
            }
        }
        return stack;
    }
}
