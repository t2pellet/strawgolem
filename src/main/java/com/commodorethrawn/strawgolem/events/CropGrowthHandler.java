package com.commodorethrawn.strawgolem.events;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.commodorethrawn.strawgolem.storage.StrawgolemSaveData;
import net.minecraft.block.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Handles how golems tend to harvest crops
 * Every time a crop grows, it checks for a golem nearby to harvest. If none is found it is added to a queue
 * Every 100 ticks, it goes through the queue checking for golems to harvest, skipping through already harvested blocks
 * and breaking when it cannot find a golem to harvest
 */
@Mod.EventBusSubscriber(modid = Strawgolem.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CropGrowthHandler {

    protected static final PriorityQueue<CropQueueEntry> queue = new PriorityQueue<>();
    private static final int HARVEST_DELAY = 100;
    static StrawgolemSaveData data;

    private CropGrowthHandler() {}

    @SubscribeEvent
    public static void serverStart(FMLServerStartingEvent event) {
        Strawgolem.logger.info("Strawgolem: Server Starting");
        data = StrawgolemSaveData.get(event.getServer().getWorld(DimensionType.OVERWORLD));
    }

    private static int ticks = 0;
    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && EffectiveSide.get().isServer()) {
            while (!queue.isEmpty() && ticks == queue.peek().tick) {
                queue.remove().execute();
                data.markDirty();
            }
            ++ticks;
        }
    }

    @SubscribeEvent
    public static void onCropGrowth(BlockEvent.CropGrowEvent.Post event) {
        if (!event.getWorld().isRemote() && isFullyGrown(event.getWorld(), event.getPos())) {
            EntityStrawGolem golem = getCropGolem(event.getWorld(), event.getPos());
            if (golem != null) {
                golem.setHarvesting(event.getPos());
            } else {
                scheduleCrop(event.getWorld(), event.getPos(), 12);
            }
        }
    }

    @SubscribeEvent
    public static void onCropGrowth(BonemealEvent event) {
        if (!event.getWorld().isRemote() && isNearlyGrown(event.getWorld(), event.getPos())) {
            EntityStrawGolem golem = getCropGolem(event.getWorld(), event.getPos());
            if (golem != null) {
                golem.setHarvesting(event.getPos());
            } else {
                scheduleCrop(event.getWorld(), event.getPos(), 12);
            }
        }
    }

    /**
     * Returns the first golem nearby that has path to the crop, or null if there are none
     * @param world the world
     * @param pos the position
     * @return applicable golem or null if none apply
     */
    private static EntityStrawGolem getCropGolem(IWorld world, BlockPos pos) {
        AxisAlignedBB golemAABB = new AxisAlignedBB(pos).grow(
                ConfigHelper.getSearchRangeHorizontal(),
                ConfigHelper.getSearchRangeVertical(),
                ConfigHelper.getSearchRangeHorizontal());
        List<EntityStrawGolem> golemList = world.getEntitiesWithinAABB(EntityStrawGolem.class, golemAABB);
        for (EntityStrawGolem golem : golemList) {
            if (golem.getHarvestPos().equals(BlockPos.ZERO)
                    && golem.canSeeBlock(world, pos)
                    && golem.isHandEmpty()) {
                return golem;
            }
        }
        return null;
    }

    /**
     * Returns true if the crop is applicable and fully grown, false otherwise
     *
     * @param world the world
     * @param pos the position
     * @return whether the block is fully grown
     */
    private static boolean isFullyGrown(IWorld world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (ConfigHelper.blockHarvestAllowed(state.getBlock())) {
            if (state.getBlock() instanceof CropsBlock) {
                CropsBlock crop = (CropsBlock) state.getBlock();
                return crop.isMaxAge(state);
            } else if (state.getBlock() instanceof StemGrownBlock) {
                return true;
            } else if (state.getBlock() instanceof NetherWartBlock) {
                return state.get(NetherWartBlock.AGE) == 3;
            } else if (state.getBlock() instanceof BushBlock
                    && state.getBlock() instanceof IGrowable
                    && state.has(BlockStateProperties.AGE_0_3)) {
                return state.get(BlockStateProperties.AGE_0_3) == 3;
            }
        }
        return false;
    }

    /**
     * Returns true if the crop is applicable and almost fully grown, false otherwise
     * Used for bone meal event
     *
     * @param world the world
     * @param pos the position
     * @return whether the crop is nearly fully grown
     */
    private static boolean isNearlyGrown(IWorld world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (ConfigHelper.blockHarvestAllowed(state.getBlock())) {
            if (state.getBlock() instanceof CropsBlock) {
                CropsBlock crop = (CropsBlock) state.getBlock();
                return state.get(crop.getAgeProperty()) >= crop.getMaxAge() - 2;
            } else if (state.getBlock() instanceof StemGrownBlock) {
                return true;
            } else if (state.getBlock() instanceof NetherWartBlock) {
                return state.get(NetherWartBlock.AGE) == 2;
            } else if (state.getBlock() instanceof BushBlock
                    && state.getBlock() instanceof IGrowable
                    && state.has(BlockStateProperties.AGE_0_3)) {
                return state.get(BlockStateProperties.AGE_0_3) == 2;
            }
        }
        return false;
    }

    /**
     * Schedules a crop to be harvested
     *
     * @param world    the world the crop is in
     * @param pos      the position of the crop
     * @param runsLeft the amount of times left to reschedule this crop to be harvested
     */
    public static void scheduleCrop(IWorld world, BlockPos pos, int runsLeft) {
        if (runsLeft <= 0) return;
        int executeTick = ticks + HARVEST_DELAY;
        queue.add(new CropQueueEntry(pos, world, executeTick, runsLeft));
        if (data != null) data.markDirty();
    }

    public static Iterator<CropQueueEntry> getCrops() {
        return queue.iterator();
    }

    public static class CropQueueEntry implements Comparable<CropQueueEntry> {
        private final BlockPos pos;
        private final IWorld world;
        private final int tick;
        private int count;

        public BlockPos getPos() {
            return pos;
        }

        public IWorld getWorld() {
            return world;
        }

        public CropQueueEntry(BlockPos pos, IWorld world, int tick, int count) {
            this.pos = pos;
            this.world = world;
            this.tick = tick;
            this.count = count;
        }

        public void execute() {
            if (world.isBlockLoaded(pos)) {
                if (isFullyGrown(world, pos)) {
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
