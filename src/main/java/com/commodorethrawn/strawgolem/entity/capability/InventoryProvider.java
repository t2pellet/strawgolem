package com.commodorethrawn.strawgolem.entity.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InventoryProvider implements ICapabilitySerializable<INBT> {

    @CapabilityInject(IItemHandler.class)
    public static final Capability<IItemHandler> CROP_SLOT = null;

    private final LazyOptional<IItemHandler> inventoryInstance = LazyOptional.of(() -> new ItemStackHandler(1));

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CROP_SLOT) {
            return inventoryInstance.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return CROP_SLOT.getStorage().writeNBT(CROP_SLOT, inventoryInstance.orElseThrow(() -> new IllegalArgumentException("cant be empty")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        CROP_SLOT.getStorage().readNBT(CROP_SLOT, inventoryInstance.orElseThrow(() -> new IllegalArgumentException("cant be empty")), null, nbt);
    }
}
