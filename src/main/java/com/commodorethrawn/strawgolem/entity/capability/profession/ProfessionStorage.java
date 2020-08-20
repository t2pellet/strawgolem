package com.commodorethrawn.strawgolem.entity.capability.profession;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class ProfessionStorage implements Capability.IStorage<IProfession> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<IProfession> capability, IProfession instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("profession", instance.get().value);
        return tag;
    }

    @Override
    public void readNBT(Capability<IProfession> capability, IProfession instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        instance.set(IProfession.GolemProfession.valueOf(tag.getInt("profession")));
    }
}
