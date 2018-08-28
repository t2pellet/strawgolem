package nivoridocs.strawgolem.entity.capability.lifespan;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class LifespanStorage implements IStorage<ILifespan> {

	@Override
	public NBTBase writeNBT(Capability<ILifespan> capability, ILifespan instance, EnumFacing side) {
		return new NBTTagInt(instance.get());
	}

	@Override
	public void readNBT(Capability<ILifespan> capability, ILifespan instance, EnumFacing side, NBTBase nbt) {
		instance.set(((NBTPrimitive) nbt).getInt());
	}

}
