package com.commodorethrawn.strawgolem.storage;

import com.commodorethrawn.strawgolem.events.CropGrowthHandler;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class StrawgolemSaveData {

    private static final String GOLEM_DATA_NAME = "strawgolem.dat";
    private static final int TAG_COMPOUND = 10;

    private final File worldDataDir;
    private final MinecraftServer server;

    public StrawgolemSaveData(MinecraftServer server) {
        this.server = server;
        String url = server.getSavePath(WorldSavePath.LEVEL_DAT).toFile().getParentFile().getAbsolutePath();
        worldDataDir = new File(url + "/data");
    }

    private static final String WORLD = "world";
    private static final String POS = "pos";

    public void loadData() throws IOException {
        File saveFile = new File(worldDataDir, GOLEM_DATA_NAME);
        if (saveFile.exists() && saveFile.isFile()) {
            CompoundTag nbt = NbtIo.readCompressed(saveFile);
            ListTag listTag = nbt.getList("listTag", TAG_COMPOUND);
            listTag.forEach(tag -> {
                CompoundTag entryTag = (CompoundTag) tag;
                if (entryTag.get(WORLD) != null) {
                    RegistryKey<World> dim = DimensionType.method_28521(new Dynamic<>(NbtOps.INSTANCE, entryTag.get(WORLD)))
                            .result().orElseThrow(() -> new IllegalArgumentException("Invalid map dimension: " + entryTag.get(WORLD)));
                    if (entryTag.get(POS) != null && dim != null) {
                        BlockPos pos = NbtHelper.toBlockPos((CompoundTag) entryTag.get(POS));
                        World world = server.getWorld(dim);
                        if (world != null) {
                            CropGrowthHandler.scheduleCrop(world, pos, 5);
                        }
                    }
                }
            });
        }
    }

    public void saveData() throws IOException {
        CompoundTag compound = new CompoundTag();
        ListTag listTag = new ListTag();
        CropGrowthHandler.getCrops().forEach(entry -> {
            CompoundTag entryTag = new CompoundTag();
            entryTag.put(POS, NbtHelper.fromBlockPos(entry.getPos()));
            Identifier.CODEC.encodeStart(NbtOps.INSTANCE, entry.getWorld().getRegistryKey().getValue())
                    .result().ifPresent(dim -> entryTag.put(WORLD, dim));
            listTag.add(entryTag);
        });
        compound.put("listTag", listTag);
        File file = new File(worldDataDir, GOLEM_DATA_NAME);
        NbtIo.writeCompressed(compound, file);
    }
}
