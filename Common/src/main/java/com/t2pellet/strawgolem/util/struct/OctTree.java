package com.t2pellet.strawgolem.util.struct;

import com.t2pellet.strawgolem.StrawgolemCommon;
import net.minecraft.core.Vec3i;

import java.util.*;

class OctTree implements PosTree {

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

        private final Vec3i bottomCorner;
        private final Vec3i topCorner;
        private final Vec3i midPoint;

        private OctBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            this(new Vec3i(minX, minY, minZ), new Vec3i(maxX, maxY, maxZ));
        }

        private OctBox(Vec3i bottomCorner, Vec3i topCorner) {
            if (topCorner.getX() < bottomCorner.getX() || topCorner.getY() < bottomCorner.getY() || topCorner.getZ() < bottomCorner.getZ()) {
                throw new IllegalArgumentException("Invalid OctTree boundaries");
            }

            this.bottomCorner = bottomCorner;
            this.topCorner = topCorner;
            this.midPoint = new Vec3i(
                    average(bottomCorner.getX(), topCorner.getX()),
                    average(bottomCorner.getY(), topCorner.getY()),
                    average(bottomCorner.getZ(), topCorner.getZ()));
        }

        private int average(int low, int high) {
            return low + ((high - low) >>> 1);
        }

        boolean isInBoundary(Vec3i point) {
            return bottomCorner.getX() <= point.getX() && bottomCorner.getY() <= point.getY() && bottomCorner.getZ() <= point.getZ()
                    && topCorner.getX() > point.getX() && topCorner.getY() > point.getY() && topCorner.getZ() > point.getZ();
        }

        Octant getOctant(Vec3i point) {
            if (point.getY() >= midPoint.getY()) { // U
                if (point.getZ() >= midPoint.getZ()) { // S
                    if (point.getX() >= midPoint.getX()) { // E
                        return Octant.U_S_E;
                    } else { // W
                        return Octant.U_S_W;
                    }
                } else { // N
                    if (point.getX() >= midPoint.getX()) { // E
                        return Octant.U_N_E;
                    } else { // W
                        return Octant.U_N_W;
                    }
                }
            } else { // D
                if (point.getZ() >= midPoint.getZ()) { // S
                    if (point.getX() >= midPoint.getX()) { // E
                        return Octant.D_S_E;
                    } else { // W
                        return Octant.D_S_W;
                    }
                } else { // N
                    if (point.getX() >= midPoint.getX()) { // E
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
            return other.bottomCorner.getX() > topCorner.getX() || other.bottomCorner.getY() > topCorner.getY() || other.bottomCorner.getZ() > topCorner.getZ()
                    || bottomCorner.getX() > other.topCorner.getX() || bottomCorner.getY() > other.topCorner.getY() || bottomCorner.getZ() > other.topCorner.getZ();
        }

        OctBox getOctantBox(Octant octant) {
            Vec3i bottomCorner;
            Vec3i topCorner;
            switch (octant) {
                case D_N_E -> {
                    bottomCorner = new Vec3i(this.midPoint.getX(), this.bottomCorner.getY(), this.bottomCorner.getZ());
                    topCorner = new Vec3i(this.topCorner.getX(), this.midPoint.getY(), this.midPoint.getZ());
                }
                case D_N_W -> {
                    bottomCorner = this.bottomCorner;
                    topCorner = this.midPoint;
                }
                case D_S_E -> {
                    bottomCorner = new Vec3i(this.midPoint.getX(), this.bottomCorner.getY(), this.midPoint.getZ());
                    topCorner = new Vec3i(this.topCorner.getX(), this.midPoint.getY(), this.topCorner.getZ());
                }
                case D_S_W -> {
                    bottomCorner = new Vec3i(this.bottomCorner.getX(), this.bottomCorner.getY(), this.midPoint.getZ());
                    topCorner = new Vec3i(this.midPoint.getX(), this.midPoint.getY(), this.topCorner.getZ());
                }
                case U_N_E -> {
                    bottomCorner = new Vec3i(this.midPoint.getX(), this.midPoint.getY(), this.bottomCorner.getZ());
                    topCorner = new Vec3i(this.topCorner.getX(), this.topCorner.getY(), this.midPoint.getZ());
                }
                case U_N_W -> {
                    bottomCorner = new Vec3i(this.bottomCorner.getX(), this.midPoint.getY(), this.bottomCorner.getZ());
                    topCorner = new Vec3i(this.midPoint.getX(), this.topCorner.getY(), this.midPoint.getZ());
                }
                case U_S_E -> {
                    bottomCorner = this.midPoint;
                    topCorner = this.topCorner;
                }
                default -> { // U_S_W
                    bottomCorner = new Vec3i(this.bottomCorner.getX(), this.midPoint.getY(), this.midPoint.getZ());
                    topCorner = new Vec3i(this.midPoint.getX(), this.topCorner.getY(), this.topCorner.getZ());
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
    public void insert(final Vec3i pos) {
        if (pos == null) {
            StrawgolemCommon.LOG.error("Tried inserting null BlockPos into OctTree, ignoring");
            return;
        }
        if (!boundary.isInBoundary(pos)) {
            StrawgolemCommon.LOG.error("Attempting to insert point outside of OctTree boundary");
            return;
        }

        OctBox.Octant childOctant = boundary.getOctant(pos);
        Object child = children.get(childOctant);
        if (child == null) { // empty
            children.put(childOctant, pos);
        } else if (child instanceof Vec3i point) { // point node
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
        } else if (child instanceof Vec3i) { // point node
            children.put(childOctant, null);
        } else { // region node
            OctTree childTree = (OctTree) child;
            childTree.delete(pos);
        }
    }

    @Override
    public Vec3i findNearest(Vec3i pos, int maxRange) {
        if (pos == null) {
            StrawgolemCommon.LOG.error("Tried to find nearest pos to null query - returning null");
            return null;
        }
        if (!boundary.isInBoundary(pos)) {
            StrawgolemCommon.LOG.error("Attempting to find nearest point outside of boundary: " + pos);
            return null;
        }

        OctBox rangeBoundary = new OctBox(
                pos.getX() - maxRange, pos.getY() - maxRange, pos.getZ() - maxRange,
                pos.getX() + maxRange + 1, pos.getY() + maxRange + 1, pos.getZ() + maxRange + 1);
        Queue<Vec3i> pointQueue = new PriorityQueue<>(Comparator.comparingInt(o -> o.distManhattan(pos)));
        rangeSearch(rangeBoundary, pointQueue);
        return pointQueue.poll();
    }

    private void rangeSearch(final OctBox rangeBoundary, final Queue<Vec3i> result) {
        if (boundary.isSubsetOf(rangeBoundary)) {
            // report all
            buildQueue(result, this);
            return;
        } else if (boundary.isDiscreteWith(rangeBoundary)) {
            // nothing to report
            return;
        }

        for (OctBox.Octant octant : OctBox.Octant.values()) {
            Object child = children.get(octant);
            if (child instanceof Vec3i point) {
                if (rangeBoundary.isInBoundary(point)) {
                    result.offer(point);
                }
            } else if (child != null) {
                OctTree tree = (OctTree) child;
                tree.rangeSearch(rangeBoundary, result);
            }
        }
    }


    @Override
    public Iterator<Vec3i> iterator() {
        Queue<Vec3i> queue = new ArrayDeque<>();
        buildQueue(queue, this);

        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return !queue.isEmpty();
            }

            @Override
            public Vec3i next() {
                return queue.poll();
            }
        };
    }

    private static void buildQueue(Queue<Vec3i> stack, OctTree current) {
        for (OctBox.Octant octant : OctBox.Octant.values()) {
            Object child = current.children.get(octant);
            if (child instanceof Vec3i point) {
                stack.offer(point);
            } else if (child != null) {
                OctTree tree = (OctTree) child;
                buildQueue(stack, tree);
            }
        }
    }
}
