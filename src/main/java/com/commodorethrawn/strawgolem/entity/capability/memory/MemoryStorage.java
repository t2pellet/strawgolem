package com.commodorethrawn.strawgolem.entity.capability.memory;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.*;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.World;
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
        Set<Pair<RegistryKey<World>, BlockPos>> posList = instance.getPositions();
        ListNBT tagList = new ListNBT();
        for (Pair<RegistryKey<World>, BlockPos> pos : posList) {
            CompoundNBT posNBT = new CompoundNBT();
            ResourceLocation.field_240908_a_.encodeStart(NBTDynamicOps.INSTANCE, pos.getFirst().func_240901_a_()).result().ifPresent(dim -> {
                posNBT.put("id", dim);
            });
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
        for (INBT inbt : tagList) {
            CompoundNBT posNBT = (CompoundNBT) inbt;
            RegistryKey<World> dim = DimensionType.func_236025_a_(new Dynamic<>(NBTDynamicOps.INSTANCE, posNBT.get("id"))).result().orElseThrow(() -> {
                return new IllegalArgumentException("Invalid map dimension: " + posNBT.get("id"));
            });
            if (dim == null) continue;
            BlockPos pos = NBTUtil.readBlockPos(posNBT.getCompound("pos"));
            instance.addPosition(ServerLifecycleHooks.getCurrentServer().getWorld(dim), pos);
        }
        INBT posTag = tag.get("priority");
        if (posTag instanceof CompoundNBT) {
            instance.setPriorityChest(NBTUtil.readBlockPos((CompoundNBT) posTag));
        }
    }
}
