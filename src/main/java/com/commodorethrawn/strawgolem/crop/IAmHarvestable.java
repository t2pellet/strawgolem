package com.commodorethrawn.strawgolem.crop;

import net.minecraft.block.BlockState;

public interface IAmHarvestable {

    boolean isMature(BlockState state);
}
