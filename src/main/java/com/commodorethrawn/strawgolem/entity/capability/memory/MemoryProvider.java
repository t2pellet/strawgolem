package com.commodorethrawn.strawgolem.entity.capability.memory;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MemoryProvider implements ICapabilitySerializable<INBT> {

    @CapabilityInject(IMemory.class)
    public static final Capability<IMemory> MEMORY_CAP = null;

    private final LazyOptional<IMemory> memoryInstance = LazyOptional.of(() -> MEMORY_CAP.getDefaultInstance());

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == MEMORY_CAP) {
            return memoryInstance.cast();
        } else {
            return LazyOptional.empty();
        }
    }

    @Override
    public INBT serializeNBT() {
        return MEMORY_CAP.getStorage().writeNBT(MEMORY_CAP, memoryInstance.orElseThrow(() -> new IllegalArgumentException("cant be empty")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        MEMORY_CAP.getStorage().readNBT(MEMORY_CAP, memoryInstance.orElseThrow(() -> new IllegalArgumentException("cant be empty")), null, nbt);
    }
}
