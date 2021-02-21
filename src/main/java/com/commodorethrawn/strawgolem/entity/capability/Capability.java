package com.commodorethrawn.strawgolem.entity.capability;

import com.commodorethrawn.strawgolem.Strawgolem;
import net.minecraft.nbt.Tag;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public interface Capability {

    /**
     * Creates instance of the desired capability
     * @param clazz the capability class
     * @param <T> the capability instance
     * @return the capability instance
     */
    static <T extends Capability> Optional<T> create(Class<T> clazz) {
        T instance;
        try {
            instance = (T) clazz.getMethod("getInstance").invoke(null);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException ex) {
            Strawgolem.logger.debug("Failed to instantiate class: " + clazz + ". Does the interface have a getInstance() method?");
            instance = null;
        }
        return Optional.ofNullable(instance);
    }

    /**
     * Writes the capability data to a tag
     * @return the data tag
     */
    Tag writeTag();

    /**
     * Reads the capability data from a tag
     * @param tag the data tag
     */
    void readTag(Tag tag);

}
