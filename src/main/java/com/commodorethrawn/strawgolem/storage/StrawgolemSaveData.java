package com.commodorethrawn.strawgolem.storage;

import com.commodorethrawn.strawgolem.crop.CropHandler;
import net.minecraft.nbt.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class StrawgolemSaveData {

    private static final int TAG_COMPOUND = 10;

    private final File worldDataDir;

    public StrawgolemSaveData(MinecraftServer server) {
        worldDataDir = new File(server.getSavePath(WorldSavePath.ROOT) + "/strawgolem");
        if (!worldDataDir.exists()) worldDataDir.mkdirs();
    }

    private static final String POS = "pos";

    public void loadData(MinecraftServer server) throws IOException {
        for (ServerWorld world : server.getWorlds()) {
            File saveFile = new File(worldDataDir, getFileName(world));
            if (saveFile.exists() && saveFile.isFile()) {
                CompoundTag worldTag = NbtIo.readCompressed(saveFile);
                ListTag positionsTag = worldTag.getList(POS, TAG_COMPOUND);
                positionsTag.forEach(tag -> {
                    BlockPos pos = NbtHelper.toBlockPos((CompoundTag) tag);
                    CropHandler.INSTANCE.addCrop(world, pos);
                });
            }
        }
    }

    public void saveData(MinecraftServer server) throws IOException {
        CompoundTag worldTag = new CompoundTag();
        ListTag positionsTag = new ListTag();
        for (ServerWorld world : server.getWorlds()) {
            Iterator<BlockPos> cropIterator = CropHandler.INSTANCE.getCrops(world);
            while (cropIterator.hasNext()) {
                BlockPos pos = cropIterator.next();
                positionsTag.add(NbtHelper.fromBlockPos(pos));
            }
            worldTag.put(POS, positionsTag);
            File file = new File(worldDataDir, getFileName(world));
            NbtIo.writeCompressed(worldTag, file);
        }
    }

    private String getFileName(World world) {
        Identifier id = world.getRegistryKey().getValue();
        return id.getNamespace() + "-" + id.getPath() + ".dat";
    }
}
