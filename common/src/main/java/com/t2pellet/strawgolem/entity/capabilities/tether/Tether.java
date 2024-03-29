package com.t2pellet.strawgolem.entity.capabilities.tether;

import com.t2pellet.tlib.entity.capability.api.Capability;
import com.t2pellet.tlib.entity.capability.api.ICapabilityHaver;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public interface Tether extends Capability {

    static <E extends Entity & ICapabilityHaver> Tether getInstance(E entity) {
        return new TetherImpl<>(entity);
    }

    BlockPos get();
    void update();
    void update(BlockPos pos);
    boolean isTooFar();
    boolean exists();

}
