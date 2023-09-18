package com.t2pellet.strawgolem.compat.api;

import net.minecraft.world.level.block.state.BlockState;

public interface HarvestableBlock {

    boolean isMaxAge(BlockState state);

}
