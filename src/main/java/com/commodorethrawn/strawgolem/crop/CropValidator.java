package com.commodorethrawn.strawgolem.crop;

import com.commodorethrawn.strawgolem.config.ConfigHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.IntProperty;

import java.util.Collections;

public class CropValidator {

    private CropValidator() {
    }

    private static final CropRegistry cropRegistry = CropRegistry.INSTANCE;

    public static boolean isCrop(Block block) {
        return (block instanceof IAmHarvestable || cropRegistry.containsCrop(block))
                && ConfigHelper.blockHarvestAllowed(block);
    }

    public static boolean isCrop(BlockEntity blockEntity) {
        return (blockEntity instanceof IAmHarvestable || cropRegistry.containsCrop(blockEntity))
                && ConfigHelper.blockHarvestAllowed(blockEntity.getCachedState().getBlock());
    }

    public static boolean isStem(Block block) {
        return block instanceof AttachedStemBlock;
    }

    public static boolean isGrownCrop(BlockState state) {
        Block block = state.getBlock();
        if (!isCrop(block)) return false;
        if (block instanceof IAmHarvestable) {
            return ((IAmHarvestable) block).isMature(state);
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

    public static boolean isGrownCrop(BlockEntity blockEntity) {
        IntProperty ageProperty = cropRegistry.getAgeProperty(blockEntity);
        if (ageProperty != null) {
            int maxValue = Collections.max(ageProperty.getValues());
            return blockEntity.getCachedState().get(ageProperty) == maxValue;
        } else return false;
    }

}
