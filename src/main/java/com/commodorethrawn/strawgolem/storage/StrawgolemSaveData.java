package com.commodorethrawn.strawgolem.storage;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.events.CropGrowthHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class StrawgolemSaveData extends WorldSavedData {

    public StrawgolemSaveData() {
        super(Strawgolem.MODID);
    }

    public StrawgolemSaveData(String name) {
        super(name);
        markDirty();
    }

    public static StrawgolemSaveData get(final ServerWorld world) {
        DimensionSavedDataManager storage = world.getSavedData();
        return storage.getOrCreate(StrawgolemSaveData::new, Strawgolem.MODID);
    }

    @Override
    public void read(CompoundNBT nbt) {
        ListNBT listTag = nbt.getList("listTag", Constants.NBT.TAG_COMPOUND);
        for (INBT tag : listTag) {
            CompoundNBT entryTag = (CompoundNBT) tag;
            BlockPos pos = null;
            IWorld world = null;
            if (entryTag.get("pos") != null) {
                pos = NBTUtil.readBlockPos(entryTag.getCompound("pos"));
            }
            if (entryTag.get("world") != null) {
                DimensionType dimension = DimensionType.getById(entryTag.getInt("world"));
                if (dimension != null) world = ServerLifecycleHooks.getCurrentServer().getWorld(dimension);
            }
            if (world != null && pos != null) {
                CropGrowthHandler.cropQueue.add(new CropGrowthHandler.CropQueueEntry(pos, world));
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        Iterator<CropGrowthHandler.CropQueueEntry> cropIterator = CropGrowthHandler.cropQueue.iterator();
        ListNBT listTag = new ListNBT();
        while (cropIterator.hasNext()) {
            CompoundNBT entryTag = new CompoundNBT();
            CropGrowthHandler.CropQueueEntry entry = cropIterator.next();
            entryTag.put("pos", NBTUtil.writeBlockPos(entry.getPos()));
            entryTag.putInt("world", entry.getWorld().getDimension().getType().getId());
            listTag.add(entryTag);
        }
        compound.put("listTag", listTag);
        return compound;
    }
}
