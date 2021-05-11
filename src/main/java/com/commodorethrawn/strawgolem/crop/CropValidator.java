package com.commodorethrawn.strawgolem.crop;

import com.commodorethrawn.strawgolem.config.ConfigHelper;
import net.minecraft.block.AttachedStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GourdBlock;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

import java.util.Arrays;
import java.util.Collections;

public class CropValidator {

    private CropValidator() {
    }

    private static final ICropRegistry cropRegistry = ICropRegistry.INSTANCE;

    public static boolean isCrop(Block block) {
        return ConfigHelper.blockHarvestAllowed(block)
                && (block instanceof IAmHarvestable || cropRegistry.containsCrop(block));
    }

    public static boolean isStem(Block block) {
        return block instanceof AttachedStemBlock;
    }

    public static boolean isGrownCrop(BlockState state) {
        Block block = state.getBlock();
        if (!isCrop(block)) return false;
        if (block instanceof IAmHarvestable) {
            return ((IAmHarvestable) block).isFullyGrown(state);
        } else {
            IntProperty ageProperty = cropRegistry.getAgeProperty(block);
            if (ageProperty != null) {
                int maxValue = Collections.max(ageProperty.getValues());
                return state.get(ageProperty) == maxValue;
            } else if (block instanceof GourdBlock) {
                GourdBlock gourdBlock = (GourdBlock) block;
                return gourdBlock.getAttachedStem() != null;
            } else return false;
        }
    }

}
