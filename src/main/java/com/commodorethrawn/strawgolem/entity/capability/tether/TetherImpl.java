package com.commodorethrawn.strawgolem.entity.capability.tether;

import com.mojang.serialization.Dynamic;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

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
    public <T extends Entity> double distanceTo(T entity) {
        // If we are in a different dimension, we should reset the tether
        if (entity.world.getRegistryKey().equals(tetherPos.getWorld())) {
            return entity.getBlockPos().getManhattanDistance(tetherPos.getPos());
        }
        tetherPos = new TetherPosImpl(entity.world, entity.getBlockPos());
        return 0;
    }

    @Override
    public double distanceTo(World world, BlockPos pos) {
        if (world.getRegistryKey().equals(tetherPos.getWorld())) {
            return pos.getManhattanDistance(tetherPos.getPos());
        }
        // If we are in a different dimension, we should reset the tether
        tetherPos = new TetherPosImpl(world, pos);
        return 0;
    }

    @Override
    public Tag writeTag() {
        CompoundTag tag = new CompoundTag();
        tag.put("pos", NbtHelper.fromBlockPos(tetherPos.getPos()));
        Identifier.CODEC.encodeStart(NbtOps.INSTANCE, tetherPos.getWorld().getValue()).result().ifPresent(dim -> {
            tag.put("world", dim);
        });
        return tag;
    }

    @Override
    public void readTag(Tag nbt) {
        CompoundTag tag = (CompoundTag)  nbt;
        RegistryKey<World> dim = DimensionType.method_28521(new Dynamic<>(NbtOps.INSTANCE, tag.get("world"))).result().orElseThrow(() -> {
            return new IllegalArgumentException("Invalid map dimension: " + tag.get("world"));
        });
        if (dim == null) return;
        BlockPos pos = NbtHelper.toBlockPos(tag.getCompound("pos"));
        set(dim, pos);
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
