package com.commodorethrawn.strawgolem.crop;

import net.minecraft.block.Block;
import net.minecraft.state.property.IntProperty;

import java.util.HashMap;
import java.util.Map;

class CropRegistry implements ICropRegistry {

    private final Map<Block, IntProperty> ids = new HashMap<>();

    @Override
    public void register(Block id, IntProperty ageProperty) {
        ids.put(id, ageProperty);
    }

    @Override
    public boolean containsCrop(Block id) {
        return ids.containsKey(id);
    }

    @Override
    public IntProperty getAgeProperty(Block id) {
        return ids.get(id);
    }
}
