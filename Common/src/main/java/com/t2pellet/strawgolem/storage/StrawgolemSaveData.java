package com.t2pellet.strawgolem.storage;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.crop.CropHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class StrawgolemSaveData {

    private static final int VERSION = 1;

    private static final int TAG_COMPOUND = 10;

    private final File worldDataDir;

    public StrawgolemSaveData(MinecraftServer server) {
        worldDataDir = new File(server.getWorldPath(LevelResource.ROOT) + "strawgolem");
        if (!worldDataDir.exists()) worldDataDir.mkdirs();
    }

    private static final String POS = "pos";
    private static final String VERSION_KEY = "version";

    public void loadData(MinecraftServer server) throws IOException {
        StrawgolemCommon.LOG.info("Loading strawgolem save data");
        for (ServerLevel world : server.getAllLevels()) {
            File saveFile = new File(worldDataDir, getFileName(world));
            if (saveFile.exists() && saveFile.isFile()) {
                CompoundTag worldTag = NbtIo.readCompressed(saveFile);
                if (!worldTag.contains(VERSION_KEY) || worldTag.getInt(VERSION_KEY) != VERSION) continue;
                ListTag positionsTag = worldTag.getList(POS, TAG_COMPOUND);
                positionsTag.forEach(tag -> {
                    BlockPos pos = NbtUtils.readBlockPos((CompoundTag) tag);
                    CropHandler.INSTANCE.addCrop(world, pos);
                });
            }
        }
    }

    public void saveData(MinecraftServer server) throws IOException {
        StrawgolemCommon.LOG.info("Saving strawgolem save data");
        CompoundTag worldTag = new CompoundTag();
        ListTag positionsTag = new ListTag();
        for (ServerLevel world : server.getAllLevels()) {
            Iterator<BlockPos> cropIterator = CropHandler.INSTANCE.getCrops(world);
            while (cropIterator.hasNext()) {
                BlockPos pos = cropIterator.next();
                positionsTag.add(NbtUtils.writeBlockPos(pos));
            }
            worldTag.put(POS, positionsTag);
            worldTag.putInt(VERSION_KEY, VERSION);
            File file = new File(worldDataDir, getFileName(world));
            NbtIo.writeCompressed(worldTag, file);
        }
    }

    private String getFileName(Level world) {
        ResourceLocation id = world.dimension().location();
        return id.getNamespace() + "-" + id.getPath() + ".dat";
    }
}
