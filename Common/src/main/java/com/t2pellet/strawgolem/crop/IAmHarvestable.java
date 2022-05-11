package com.t2pellet.strawgolem.crop;

import net.minecraft.world.level.block.state.BlockState;

public interface IAmHarvestable {

    boolean isMature(BlockState state);
}
