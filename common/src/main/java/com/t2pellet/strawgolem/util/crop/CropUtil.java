package com.t2pellet.strawgolem.util.crop;

import com.t2pellet.strawgolem.compat.HarvestableBlock;
import com.t2pellet.strawgolem.compat.HarvestableState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CropUtil {

    private CropUtil() {}

    public static boolean isCrop(LevelAccessor level, BlockPos pos) {
        return pos != null && isCrop(level.getBlockState(pos));
    }

    public static boolean isGrownCrop(LevelAccessor level, BlockPos pos) {
        return pos != null && isGrownCrop(level.getBlockState(pos));
    }

    public static boolean isGrownCrop(BlockState state) {
        if (state.getBlock() instanceof CropBlock cropBlock) {
            return cropBlock.isMaxAge(state);
        } else if (state.getBlock() instanceof HarvestableBlock cropBlock) {
            return cropBlock.isMaxAge(state);
        } else if (state instanceof HarvestableState cropBlock) {
            return cropBlock.isMaxAge();
        }
        return false;
    }

    public static boolean isCrop(BlockState state) {
        return state.getBlock() instanceof CropBlock | state.getBlock() instanceof HarvestableBlock || state instanceof HarvestableState;
    }

}
