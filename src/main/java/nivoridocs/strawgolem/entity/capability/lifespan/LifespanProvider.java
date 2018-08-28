package nivoridocs.strawgolem.entity.capability.lifespan;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class LifespanProvider implements ICapabilitySerializable<NBTBase> {
	
	@CapabilityInject(ILifespan.class)
	public static final Capability<ILifespan> LIFESPAN_CAP = null;
	
	private ILifespan instance = LIFESPAN_CAP.getDefaultInstance();

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == LIFESPAN_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return hasCapability(capability, facing) ? LIFESPAN_CAP.<T>cast(instance) : null;
	}

	@Override
	public NBTBase serializeNBT() {
		return LIFESPAN_CAP.getStorage().writeNBT(LIFESPAN_CAP, instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		LIFESPAN_CAP.getStorage().readNBT(LIFESPAN_CAP, instance, null, nbt);
	}

}
