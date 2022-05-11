package com.t2pellet.strawgolem.entity.capability.tether;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

class TetherImpl implements Tether {

    private TetherPos tetherPos;

    public TetherImpl() {
        tetherPos = TetherPos.ORIGIN;
    }

    @Override
    public void set(Level world, BlockPos pos) {
        this.tetherPos = new TetherPosImpl(world, pos);
    }

    @Override
    public void set(ResourceKey<Level> world, BlockPos pos) {
        this.tetherPos = new TetherPosImpl(world, pos);
    }

    @Override
    public TetherPos get() {
        return tetherPos;
    }

    @Override
    public <T extends Entity> double distanceTo(T entity) {
        // If we are in a different dimension, we should reset the tether
        if (entity.level.dimension().equals(tetherPos.getWorld())) {
            return entity.blockPosition().distManhattan(tetherPos.getPos());
        }
        tetherPos = new TetherPosImpl(entity.level, entity.blockPosition());
        return 0;
    }

    @Override
    public double distanceTo(Level world, BlockPos pos) {
        if (world.dimension().equals(tetherPos.getWorld())) {
            return pos.distManhattan(tetherPos.getPos());
        }
        // If we are in a different dimension, we should reset the tether
        tetherPos = new TetherPosImpl(world, pos);
        return 0;
    }

    @Override
    public Tag writeTag() {
        CompoundTag tag = new CompoundTag();
        tag.put("pos", NbtUtils.writeBlockPos(tetherPos.getPos()));
        ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, tetherPos.getWorld().location()).result().ifPresent(dim -> {
            tag.put("world", dim);
        });
        return tag;
    }

    @Override
    public void readTag(Tag nbt) {
        CompoundTag tag = (CompoundTag)  nbt;
        ResourceKey<Level> dim = DimensionType.parseLegacy(new Dynamic<>(NbtOps.INSTANCE, tag.get("world"))).result().orElseThrow(() -> {
            return new IllegalArgumentException("Invalid map dimension: " + tag.get("world"));
        });
        if (dim == null) return;
        BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("pos"));
        set(dim, pos);
    }

    static class TetherPosImpl implements TetherPos {

        private ResourceKey<Level> world;
        private BlockPos pos;

        public TetherPosImpl(Level world, BlockPos pos) {
           this(world.dimension(), pos);
        }

        public TetherPosImpl(ResourceKey<Level> world, BlockPos pos) {
            this.world = world;
            this.pos = pos;
        }

        @Override
        public ResourceKey<Level> getWorld() {
            return world;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }
    }
}
