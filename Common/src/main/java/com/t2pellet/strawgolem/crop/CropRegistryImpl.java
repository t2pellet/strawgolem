package com.t2pellet.strawgolem.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

class CropRegistryImpl implements CropRegistry {

    private final Map<CropKey<?>, Object> entries = new HashMap<>();

    @Override
    public <T extends BlockState> void register(Block id, IHarvestData<T> harvestData) {
        entries.put(new CropKey<>(id), harvestData);
    }

    @Override
    public <T extends BlockEntity> void register(T id, IHarvestData<T> harvestData) {
        entries.put(new CropKey<>(id), harvestData);
    }

    @Override
    public <T extends BlockState> boolean isGrownCrop(T state) {
        return isGrownCrop(new CropKey<>(state.getBlock()), state);
    }

    @Override
    public <T extends BlockEntity> boolean isGrownCrop(T block) {
        return isGrownCrop(new CropKey<>(block), block);
    }

    public <T extends BlockState> void handleReplant(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        BlockEntity entity = level.getBlockEntity(pos);
        if (contains(new CropKey<>(state.getBlock()))) {
            handleReplant(new CropKey<>(state.getBlock()), level, pos, state);
        } else if (contains(new CropKey<>(entity))) {
            handleReplant(new CropKey<>(entity), level, pos, entity);
        }
    }

    private <T> void handleReplant(CropKey<?> key, Level level, BlockPos pos, T val) {
        if (contains(key)) {
            IHarvestData<T> data = (IHarvestData<T>) entries.get(key);
            data.doReplant(level, pos, val);
        }
    }

    private <T> boolean isGrownCrop(CropKey<?> key, T val) {
        if (contains(key)) {
            IHarvestData<T> data = (IHarvestData<T>) entries.get(key);
            return data.isMature(val);
        }
        return false;
    }

    private boolean contains(CropKey<?> id) {
        return id != null && id.get() != null && entries.containsKey(id);
    }

    private static class CropKey<T> {

        private final T obj;

        private CropKey(T obj) {
            this.obj = obj;
        }

        T get() {
            return obj;
        }

        @Override
        public int hashCode() {
            return get().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (obj instanceof CropKey<?> entry) {
                return get().equals(entry.get());
            }
            else return get().equals(obj);
        }
    }
}
