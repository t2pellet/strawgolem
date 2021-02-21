package com.commodorethrawn.strawgolem.entity.capability.tether;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

class TetherImpl implements Tether {

    private TetherPos tetherPos;

    public TetherImpl() {
        tetherPos = TetherPos.ORIGIN;
    }

    @Override
    public void set(World world, BlockPos pos) {
        this.tetherPos = new TetherPosImpl(world, pos);
    }

    @Override
    public void set(RegistryKey<World> world, BlockPos pos) {
        this.tetherPos = new TetherPosImpl(world, pos);
    }

    @Override
    public TetherPos get() {
        return tetherPos;
    }

    @Override
    public int distanceTo(World world, BlockPos pos) {
        // If we are in a different dimension, we should reset the tether
        if (!world.getRegistryKey().equals(tetherPos.getWorld())) {
           return pos.getManhattanDistance(tetherPos.getPos());
        }
        tetherPos = new TetherPosImpl(world, pos);
        return 0;
    }

    static class TetherPosImpl implements TetherPos {

        private RegistryKey<World> world;
        private BlockPos pos;

        public TetherPosImpl(World world, BlockPos pos) {
           this(world.getRegistryKey(), pos);
        }

        public TetherPosImpl(RegistryKey<World> world, BlockPos pos) {
            this.world = world;
            this.pos = pos;
        }

        @Override
        public RegistryKey<World> getWorld() {
            return world;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }
    }
}
