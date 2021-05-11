package com.commodorethrawn.strawgolem.events;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.crop.CropValidator;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.commodorethrawn.strawgolem.entity.ai.GolemHarvestGoal;
import com.commodorethrawn.strawgolem.mixin.GoalSelectorAccessor;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.io.IOException;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Handles how golems tend to harvest crops
 * Every time a crop grows, it checks for a golem nearby to harvest. If none is found it is added to a queue
 * Every 100 ticks, it goes through the queue checking for golems to harvest, skipping through already harvested blocks
 * and breaking when it cannot find a golem to harvest
 */
public class CropGrowthHandler {

    protected static final PriorityQueue<CropQueueEntry> queue = new PriorityQueue<>();
    private static final int HARVEST_DELAY = 100;

    private CropGrowthHandler() {}

    private static int ticks = 0;

    public static void tick(ServerWorld world) {
        if (!world.isClient && Strawgolem.getSaveData() != null) {
            while (!queue.isEmpty() && ticks == queue.peek().tick) {
                queue.remove().execute();
                try {
                    Strawgolem.getSaveData().saveData();
                } catch (IOException ex) { ex.printStackTrace(); }
            }
            ++ticks;
        }
    }

    public static void onCropGrowth(WorldAccess world, BlockPos cropPos) {
        if (!world.isClient()) {
            if (CropValidator.isGrownCrop(world.getBlockState(cropPos))) {
                EntityStrawGolem golem = getCropGolem(world, cropPos);
                if (golem != null) {
                    golem.setHarvesting(cropPos);
                } else {
                    scheduleCrop(world, cropPos, 12);
                }
            }
        }
    }

    /**
     * Returns the first golem nearby that has path to the crop, or null if there are none
     *
     * @param world the world
     * @param pos   the position
     * @return applicable golem or null if none apply
     */
    private static EntityStrawGolem getCropGolem(WorldAccess world, BlockPos pos) {
        Box golemAABB = new Box(pos).expand(
                ConfigHelper.getSearchRangeHorizontal(),
                ConfigHelper.getSearchRangeVertical(),
                ConfigHelper.getSearchRangeHorizontal());
        return world.getEntitiesIncludingUngeneratedChunks(EntityStrawGolem.class, golemAABB).stream()
                .filter(golem -> {
                    GoalSelector goalSelector = ((GoalSelectorAccessor) golem).goalSelector();
                    boolean notHarvesting = goalSelector.getRunningGoals().noneMatch(goal -> goal.getGoal() instanceof GolemHarvestGoal);
                    return notHarvesting && golem.canSeeBlock(world, pos) && golem.isHandEmpty();
                })
                .findFirst().orElse(null);
    }

    /**
     * Schedules a crop to be harvested
     * @param world    the world the crop is in
     * @param pos      the position of the crop
     * @param runsLeft the amount of times left to reschedule this crop to be harvested
     */
    public static void scheduleCrop(WorldAccess world, BlockPos pos, int runsLeft) {
        if (runsLeft <= 0) return;
        int executeTick = ticks + HARVEST_DELAY;
        queue.add(new CropQueueEntry(pos, (World) world, executeTick, runsLeft));
        if (Strawgolem.getSaveData() != null) {
            try {
                Strawgolem.getSaveData().saveData();
            } catch (IOException ex) { ex.printStackTrace(); }
        }
    }

    public static Iterator<CropQueueEntry> getCrops() {
        return queue.iterator();
    }

    public static class CropQueueEntry implements Comparable<CropQueueEntry> {
        private final BlockPos pos;
        private final World world;
        private final int tick;
        private int count;

        public BlockPos getPos() {
            return pos;
        }

        public World getWorld() {
            return world;
        }

        public CropQueueEntry(BlockPos pos, World world, int tick, int count) {
            this.pos = pos;
            this.world = world;
            this.tick = tick;
            this.count = count;
        }

        public void execute() {
            if (world.isChunkLoaded(pos)) {
                if (CropValidator.isGrownCrop(world.getBlockState(pos))) {
                    EntityStrawGolem golem = getCropGolem(world, pos);
                    if (golem != null) {
                        golem.setHarvesting(pos);
                        return;
                    }
                    scheduleCrop(world, pos, --count);
                }
            } else scheduleCrop(world, pos, count);
        }

        @Override
        public int compareTo(CropQueueEntry entry) {
            if (entry.tick == tick) {
                if (entry.count == count) return 0;
                return entry.count > count ? 1 : -1;
            }
            return entry.tick > tick ? -1 : 1;
        }
    }
}
