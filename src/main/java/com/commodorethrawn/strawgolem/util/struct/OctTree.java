package com.commodorethrawn.strawgolem.util.struct;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.*;

class OctBox {

    final int minX, minY, minZ;
    final int maxX, maxY, maxZ;
    final BlockPos center;

    public OctBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        center = calculateCenter();
    }

    private BlockPos calculateCenter() {
        int centerX = (minX + maxX) / 2;
        int centerY = (minY + maxY) / 2;
        int centerZ = (minZ + maxZ) / 2;
        return new BlockPos(centerX, centerY, centerZ);
    }

}

public class OctTree implements PosTree {

    private static final OctBox DEFAULT = new OctBox(-2147483645, -2147483645, -2147483645, 2147483645, 2147483645, 2147483645);
    private static final HashMap<Triplet<Boolean, Boolean, Boolean>, Octant> octants = new HashMap<>();
    static {
        octants.put(new Triplet<>(true, true, true), Octant.FIRST);
        octants.put(new Triplet<>(true, true, false), Octant.SECOND);
        octants.put(new Triplet<>(false, true, true), Octant.THIRD);
        octants.put(new Triplet<>(false, true, false), Octant.FOURTH);
        octants.put(new Triplet<>(true, false, true), Octant.FIFTH);
        octants.put(new Triplet<>(true, false, false), Octant.SIXTH);
        octants.put(new Triplet<>(false, false, true), Octant.SEVENTH);
        octants.put(new Triplet<>(false, false, false), Octant.EIGHTH);  
    }

    
    private final OctTree parent;
    private final OctBox boundary;
    private final Vec3i center;
    private final HashMap<Octant, OctTree> octTrees = new HashMap<>();
    private BlockPos point;

    private enum Octant {
        FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH, SEVENTH, EIGHTH
    }

    OctTree() {
        parent = null;
        boundary = DEFAULT;
        center = BlockPos.ORIGIN;
        buildMaps();
    }

    public OctTree(final OctTree parent, final Octant octant) {
        this.parent = parent;
        switch (octant) {
            case FIRST:
                boundary = new OctBox(parent.boundary.minX, parent.boundary.minY, parent.boundary.minZ, parent.center.getX(), parent.center.getY(), parent.center.getZ());
                break;
            case SECOND:
                boundary = new OctBox(parent.boundary.minX, parent.boundary.minY, parent.center.getZ(), parent.center.getX(), parent.center.getY(), parent.boundary.maxZ);
                break;
            case THIRD:
                boundary = new OctBox(parent.center.getX(), parent.boundary.minY, parent.boundary.minZ, parent.boundary.maxX, parent.center.getY(), parent.center.getZ());
                break;
            case FOURTH:
                boundary = new OctBox(parent.center.getX(), parent.boundary.minY, parent.center.getZ(), parent.boundary.maxX, parent.center.getY(), parent.boundary.maxZ);
                break;
            case FIFTH:
                boundary = new OctBox(parent.boundary.minX, parent.center.getY(), parent.boundary.minZ, parent.center.getX(), parent.boundary.maxY, parent.center.getZ());
                break;
            case SIXTH:
                boundary = new OctBox(parent.boundary.minX, parent.center.getY(), parent.center.getZ(), parent.center.getX(), parent.boundary.maxY, parent.boundary.maxZ);
                break;
            case SEVENTH:
                boundary = new OctBox(parent.center.getX(), parent.center.getY(), parent.boundary.minZ, parent.boundary.maxX, parent.boundary.maxY, parent.center.getZ());
                break;
            case EIGHTH:
            default:
                boundary = new OctBox(parent.center.getX(), parent.center.getY(), parent.center.getZ(), parent.boundary.maxX, parent.boundary.maxY, parent.boundary.maxZ);
                break;
        }
        center = boundary.center;
        buildMaps();
    }

    // ADT methods

    @Override
    public void insert(BlockPos pos) {
        if (pos == null) return;
        OctTree result = search(pos);
        if (pos.equals(result.point)) return;

        if (result.isEmpty()) {
            result.point = pos;
        } else if (result.isLeaf()) {
            result.insertToLeaf(pos);
        } else {
            Octant posOctant = result.getOctant(pos);
            OctTree posTree = new OctTree(result, posOctant);
            posTree.point = pos;
            result.setOctTree(posOctant, posTree);
        }
    }

