package com.commodorethrawn.strawgolem.events;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.commodorethrawn.strawgolem.entity.strawgolem.EntityStrawGolem;
import com.commodorethrawn.strawgolem.storage.StrawgolemSaveData;
import net.minecraft.block.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Handles how golems tend to harvest crops
 * <p>
 * Every time a crop grows, it checks for a golem nearby to harvest. If none is found it is added to a queue
 * Every 100 ticks, it goes through the queue checking for golems to harvest, skipping through already harvested blocks
 * and breaking when it cannot find a golem to harvest
 */
@Mod.EventBusSubscriber(modid = Strawgolem.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CropGrowthHandler {

    public static Queue<CropQueueEntry> cropQueue = new LinkedList<>();
    public static StrawgolemSaveData data;
    private static int tick = 0;

    @SubscribeEvent
    public static void serverStart(FMLServerStartingEvent event) {
        Strawgolem.logger.info("Strawgolem: Server Starting");
        data = StrawgolemSaveData.get(event.getServer().getWorld(DimensionType.OVERWORLD));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (tick % 100 == 0) {
                while (!cropQueue.isEmpty()) {
                    if (!isFullyGrown(cropQueue.peek())) {
                        cropQueue.remove();
                        data.markDirty();
                        continue;
                    }
                    EntityStrawGolem golem = getCropGolem(cropQueue.peek());
                    if (golem == null) break;
                    golem.setHarvesting(cropQueue.remove().getPos());
                    data.markDirty();
                }
            }
            ++tick;
        }
    }

    @SubscribeEvent
    public static void onCropGrowth(BlockEvent.CropGrowEvent event) {
        CropQueueEntry entry = new CropQueueEntry(event.getPos(), event.getWorld());
        if (!event.getWorld().isRemote() && isFullyGrown(entry)) {
            EntityStrawGolem golem = getCropGolem(entry);
            if (golem != null) {
                golem.setHarvesting(entry.getPos());
            } else {
                cropQueue.add(new CropQueueEntry(event.getPos(), event.getWorld()));
                data.markDirty();
            }
        }
    }

    /**
     * Returns the first golem nearby that has line of sight to the crop, or null if there are none
     *
     * @param crop
     * @return applicable golem or null if none apply
     */
    private static EntityStrawGolem getCropGolem(CropQueueEntry crop) {
        AxisAlignedBB golemAABB = new AxisAlignedBB(crop.pos).grow(StrawgolemConfig.getSearchRangeHorizontal(),
                StrawgolemConfig.getSearchRangeVertical(),
                StrawgolemConfig.getSearchRangeHorizontal());
        List<EntityStrawGolem> golemList = crop.world.getEntitiesWithinAABB(EntityStrawGolem.class, golemAABB);
        for (EntityStrawGolem golem : golemList) {
            if (golem.shouldHarvestBlock(crop.world, crop.pos) && golem.isHandEmpty()) {
                return golem;
            }
        }
        return null;
    }

    /**
     * Returns true if the crop is applicable and fully grown, false otherwise
     *
     * @param entry
     * @return whether the block is fully grown
     */
    private static boolean isFullyGrown(CropQueueEntry entry) {
        BlockState state = entry.getWorld().getBlockState(entry.getPos());
        if (state.getBlock() instanceof CropsBlock) {
            CropsBlock crop = (CropsBlock) state.getBlock();
            return crop.isMaxAge(state);
        } else if (state.getBlock() instanceof StemGrownBlock) {
            return true;
        } else if (state.getBlock() instanceof NetherWartBlock) {
            return state.get(NetherWartBlock.AGE) == 3;
        } else if (state.getBlock() instanceof BushBlock && state.has(BlockStateProperties.AGE_0_3)) {
            return state.get(BlockStateProperties.AGE_0_3) == 3;
        }
        return false;
    }

    public static class CropQueueEntry {
        private BlockPos pos;
        private IWorld world;

        public CropQueueEntry(BlockPos pos, IWorld world) {
            this.pos = pos;
            this.world = world;
        }

        public BlockPos getPos() {
            return pos;
        }

        public IWorld getWorld() {
            return world;
        }
    }
}
