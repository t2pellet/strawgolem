package com.t2pellet.strawgolem.storage;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.crop.WorldCrops;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static net.minecraft.nbt.Tag.TAG_COMPOUND;

/**
 * Kept for backwards compatibility
 */
@Deprecated
public class StrawgolemSaveData {

    private static final String POS = "pos";
    private static final String VERSION_KEY = "version";
    private static final int VERSION = 1;

    private final File worldDataDir;

    public StrawgolemSaveData(MinecraftServer server) {
        worldDataDir = new File(server.getWorldPath(LevelResource.ROOT) + "strawgolem");
    }

    public void loadData(MinecraftServer server) throws IOException {
        if (worldDataDir.exists()) {
            StrawgolemCommon.LOG.info("Loading strawgolem legacy save data");
            for (ResourceKey<Level> world : server.levelKeys()) {
                File saveFile = new File(worldDataDir, getFileName(world));
                if (saveFile.exists() && saveFile.isFile()) {
                    CompoundTag worldTag = NbtIo.readCompressed(saveFile);
                    if (!worldTag.contains(VERSION_KEY) || worldTag.getInt(VERSION_KEY) != VERSION) continue;
                    ListTag positionsTag = worldTag.getList(POS, TAG_COMPOUND);
                    positionsTag.forEach(tag -> {
                        BlockPos pos = NbtUtils.readBlockPos((CompoundTag) tag);
                        WorldCrops.of(server.getLevel(world)).addCrop(pos);
                    });
                    FileUtils.deleteQuietly(saveFile);
                }
            }
            FileUtils.deleteDirectory(worldDataDir);
        }
    }

    private String getFileName(ResourceKey<Level> key) {
        ResourceLocation id = key.location();
        return id.getNamespace() + "-" + id.getPath() + ".dat";
    }
}
