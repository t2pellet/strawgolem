package com.commodorethrawn.strawgolem.entity.capability.memory;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.Set;

public class MemoryStorage {

    private static final int TAG_COMPOUND = 10;

    public static Tag writeNBT(Memory instance) {
        CompoundTag tag = new CompoundTag();
        Set<Pair<RegistryKey<World>, BlockPos>> posList = instance.getPositions();
        ListTag tagList = new ListTag();
        for (Pair<RegistryKey<World>, BlockPos> pos : posList) {
            CompoundTag posNBT = new CompoundTag();
            Identifier.CODEC.encodeStart(NbtOps.INSTANCE, pos.getFirst().getValue()).result().ifPresent(dim -> {
                posNBT.put("id", dim);
            });
            posNBT.put("pos", NbtHelper.fromBlockPos(pos.getSecond()));
            tagList.add(posNBT);
        }
        tag.put("positions", tagList);
        tag.put("priority", NbtHelper.fromBlockPos(instance.getPriorityChest()));
        return tag;
    }

    public static void readNBT(MinecraftServer server, Memory instance, Tag nbt) {
        CompoundTag tag = (CompoundTag) nbt;
        ListTag tagList = tag.getList("positions", TAG_COMPOUND);
        for (Tag inbt : tagList) {
            CompoundTag posNBT = (CompoundTag) inbt;
            RegistryKey<World> dim = DimensionType.method_28521(new Dynamic<>(NbtOps.INSTANCE, posNBT.get("id"))).result().orElseThrow(() -> {
                return new IllegalArgumentException("Invalid map dimension: " + posNBT.get("id"));
            });
            if (dim == null) continue;
            BlockPos pos = NbtHelper.toBlockPos(posNBT.getCompound("pos"));
            instance.addPosition(server.getWorld(dim), pos);
        }
        Tag posTag = tag.get("priority");
        if (posTag instanceof CompoundTag) {
            instance.setPriorityChest(NbtHelper.toBlockPos((CompoundTag) posTag));
        }
    }
}
