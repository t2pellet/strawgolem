package com.commodorethrawn.strawgolem.util.struct;

import net.minecraft.util.math.BlockPos;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

class PosTreeImpl implements PosTree {

    private static class PosNode {

        private BlockPos pos;
        private PosNode left;
        private PosNode right;

        public PosNode(BlockPos pos, final PosNode left, final PosNode right) {
            this.pos = pos;
            this.left = left;
            this.right = right;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }

        public boolean lessThan(final BlockPos blockPos, int dim) {
            switch (dim) {
                case 0:
                    return pos.getX() < blockPos.getX();
                case 1:
                    return pos.getY() < blockPos.getY();
                default:
                    return pos.getZ() < blockPos.getZ();
            }
        }

        public boolean greaterThan(final BlockPos blockPos, int dim) {
            switch (dim) {
                case 0:
                    return pos.getX() > blockPos.getX();
                case 1:
                    return pos.getY() > blockPos.getY();
                default:
                    return pos.getZ() > blockPos.getZ();
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PosNode)) return false;
                PosNode node = (PosNode) obj;
                return pos.equals(node.pos);
        }
    }

    private class TreeIterator implements Iterator<BlockPos> {

        private final Stack<PosNode> stack;

        private TreeIterator() {
            PosNode node = root;
            stack = new Stack<>();
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public BlockPos next() {
            if (!hasNext()) throw new NoSuchElementException();
            PosNode curr = stack.pop();
            BlockPos result = curr.pos;
            if (curr.right != null) {
                curr = curr.right;
                while (curr != null) {
                    stack.push(curr);
                    curr = curr.left;
                }
            }
            return result;
        }
    }

    PosNode root = null;

    public boolean isEmpty() {
        return root == null;
    }

    public void insert(final BlockPos pos) {
        root = insert(pos, root, 0);
    }

    public BlockPos findNearest(final BlockPos pos) {
        if (root == null) return null;
        return findNearest(pos, root, root.pos, root.pos.getSquaredDistance(pos), 0);
    }

    public void delete(final BlockPos pos) {
        root = deletePos(pos, root, 0);
    }

    public Iterator<BlockPos> iterator() {
        return new TreeIterator();
    }

    private PosNode insert(final BlockPos pos, PosNode node, int dim) {
        if (node == null) {
            node = new PosNode(pos, null, null);
        } else if (node.greaterThan(pos, dim)) {
            node.left = insert(pos, node.left, (dim + 1) % 3);
        } else {
            node.right = insert(pos, node.right, (dim + 1) % 3);
        }
        return node;
    }

    private BlockPos findNearest(final BlockPos pos, PosNode node, BlockPos closest, double closestDistance, int dim) {
        if (pos.equals(node.pos)) return pos;
        double distance = pos.getSquaredDistance(node.pos);
        if (distance < closestDistance) {
            closest = node.pos;
            closestDistance = distance;
        }
        if (node.isLeaf()) return closest;
        if (node.greaterThan(pos, dim) && node.left != null) {
            closest = findNearest(pos, node.left, closest, closestDistance, (dim + 1) % 3);
        } else if (node.right != null) {
            closest = findNearest(pos, node.right, closest, closestDistance, (dim + 1) % 3);
        }
        return closest;
    }

    private PosNode deletePos(final BlockPos pos, PosNode node, int dim) {
        if (node == null) return null;
        if (node.pos.equals(pos)) {
            if (node.right != null) {
                PosNode min = findMin(node.right, dim, dim);
                node.pos = min.pos;
                node.right = deletePos(min.pos, node.right, (dim + 1) % 3);
            } else if (node.left != null) {
                PosNode min = findMin(node.left, dim, dim);
                node.pos = min.pos;
                node.right = deletePos(min.pos, node.left, (dim + 1) % 3);
                node.left = null;
            } else {
                return null;
            }
        } else {
            if (node.greaterThan(pos, dim)) {
                node.left = deletePos(pos, node.left, (dim + 1) % 3);
            } else {
                node.right = deletePos(pos, node.right, (dim + 1) % 3);
            }
        }
        return node;
    }

    private PosNode findMin(PosNode node, int desiredDim, int dim) {
        if (node == null) return null;
        if (desiredDim == dim) {
            if (node.left == null) return node;
            return findMin(node.left, desiredDim, (dim + 1) % 3);
        }
        PosNode min = node;
        PosNode left = findMin(node.left, desiredDim, (dim + 1) % 3);
        PosNode right = findMin(node.right, desiredDim, (dim + 1) % 3);
        if (left != null && left.lessThan(min.pos, desiredDim)) min = left;
        if (right != null && right.lessThan(min.pos, desiredDim)) min = right;
        return min;
    }


}
