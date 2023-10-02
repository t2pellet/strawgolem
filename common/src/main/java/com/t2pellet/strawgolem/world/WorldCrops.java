package com.t2pellet.strawgolem.world;

import com.t2pellet.strawgolem.Constants;
import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.util.crop.CropUtil;
import com.t2pellet.strawgolem.util.octree.Octree;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.AABB;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface WorldCrops {
    String DATA_KEY = "WorldCrops";

    static WorldCrops of(ServerLevel level) {
        DimensionDataStorage dataStorage = level.getDataStorage();
        return dataStorage.computeIfAbsent(tag -> WorldCropsImpl.load(level, tag), () -> new WorldCropsImpl(level), DATA_KEY);
    }

    void add(BlockPos pos);
    void remove(BlockPos pos);
    BlockPos findNearest(BlockPos pos);
    void lock(BlockPos pos);
    void unlock(BlockPos pos);
}

class WorldCropsImpl extends SavedData implements WorldCrops {

    private static final String TAG_VERSION = "version";
    private static final String TAG_POS = "pos";
    private static final int VERSION = 1;

    private final ServerLevel level;
    private final Octree tree = new Octree(new AABB(Integer.MIN_VALUE + 1, Integer.MIN_VALUE + 1, Integer.MIN_VALUE + 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
    private final Set<BlockPos> inProgressHarvestSet = new HashSet<>();

    WorldCropsImpl(ServerLevel level) {
        this.level = level;
    }

    static WorldCropsImpl load(ServerLevel level, CompoundTag tag) {
        Constants.LOG.info("Loading strawgolem save data");
        System.out.println("tag: " + tag.toString());

        WorldCropsImpl crops = new WorldCropsImpl(level);
        if (!tag.contains(TAG_VERSION) || tag.getInt(TAG_VERSION) != VERSION) return crops;

        ListTag positions = tag.getList(TAG_POS, Tag.TAG_COMPOUND);
        for (Tag position : positions) {
            BlockPos pos = NbtUtils.readBlockPos((CompoundTag) position);
            crops.add(pos);
        }
        return crops;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag positionsTag = new ListTag();
        List<BlockPos> crops = tree.getAll();
        for (BlockPos pos : crops) {
            if (CropUtil.isGrownCrop(level, pos)) {
                positionsTag.add(NbtUtils.writeBlockPos(pos));
            }
        }
        for (BlockPos pos : inProgressHarvestSet) {
            if (CropUtil.isGrownCrop(level, pos)) {
                positionsTag.add(NbtUtils.writeBlockPos(pos));
            }
        }
        tag.put(TAG_POS, positionsTag);
        tag.putInt(TAG_VERSION, VERSION);

        System.out.println("saving: " + tag);

        return tag;
    }

    @Override
    public void add(BlockPos pos) {
        if (CropUtil.isGrownCrop(level, pos)) {
            tree.insert(pos);
            setDirty();
        }
    }

    @Override
    public void remove(BlockPos pos) {
        if (!inProgressHarvestSet.remove(pos)) {
            tree.remove(pos);
        }
        setDirty();
    }

    @Override
    public BlockPos findNearest(BlockPos pos) {
        return tree.findNearest(pos, StrawgolemConfig.Harvesting.harvestRange.get());
    }

    @Override
    public void lock(BlockPos pos) {
        if (tree.remove(pos) && CropUtil.isGrownCrop(level, pos)) {
            inProgressHarvestSet.add(pos);
        }
    }

    @Override
    public void unlock(BlockPos pos) {
        if (inProgressHarvestSet.remove(pos) && CropUtil.isGrownCrop(level, pos)) {
            tree.insert(pos);
        }
    }
}
