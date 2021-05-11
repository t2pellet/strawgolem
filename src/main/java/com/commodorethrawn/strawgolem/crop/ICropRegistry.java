package com.commodorethrawn.strawgolem.crop;

import net.minecraft.block.Block;
import net.minecraft.state.property.IntProperty;

public interface ICropRegistry {

    ICropRegistry INSTANCE = new CropRegistry();

    void register(Block id, IntProperty ageProperty);

    boolean containsCrop(Block id);

    IntProperty getAgeProperty(Block id);
}
