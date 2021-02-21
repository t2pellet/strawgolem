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

import java.util.HashSet;
import java.util.Set;

class MemoryImpl implements Memory {

    private final Set<Pair<RegistryKey<World>, BlockPos>> posList;
    // location of preferred chest for delivery
    private BlockPos priority;

    public MemoryImpl() {
        posList = new HashSet<>();
        priority = BlockPos.ORIGIN;
    }

    @Override
    public Set<Pair<RegistryKey<World>, BlockPos>> getPositions() {
        return posList;
    }

    @Override
    public BlockPos getDeliveryChest(World world, BlockPos pos) {
        if (!priority.equals(BlockPos.ORIGIN)) {
            return priority;
        }
        BlockPos closest = BlockPos.ORIGIN;
        for (Pair<RegistryKey<World>, BlockPos> chestPos : posList) {
            if (!chestPos.getFirst().equals(world.getRegistryKey())) continue;
            if (pos.getSquaredDistance(closest) >= pos.getSquaredDistance(chestPos.getSecond())) {
                closest = chestPos.getSecond();
            }
        }
        return closest;
    }

    @Override
    public void addPosition(World world, BlockPos pos) {
        addPosition(world.getRegistryKey(), pos);
    }

    public void addPosition(RegistryKey<World> world, BlockPos pos) {
        posList.add(Pair.of(world, pos));
    }

    @Override
    public void removePosition(World world, BlockPos pos) {
        if (priority.equals(pos)) priority = BlockPos.ORIGIN;
        posList.remove(Pair.of(world.getRegistryKey(), pos));
    }

    @Override
    public BlockPos getPriorityChest() {
        return priority;
    }

    @Override
    public void setPriorityChest(BlockPos pos) {
        priority = pos;
    }

    @Override
    public Tag writeTag() {
        CompoundTag tag = new CompoundTag();
        Set<Pair<RegistryKey<World>, BlockPos>> posList = getPositions();
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
        tag.put("priority", NbtHelper.fromBlockPos(getPriorityChest()));
        return tag;
    }

    @Override
    public void readTag(Tag nbt) {
        CompoundTag tag = (CompoundTag) nbt;
        ListTag tagList = tag.getList("positions", 10);
        for (Tag inbt : tagList) {
            CompoundTag posNBT = (CompoundTag) inbt;
            RegistryKey<World> dim = DimensionType.method_28521(new Dynamic<>(NbtOps.INSTANCE, posNBT.get("id"))).result().orElseThrow(() -> {
                return new IllegalArgumentException("Invalid map dimension: " + posNBT.get("id"));
            });
            if (dim == null) continue;
            BlockPos pos = NbtHelper.toBlockPos(posNBT.getCompound("pos"));
            addPosition(dim, pos);
        }
        Tag posTag = tag.get("priority");
        if (posTag instanceof CompoundTag) {
            setPriorityChest(NbtHelper.toBlockPos((CompoundTag) posTag));
        }
    }
}
