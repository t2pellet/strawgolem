package com.commodorethrawn.strawgolem.entity.capability.memory;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

public class MemoryStorage implements Capability.IStorage<IMemory> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<IMemory> capability, IMemory instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        List<BlockPos> posList = instance.getPositionList();
        ListNBT tagList = new ListNBT();
        for (BlockPos pos : posList) {
            tagList.add(NBTUtil.writeBlockPos(pos));
        }
        tag.put("positions", tagList);
        return tag;
    }

    @Override
    public void readNBT(Capability<IMemory> capability, IMemory instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        ListNBT tagList = tag.getList("positions", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); ++i) {
            instance.addPosition(NBTUtil.readBlockPos((CompoundNBT) tagList.get(i)));
        }
    }
}
