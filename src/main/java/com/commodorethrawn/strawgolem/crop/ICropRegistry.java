package com.commodorethrawn.strawgolem.crop;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.IntProperty;

public interface ICropRegistry {

    ICropRegistry INSTANCE = new CropRegistry();

    void register(Block id, IntProperty ageProperty);

    void register(BlockEntity id, IntProperty ageProperty);

    boolean containsCrop(Block id);

    boolean containsCrop(BlockEntity id);

    IntProperty getAgeProperty(Block id);

    IntProperty getAgeProperty(BlockEntity id);

}
