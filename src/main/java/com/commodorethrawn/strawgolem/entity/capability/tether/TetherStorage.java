package com.commodorethrawn.strawgolem.entity.capability.tether;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class TetherStorage {

    public static Tag writeNBT(Tether instance) {
        Tether.TetherPos pos = instance.get();
        CompoundTag tag = new CompoundTag();
        tag.put("pos", NbtHelper.fromBlockPos(pos.getPos()));
        Identifier.CODEC.encodeStart(NbtOps.INSTANCE, pos.getWorld().getValue()).result().ifPresent(dim -> {
            tag.put("world", dim);
        });
        return tag;
    }

    public static void readNBT(MinecraftServer server, Tether instance, Tag nbt) {
        CompoundTag tag = (CompoundTag)  nbt;
        RegistryKey<World> dim = DimensionType.method_28521(new Dynamic<>(NbtOps.INSTANCE, tag.get("world"))).result().orElseThrow(() -> {
            return new IllegalArgumentException("Invalid map dimension: " + tag.get("world"));
        });
        if (dim == null) return;
        BlockPos pos = NbtHelper.toBlockPos(tag.getCompound("pos"));
        instance.set(dim, pos);
    }
}
