package com.commodorethrawn.strawgolem.entity.capability.lifespan;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LifespanProvider implements ICapabilitySerializable<INBT> {
	
	@CapabilityInject(ILifespan.class)
	public static final Capability<ILifespan> LIFESPAN_CAP = null;

	private LazyOptional<ILifespan> instance = LazyOptional.of(LIFESPAN_CAP::getDefaultInstance);

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return cap == LIFESPAN_CAP ? instance.cast() : LazyOptional.empty();
	}

	@Override
	public INBT serializeNBT() {
		return LIFESPAN_CAP.getStorage().writeNBT(LIFESPAN_CAP, instance.orElseThrow(() -> new IllegalArgumentException("cant be empty")), null);
	}

	@Override
	public void deserializeNBT(INBT nbt) {
        LIFESPAN_CAP.getStorage().readNBT(LIFESPAN_CAP, instance.orElseThrow(() -> new IllegalArgumentException("cant be empty")), null, nbt);
	}
}
