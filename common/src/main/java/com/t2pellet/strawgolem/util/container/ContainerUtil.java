package com.t2pellet.strawgolem.util.container;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.LevelAccessor;

public class ContainerUtil {

    private ContainerUtil() {}

    public static boolean isContainer(LevelAccessor level, BlockPos pos) {
        return pos != null && level.getBlockEntity(pos) instanceof Container;
    }

}