    @Override
    public void delete(BlockPos pos) {
        if (pos == null) return;
        OctTree result = search(pos);

        if (pos.equals(result.point)) {
            result.point = null;
            while (result.parent != null && result.isEmpty()) {
                Octant octant = result.parent.getOctant(pos);
                result.parent.setOctTree(octant, null);
                result = result.parent;
            }
        }
    }

    @Override
    public BlockPos findNearest(final BlockPos pos) {
        if (isEmpty()) return null;
        OctTree closestTree = search(pos);
        if (pos.equals(closestTree.point)) return pos;

        PriorityQueue<BlockPos> closestPQ = new PriorityQueue<>((o1, o2) -> {
            return Float.compare(o1.getManhattanDistance(pos), o2.getManhattanDistance(pos));
        });

        closestTree.buildPQ(closestPQ);
        return closestPQ.poll();
    }

    private void buildPQ(PriorityQueue<BlockPos> pq) {
        for (Octant octant : Octant.values()) {
            OctTree child = getOctTree(octant);
            if (child.isLeaf()) pq.offer(child.point);
            else child.buildPQ(pq);
        }
    }

    @Override
    public boolean isEmpty() {
        return isLeaf() && point == null;
    }

    @Override
    public Iterator<BlockPos> iterator() {
        return new TreeIterator(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OctTree)) return false;
        OctTree tree = (OctTree) obj;
        return boundary.equals(tree.boundary);
    }

    // Smaller helpers

    private void buildMaps() {
        octTrees.put(Octant.FIRST, null);
        octTrees.put(Octant.SECOND, null);
        octTrees.put(Octant.THIRD, null);
        octTrees.put(Octant.FOURTH, null);
        octTrees.put(Octant.FIFTH, null);
        octTrees.put(Octant.SIXTH, null);
        octTrees.put(Octant.SEVENTH, null);
        octTrees.put(Octant.EIGHTH, null);
    }

    private Octant getOctant(final BlockPos query) {
        Triplet<Boolean, Boolean, Boolean> queryValues = new Triplet<>(
                query.getX() < center.getX(),
                query.getY() < center.getY(),
                query.getZ() < center.getZ());
        return octants.get(queryValues);
    }

    private OctTree getOctTree(final Octant query) {
        return octTrees.get(query);
    }

    private OctTree getOctTree(final BlockPos query) {
        return getOctTree(getOctant(query));
    }

    private void setOctTree(Octant key, OctTree value) {
        octTrees.put(key, value);
    }

    private boolean isLeaf() {
        return octTrees.values().stream().allMatch(Objects::isNull);
    }

    // Bigger Helpers

    private OctTree search(final BlockPos pos) {
        if (pos.equals(point)) return this;
        OctTree octTree = getOctTree(pos);
        if (octTree != null) return octTree.search(pos);
        return this;
    }

    private void insertToLeaf(BlockPos pos) {
        OctTree tree = this;

        Octant pointOctant = tree.getOctant(point);
        Octant posOctant = tree.getOctant(pos);

        while (pointOctant == posOctant) {
            tree.setOctTree(pointOctant, new OctTree(tree, pointOctant));
            tree = tree.getOctTree(pointOctant);

            pointOctant = tree.getOctant(point);
            posOctant = tree.getOctant(pos);
        }

        OctTree posTree = new OctTree(tree, posOctant);
        posTree.point = pos;
        tree.setOctTree(posOctant, posTree);

        OctTree pointTree = new OctTree(tree, pointOctant);
        pointTree.point = point;
        tree.setOctTree(pointOctant, pointTree);

        point = null;
    }

    // Iterator class
    private static class TreeIterator implements Iterator<BlockPos> {

        private final Stack<BlockPos> positions = new Stack<>();

        private TreeIterator(OctTree tree) {
            buildStack(tree);
        }

        @Override
        public boolean hasNext() {
            return !positions.isEmpty();
        }

        @Override
        public BlockPos next() {
            if (!hasNext()) throw new NoSuchElementException();
            return positions.pop();
        }

        private void buildStack(OctTree parent) {
            for (OctTree tree : parent.octTrees.values()) {
                if (tree != null) {
                    if (tree.point != null) positions.push(tree.point);
                    else buildStack(tree);
                }
            }
        }

    }

}
