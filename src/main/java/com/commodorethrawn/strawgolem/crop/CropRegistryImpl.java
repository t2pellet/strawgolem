package com.commodorethrawn.strawgolem.crop;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.IntProperty;

import java.util.HashMap;
import java.util.Map;

class CropRegistryImpl implements CropRegistry {

    private final Map<CropRegistryEntry<?>, IntProperty> ids = new HashMap<>();

    @Override
    public void register(Block id, IntProperty ageProperty) {
        ids.put(new CropRegistryEntry<>(id), ageProperty);
    }

    @Override
    public void register(BlockEntity id, IntProperty ageProperty) {
        ids.put(new CropRegistryEntry<>(id), ageProperty);
    }

    @Override
    public boolean containsCrop(Block id) {
        return contains(new CropRegistryEntry<>(id));
    }

    @Override
    public boolean containsCrop(BlockEntity id) {
        return id != null && contains(new CropRegistryEntry<>(id));
    }

    private boolean contains(CropRegistryEntry<?> id) {
        return id != null && ids.containsKey(id);
    }

    @Override
    public IntProperty getAgeProperty(Block id) {
        return getAgeProp(id);
    }

    @Override
    public IntProperty getAgeProperty(BlockEntity id) {
        return getAgeProp(id);
    }

    private <T> IntProperty getAgeProp(T id) {
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
            if (obj instanceof CropRegistryEntry<?>) {
                CropRegistryEntry<?> entry = (CropRegistryEntry<?>) obj;
                return get().equals(entry.get());
            }
            else return get().equals(obj);
        }
    }
}
