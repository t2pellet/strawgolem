package com.t2pellet.strawgolem.entity.capability.accessory;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;

public class AccessoryImpl implements Accessory {

    private boolean hasHat;

    @Override
    public boolean hasHat() {
        return hasHat;
    }

    @Override
    public void setHasHat(boolean hasHat) {
        this.hasHat = hasHat;
    }

    @Override
    public Tag writeTag() {
        return IntTag.valueOf(hasHat ? 1 : 0);
    }

    @Override
    public void readTag(Tag tag) {
        IntTag intTag = (IntTag) tag;
        hasHat = intTag.getAsInt() == 1;
    }
}
