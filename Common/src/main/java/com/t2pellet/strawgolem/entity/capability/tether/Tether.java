package com.t2pellet.strawgolem.entity.capability.tether;

import com.t2pellet.strawgolem.entity.capability.Capability;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public interface Tether extends Capability {

    static Tether getInstance() {
        return new TetherImpl();
    }

    void set(Level world, BlockPos pos);

    void set(ResourceKey<Level> world, BlockPos pos);

    TetherPos get();

    <T extends Entity> double distanceTo(T entity);

    double distanceTo(Level world, BlockPos pos);

    interface TetherPos {

        TetherPos ORIGIN = new TetherImpl.TetherPosImpl(Level.OVERWORLD, BlockPos.ZERO);

        ResourceKey<Level> getWorld();

        BlockPos getPos();

    }
}
