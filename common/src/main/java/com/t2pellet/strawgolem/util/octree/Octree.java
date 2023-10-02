package com.t2pellet.strawgolem.util.octree;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

interface IOctree {

    boolean insert(BlockPos pos);
    boolean remove(BlockPos pos);
    BlockPos findNearest(BlockPos query, int range);
    List<BlockPos> search(AABB range);
    List<BlockPos> getAll();
}

public class Octree implements IOctree {

    // Core data
    private static final int NODE_CAPACITY = 8;
    private AABB boundary;
    List<BlockPos> points = new ArrayList<>();

    // Children
    Octree northWestUp;
    Octree northWestDown;
    Octree northEastUp;
    Octree northEastDown;
    Octree southWestUp;
    Octree southWestDown;
    Octree southEastUp;
    Octree southEastDown;

    public Octree(AABB boundary) {
        this.boundary = boundary;
    }

    @Override
    public boolean insert(BlockPos pos) {
        if (!pointInBoundary(pos)) {
            return false;
        }

        if (points.size() < NODE_CAPACITY && northWestUp == null) {
            points.add(pos);
            return true;
        } else if (northWestUp == null) {
            subdivide();
        }

        if (northWestUp.insert(pos)) return true;
        if (northWestDown.insert(pos)) return true;
        if (northEastUp.insert(pos)) return true;
        if (northEastDown.insert(pos)) return true;
        if (southWestUp.insert(pos)) return true;
        if (southWestDown.insert(pos)) return true;
        if (southEastUp.insert(pos)) return true;
        if (southEastDown.insert(pos)) return true;
        return false;
    }

    @Override
    public boolean remove(BlockPos pos) {
        if (pos == null || !pointInBoundary(pos)) {
            return false;
        }

        if (points.contains(pos)) {
            points.remove(pos);
            return true;
        }
        if (northWestUp == null) return false;

        if (northWestUp.remove(pos)) return true;
        if (northWestDown.remove(pos)) return true;
        if (northEastUp.remove(pos)) return true;
        if (northEastDown.remove(pos)) return true;
        if (southWestUp.remove(pos)) return true;
        if (southWestDown.remove(pos)) return true;
        if (southEastUp.remove(pos)) return true;
        if (southEastDown.remove(pos)) return true;
        return false;
    }

    @Override
    public List<BlockPos> search(AABB range) {
        List<BlockPos> pointsInRange = new ArrayList<>();

        if (!boundary.intersects(range)) return pointsInRange;

        for (BlockPos point : points) {
            if (range.contains(Vec3.atCenterOf(point))) {
                pointsInRange.add(point);
            }
        }

        if (northWestUp == null) return pointsInRange;

        pointsInRange.addAll(northWestUp.search(range));
        pointsInRange.addAll(northWestDown.search(range));
        pointsInRange.addAll(northEastUp.search(range));
        pointsInRange.addAll(northEastDown.search(range));
        pointsInRange.addAll(southWestUp.search(range));
        pointsInRange.addAll(southWestDown.search(range));
        pointsInRange.addAll(southEastUp.search(range));
        pointsInRange.addAll(southEastDown.search(range));
        return pointsInRange;
    }

    @Override
    public List<BlockPos> getAll() {
        if (northWestUp == null) return new ArrayList<>(points);

        List<BlockPos> result = northWestUp.getAll();
        result.addAll(northWestDown.getAll());
        result.addAll(northEastUp.getAll());
        result.addAll(northEastDown.getAll());
        result.addAll(southWestUp.getAll());
        result.addAll(southWestDown.getAll());
        result.addAll(southEastUp.getAll());
        result.addAll(southEastDown.getAll());

        return result;
    }

    @Override
    public BlockPos findNearest(BlockPos query, int range) {
        if (!pointInBoundary(query)) return null;

        if (!points.isEmpty() && northWestUp == null) {
            List<BlockPos> pointsInRange = points.stream().filter(pos -> pos.distManhattan(query) <= range).toList();
            return pointsInRange.isEmpty() ? null : findNearestFromList(pointsInRange, query);
        } else if (northWestUp == null) return null;

        BlockPos closest = northWestUp.findNearest(query, range);
        if (closest == null) closest = northWestDown.findNearest(query, range);
        if (closest == null) closest = northEastUp.findNearest(query, range);
        if (closest == null) closest = northEastDown.findNearest(query, range);
        if (closest == null) closest = southWestUp.findNearest(query, range);
        if (closest == null) closest = southWestDown.findNearest(query, range);
        if (closest == null) closest = southEastUp.findNearest(query, range);
        if (closest == null) closest = southEastDown.findNearest(query, range);

        int closestDistance = closest.distManhattan(query);
        int clampedClosestDistance = Math.min(closestDistance, range);
        Vec3 corner1 = Vec3.atCenterOf(closest).add(clampedClosestDistance, clampedClosestDistance, clampedClosestDistance);
        Vec3 corner2 = Vec3.atCenterOf(closest).add(-clampedClosestDistance, -clampedClosestDistance, -clampedClosestDistance);
        AABB rangeBox = new AABB(corner1, corner2);
        List<BlockPos> rangeResults = search(rangeBox);
        return findNearestFromList(rangeResults, query);
    }

    private static BlockPos findNearestFromList(List<BlockPos> points, BlockPos query) {
        BlockPos nearest = points.get(0);
        int nearestDistance = nearest.distManhattan(query);
        for (int i = 1; i < points.size(); ++i) {
            BlockPos point = points.get(i);
            int distance = point.distManhattan(query);
            if (distance < nearestDistance) {
                nearest = point;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    private boolean pointInBoundary(BlockPos pos) {
        return boundary.contains(Vec3.atCenterOf(pos));
    }

    private void subdivide() {
        Vec3 center = boundary.getCenter();
        northWestUp = new Octree(new AABB(center, new Vec3(boundary.minX, boundary.maxY, boundary.minZ)));
        northWestDown = new Octree(new AABB(center, new Vec3(boundary.minX, boundary.minY, boundary.minZ)));
        northEastUp = new Octree(new AABB(center, new Vec3(boundary.maxX, boundary.maxY, boundary.minZ)));
        northEastDown = new Octree(new AABB(center, new Vec3(boundary.maxY, boundary.minY, boundary.minZ)));
        southWestUp = new Octree(new AABB(center, new Vec3(boundary.minX, boundary.maxY, boundary.maxZ)));
        southWestDown = new Octree(new AABB(center, new Vec3(boundary.minX, boundary.minY, boundary.maxZ)));
        southEastUp = new Octree(new AABB(center, new Vec3(boundary.maxX, boundary.maxY, boundary.maxZ)));
        southEastDown = new Octree(new AABB(center, new Vec3(boundary.maxX, boundary.minY, boundary.maxZ)));

        for (BlockPos pos : points) {
            if (northWestUp.pointInBoundary(pos)) {
                northWestUp.points.add(pos);
            } else if (northWestDown.pointInBoundary(pos)) {
                northWestDown.points.add(pos);
            } else if (northEastUp.pointInBoundary(pos)) {
                northEastUp.points.add(pos);
            } else if (northEastDown.pointInBoundary(pos)) {
                northEastDown.points.add(pos);
            } else if (southWestUp.pointInBoundary(pos)) {
                southWestUp.points.add(pos);
            } else if (southWestDown.pointInBoundary(pos)) {
                southWestDown.points.add(pos);
            } else if (southEastUp.pointInBoundary(pos)) {
                southEastUp.points.add(pos);
            } else if (southEastDown.pointInBoundary(pos)) {
                southEastDown.points.add(pos);
            }
        }
    }


}