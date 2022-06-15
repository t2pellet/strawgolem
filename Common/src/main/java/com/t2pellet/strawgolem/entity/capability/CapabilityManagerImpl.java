package com.t2pellet.strawgolem.entity.capability;

import com.t2pellet.strawgolem.StrawgolemCommon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.Map;

public class CapabilityManagerImpl implements CapabilityManager {

    private final Map<Class<? extends Capability>, Capability> map = new HashMap<>();

    CapabilityManagerImpl() {
    }

    @Override
    public <T extends Capability> void addCapability(Class<T> capabilityClass) {
        T capability = CapabilityHandler.INSTANCE.get(capabilityClass)
                .orElseThrow(() -> new InstantiationError("Failed to instantiate capability for class: " + capabilityClass.getSimpleName()));
        map.put(capabilityClass, capability);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Capability> T getCapability(Class<T> capabilityClass) {
        return (T) map.get(capabilityClass);
    }

    @Override
    public <T extends Capability> void setCapability(Class<T> capabilityClass, T capability) {
        map.put(capabilityClass, capability);
    }

    @Override
    public Tag writeTag() {
        ListTag tag = new ListTag();
        map.forEach((aClass, capability) -> {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("className", aClass.getName());
            compoundTag.put("capability", capability.writeTag());
            tag.add(compoundTag);
        });
        return tag;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readTag(Tag tag) {
        ListTag listTag = (ListTag) tag;
        listTag.forEach(tagInList -> {
            CompoundTag compoundTag = (CompoundTag) tagInList;
            try {
                Class<? extends Capability> aClass = (Class<? extends Capability>) Class.forName(compoundTag.getString("className"));
                Capability capability = fromTag(aClass, compoundTag.get("capability"));
                map.put(aClass, capability);
            } catch (ClassNotFoundException e) {
                StrawgolemCommon.LOG.error("Failed to instantiate capability from NBT", e);
            }
        });
    }

    private <T extends Capability> T fromTag(Class<T> aClass, Tag tag) {
        T capability = CapabilityHandler.INSTANCE.get(aClass)
                .orElseThrow(() -> new InstantiationError("Failed to instantiate capability for class: " + aClass.getSimpleName()));
        capability.readTag(tag);
        return capability;
    }
}
