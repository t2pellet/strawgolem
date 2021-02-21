package com.commodorethrawn.strawgolem.entity.capability;

import com.commodorethrawn.strawgolem.Strawgolem;
import net.minecraft.nbt.Tag;

import java.util.Optional;

public interface Capability {

    /**
     * Creates instance of the desired capability
     * @param clazz the capability class
     * @param <T> the capability instance
     * @return the capability instance
     */
    public static <T extends Capability> Optional<T> create(Class<T> clazz) {
        T instance;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Strawgolem.logger.debug("Failed to instantiate class: " + clazz);
            instance = null;
        }
        return Optional.ofNullable(instance);
    }

    /**
     * Writes the capability data to a tag
     * @return the data tag
     */
    public Tag writeTag();

    /**
     * Reads the capability data from a tag
     * @param tag the data tag
     */
    public void readTag(Tag tag);

}
