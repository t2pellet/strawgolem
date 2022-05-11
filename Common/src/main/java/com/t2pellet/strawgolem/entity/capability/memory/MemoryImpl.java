package com.t2pellet.strawgolem.entity.capability.memory;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.HashSet;
import java.util.Set;

class MemoryImpl implements Memory {

    private final Set<Pair<ResourceKey<Level>, BlockPos>> posList;
    // location of preferred chest for delivery
    private BlockPos priority;

    public MemoryImpl() {
        posList = new HashSet<>();
        priority = BlockPos.ZERO;
    }

    @Override
    public Set<Pair<ResourceKey<Level>, BlockPos>> getPositions() {
        return posList;
    }

    @Override
    public BlockPos getDeliveryChest(Level world, BlockPos pos) {
        if (!priority.equals(BlockPos.ZERO)) {
            return priority;
        }
        BlockPos closest = BlockPos.ZERO;
        for (Pair<ResourceKey<Level>, BlockPos> chestPos : posList) {
            if (!chestPos.getFirst().equals(world.dimension())) continue;
            if (pos.distSqr(closest) >= pos.distSqr(chestPos.getSecond())) {
                closest = chestPos.getSecond();
            }
        }
        return closest;
    }

    @Override
    public void addPosition(Level world, BlockPos pos) {
        addPosition(world.dimension(), pos);
    }

    public void addPosition(ResourceKey<Level> world, BlockPos pos) {
        posList.add(Pair.of(world, pos));
    }

    @Override
    public void removePosition(Level world, BlockPos pos) {
        if (priority.equals(pos)) priority = BlockPos.ZERO;
        posList.remove(Pair.of(world.dimension(), pos));
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
        CompoundTag nbt = new CompoundTag();
        Set<Pair<ResourceKey<Level>, BlockPos>> posList = getPositions();
        ListTag tagList = new ListTag();
        for (Pair<ResourceKey<Level>, BlockPos> pos : posList) {
            CompoundTag posNBT = new CompoundTag();
            ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, pos.getFirst().location()).result().ifPresent(dim -> {
                posNBT.put("id", dim);
            });
            posNBT.put("pos", NbtUtils.writeBlockPos(pos.getSecond()));
            tagList.add(posNBT);
        }
        nbt.put("positions", tagList);
        nbt.put("priority", NbtUtils.writeBlockPos(getPriorityChest()));
        return nbt;
    }

    @Override
    public void readTag(Tag nbt) {
        CompoundTag tag = (CompoundTag) nbt;
        ListTag tagList = tag.getList("positions", 10);
        for (Tag inbt : tagList) {
            CompoundTag posNBT = (CompoundTag) inbt;
            ResourceKey<Level> dim = DimensionType.parseLegacy(new Dynamic<>(NbtOps.INSTANCE, posNBT.get("id"))).result().orElseThrow(() -> {
                return new IllegalArgumentException("Invalid map dimension: " + posNBT.get("id"));
            });
            if (dim == null) continue;
            BlockPos pos = NbtUtils.readBlockPos(posNBT.getCompound("pos"));
            addPosition(dim, pos);
        }
        Tag posTag = tag.get("priority");
        if (posTag instanceof CompoundTag) {
            setPriorityChest(NbtUtils.readBlockPos((CompoundTag) posTag));
        }
    }
}
