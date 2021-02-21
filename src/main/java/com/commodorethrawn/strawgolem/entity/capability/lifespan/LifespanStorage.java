package com.commodorethrawn.strawgolem.entity.capability.lifespan;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;

public class LifespanStorage {

    public static Tag writeNBT(Lifespan instance) {
        return IntTag.of(instance.get());
    }

    public static void readNBT(Lifespan instance, Tag tag) {
        IntTag intTag = (IntTag) tag;
        instance.set(intTag.getInt());
    }

}
