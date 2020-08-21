package com.commodorethrawn.strawgolem.storage;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.events.CropGrowthHandler;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.*;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
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

    private static final String WORLD = "world";
    private static final String POS = "pos";

    @Override
    public void read(CompoundNBT nbt) {
        ListNBT listTag = nbt.getList("listTag", Constants.NBT.TAG_COMPOUND);
        for (INBT tag : listTag) {
            CompoundNBT entryTag = (CompoundNBT) tag;
            BlockPos pos = null;
            World world = null;
            if (entryTag.get(POS) != null) {
                pos = NBTUtil.readBlockPos(entryTag.getCompound(POS));
            }
            if (entryTag.get(WORLD) != null) {
                RegistryKey<World> dim = DimensionType.func_236025_a_(new Dynamic<>(NBTDynamicOps.INSTANCE, entryTag.get(WORLD)))
                        .result().orElseThrow(() -> new IllegalArgumentException("Invalid map dimension: " + entryTag.get(WORLD)));
                if (dim != null) world = ServerLifecycleHooks.getCurrentServer().getWorld(dim);
            }
            if (world != null && pos != null) {
                CropGrowthHandler.scheduleCrop(world, pos, 3);
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        Iterator<CropGrowthHandler.CropQueueEntry> cropIterator = CropGrowthHandler.getCrops();
        ListNBT listTag = new ListNBT();
        while (cropIterator.hasNext()) {
            CompoundNBT entryTag = new CompoundNBT();
            CropGrowthHandler.CropQueueEntry entry = cropIterator.next();
            entryTag.put(POS, NBTUtil.writeBlockPos(entry.getPos()));
            ResourceLocation.field_240908_a_.encodeStart(NBTDynamicOps.INSTANCE, entry.getWorld().func_234923_W_().func_240901_a_())
                    .result().ifPresent(dim -> entryTag.put(WORLD, dim));
            listTag.add(entryTag);
        }
        compound.put("listTag", listTag);
        return compound;
    }
}
