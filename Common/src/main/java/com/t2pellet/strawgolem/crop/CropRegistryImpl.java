package com.t2pellet.strawgolem.crop;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.HashMap;
import java.util.Map;

class CropRegistryImpl implements CropRegistry {

    private final Map<CropRegistryEntry<?>, IntegerProperty> ids = new HashMap<>();

    @Override
    public void register(Block id, IntegerProperty ageProperty) {
        if (id == null) return;
        ids.put(new CropRegistryEntry<>(id), ageProperty);
    }

    @Override
    public void register(BlockEntity id, IntegerProperty ageProperty) {
        if (id == null) return;
        ids.put(new CropRegistryEntry<>(id), ageProperty);
    }

    @Override
    public boolean containsCrop(Block id) {
        return id != null && contains(new CropRegistryEntry<>(id));
    }

    @Override
    public boolean containsCrop(BlockEntity id) {
        return id != null && contains(new CropRegistryEntry<>(id));
    }

    private boolean contains(CropRegistryEntry<?> id) {
        return id != null && ids.containsKey(id);
    }

    @Override
    public IntegerProperty getAgeProperty(Block id) {
        return id == null ? null : getAgeProp(id);
    }

    @Override
    public IntegerProperty getAgeProperty(BlockEntity id) {
        return id == null ? null : getAgeProp(id);
    }

    private <T> IntegerProperty getAgeProp(T id) {
        return ids.get(new CropRegistryEntry<>(id));
    }

    private static class CropRegistryEntry<T> {

        private final T obj;

        private CropRegistryEntry(T obj) {
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
            if (obj instanceof CropRegistryEntry<?> entry) {
                return get().equals(entry.get());
            }
            else return get().equals(obj);
        }
    }
}
