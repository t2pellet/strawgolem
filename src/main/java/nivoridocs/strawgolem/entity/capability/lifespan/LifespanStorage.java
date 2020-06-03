package nivoridocs.strawgolem.entity.capability.lifespan;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class LifespanStorage implements IStorage<ILifespan> {

	@Override
	public INBT writeNBT(Capability<ILifespan> capability, ILifespan instance, Direction side) {
		CompoundNBT tag = new CompoundNBT();
		tag.putInt("life", instance.get());
		return tag;
	}

	@Override
	public void readNBT(Capability<ILifespan> capability, ILifespan instance, Direction side, INBT nbt) {
		CompoundNBT tag = (CompoundNBT)nbt;
		instance.set(tag.getInt("life"));
	}

}
