package com.t2pellet.strawgolem.entity.capabilities.harvester;

import com.t2pellet.tlib.common.entity.capability.Capability;
import com.t2pellet.tlib.common.entity.capability.ICapabilityHaver;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;

public interface Harvester extends Capability {

    static <E extends LivingEntity & ICapabilityHaver> Harvester getInstance(E entity) {
        return new HarvesterImpl<>(entity);
    }

    void harvest(BlockPos pos);
    BlockPos getHarvesting();
    boolean isHarvesting();

}
