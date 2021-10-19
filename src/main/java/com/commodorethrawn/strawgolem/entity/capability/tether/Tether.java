package com.commodorethrawn.strawgolem.entity.capability.tether;

import com.commodorethrawn.strawgolem.entity.capability.Capability;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public interface Tether extends Capability {

    static Tether getInstance() {
        return new TetherImpl();
    }

    void set(World world, BlockPos pos);

    void set(RegistryKey<World> world, BlockPos pos);

    TetherPos get();

    <T extends Entity> double distanceTo(T entity);

    double distanceTo(World world, BlockPos pos);

    interface TetherPos {

        TetherPos ORIGIN = new TetherImpl.TetherPosImpl(World.OVERWORLD, BlockPos.ORIGIN);

        RegistryKey<World> getWorld();

        BlockPos getPos();

    }
}
