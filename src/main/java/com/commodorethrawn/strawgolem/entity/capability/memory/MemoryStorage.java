package com.commodorethrawn.strawgolem.entity.capability.memory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Set;

public class MemoryStorage implements Capability.IStorage<IMemory> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<IMemory> capability, IMemory instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        Set<Pair<IWorld, BlockPos>> posList = instance.getPositions();
        ListNBT tagList = new ListNBT();
        for (Pair<IWorld, BlockPos> pos : posList) {
            CompoundNBT posNBT = new CompoundNBT();
            posNBT.putInt("id", pos.getFirst().getDimension().getType().getId());
            posNBT.put("pos", NBTUtil.writeBlockPos(pos.getSecond()));
            tagList.add(posNBT);
        }
        tag.put("positions", tagList);
        tag.put("priority", NBTUtil.writeBlockPos(instance.getPriorityChest()));
        return tag;
    }

    @Override
    public void readNBT(Capability<IMemory> capability, IMemory instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        ListNBT tagList = tag.getList("positions", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); ++i) {
            CompoundNBT posNBT = (CompoundNBT) tagList.get(i);
            DimensionType dimType = DimensionType.getById(posNBT.getInt("id"));
            if (dimType == null) continue;
            BlockPos pos = NBTUtil.readBlockPos(posNBT.getCompound("pos"));
            instance.addPosition(ServerLifecycleHooks.getCurrentServer().getWorld(dimType), pos);
        }
        INBT posTag = tag.get("priority");
        if (posTag instanceof CompoundNBT) {
            instance.setPriorityChest(NBTUtil.readBlockPos((CompoundNBT) posTag));
        }
    }
}
