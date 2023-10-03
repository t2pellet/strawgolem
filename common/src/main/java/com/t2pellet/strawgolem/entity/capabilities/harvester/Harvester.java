package com.t2pellet.strawgolem.entity.capabilities.harvester;

import com.t2pellet.tlib.entity.capability.api.Capability;
import com.t2pellet.tlib.entity.capability.api.ICapabilityHaver;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public interface Harvester extends Capability {

    static <E extends Entity & ICapabilityHaver> Harvester getInstance(E entity) {
        return new HarvesterImpl<>(entity);
    }

    void harvest(BlockPos pos);
    void clear();
    // Client
    boolean isHarvesting();
    boolean isHarvestingBlock();
    void completeHarvest();

}
