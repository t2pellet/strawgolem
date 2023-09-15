package com.t2pellet.strawgolem.entity.capabilities.tether;

import com.t2pellet.tlib.common.entity.capability.Capability;
import com.t2pellet.tlib.common.entity.capability.ICapabilityHaver;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;

public interface Tether extends Capability {

    static <E extends LivingEntity & ICapabilityHaver> Tether getInstance(E entity) {
        return new TetherImpl<>(entity);
    }

    BlockPos get();
    void update();
    void update(BlockPos pos);
    boolean isTooFar();
    boolean exists();

}
