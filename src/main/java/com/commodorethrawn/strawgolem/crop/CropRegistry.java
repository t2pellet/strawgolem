package com.commodorethrawn.strawgolem.crop;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.IntProperty;

public interface CropRegistry {

    CropRegistry INSTANCE = new CropRegistryImpl();

    /**
     * Register a Block as a valid crop
     * @param id the block to register
     * @param ageProperty the age property for that crop
     */
    void register(Block id, IntProperty ageProperty);

    /**
     * Register a BlockEntity as a valid crop
     * @param id the block to register
     * @param ageProperty the age property for that crop
     */
    void register(BlockEntity id, IntProperty ageProperty);

    /**
     * Determine if a block is a registered crop
     * @param id the block to check
     * @return true if the block is in the registry, false otherwise
     */
    boolean containsCrop(Block id);

    /**
     * Determine if a BlockEntity is a registered crop
     * @param id the block to check
     * @return true if the block is in the registry, false otherwise
     */
    boolean containsCrop(BlockEntity id);

    /**
     * Gets the associated age property with the given block
     * @param id the block to check for
     * @return the associated age property
     */
    IntProperty getAgeProperty(Block id);

    /**
     * Gets the associated age property with the given BlockEntity
     * @param id the BlockEntity to check for
     * @return the associated age property
     */
    IntProperty getAgeProperty(BlockEntity id);

}
