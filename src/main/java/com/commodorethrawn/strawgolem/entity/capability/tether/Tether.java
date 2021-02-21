package com.commodorethrawn.strawgolem.entity.capability.tether;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public interface Tether {

    public static Tether create() {
        return new TetherImpl();
    }

    public void set(World world, BlockPos pos);

    public void set(RegistryKey<World> world, BlockPos pos);

    public TetherPos get();

    public int distanceTo(World world, BlockPos pos);

    public interface TetherPos {

        public static TetherPos ORIGIN = new TetherImpl.TetherPosImpl(World.OVERWORLD, BlockPos.ORIGIN);

        public RegistryKey<World> getWorld();

        public BlockPos getPos();

    }
}
