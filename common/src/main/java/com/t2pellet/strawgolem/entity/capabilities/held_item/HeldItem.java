package com.t2pellet.strawgolem.entity.capabilities.held_item;

import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.tlib.common.entity.capability.Capability;
import com.t2pellet.tlib.common.entity.capability.ICapabilityHaver;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface HeldItem extends Capability {

    static <E extends LivingEntity & ICapabilityHaver> HeldItem getInstance(E entity) {
        return new HeldItemImpl<>(entity);
    }

    void set (ItemStack stack);
    boolean has();

    ItemStack get();

    ItemStack remove();
}
