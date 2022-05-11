package com.t2pellet.strawgolem.crop;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public interface CropRegistry {

    CropRegistry INSTANCE = new CropRegistryImpl();

    /**
     * Register a Block as a valid crop
     * @param id the block to register
     * @param ageProperty the age property for that crop
     */
    void register(Block id, IntegerProperty ageProperty);

    /**
     * Register a BlockEntity as a valid crop
     * @param id the block to register
     * @param ageProperty the age property for that crop
     */
    void register(BlockEntity id, IntegerProperty ageProperty);

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
    IntegerProperty getAgeProperty(Block id);

    /**
     * Gets the associated age property with the given BlockEntity
     * @param id the BlockEntity to check for
     * @return the associated age property
     */
    IntegerProperty getAgeProperty(BlockEntity id);

}
