package com.commodorethrawn.strawgolem.entity.capability.profession;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ProfessionProvider implements ICapabilitySerializable<INBT> {

    @CapabilityInject(IProfession.class)
    public static final Capability<IProfession> PROFESSION_CAP = null;

    private final LazyOptional<IProfession> professionInstance = LazyOptional.of(PROFESSION_CAP::getDefaultInstance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == PROFESSION_CAP) {
            return professionInstance.cast();
        } else {
            return LazyOptional.empty();
        }
    }

    @Override
    public INBT serializeNBT() {
        return PROFESSION_CAP.getStorage().writeNBT(PROFESSION_CAP, professionInstance.orElseThrow(() -> new IllegalArgumentException("cant be empty")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        PROFESSION_CAP.getStorage().readNBT(PROFESSION_CAP, professionInstance.orElseThrow(() -> new IllegalArgumentException("cant be empty")), null, nbt);
    }
}
