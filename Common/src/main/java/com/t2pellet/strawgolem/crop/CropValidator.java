package com.t2pellet.strawgolem.crop;

import com.t2pellet.strawgolem.config.StrawgolemConfig;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.Collections;

public class CropValidator {

    private CropValidator() {
    }

    private static final CropRegistry cropRegistry = CropRegistry.INSTANCE;

    public static boolean isCrop(Block block) {
        return (block instanceof IAmHarvestable || cropRegistry.containsCrop(block))
                && StrawgolemConfig.Harvest.isHarvestAllowed(block);
    }

    public static boolean isCrop(BlockEntity blockEntity) {
        return (blockEntity instanceof IAmHarvestable || cropRegistry.containsCrop(blockEntity))
                && StrawgolemConfig.Harvest.isHarvestAllowed(blockEntity.getBlockState().getBlock());
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
            IntegerProperty ageProperty = cropRegistry.getAgeProperty(block);
            if (ageProperty != null) {
                int maxValue = Collections.max(ageProperty.getPossibleValues());
                return state.getValue(ageProperty) == maxValue;
            } else if (block instanceof StemGrownBlock) {
                StemGrownBlock gourdBlock = (StemGrownBlock) block;
                return gourdBlock.getAttachedStem() != null;
            } else return false;
        }
    }

    public static boolean isGrownCrop(BlockEntity blockEntity) {
        IntegerProperty ageProperty = cropRegistry.getAgeProperty(blockEntity);
        if (ageProperty != null) {
            int maxValue = Collections.max(ageProperty.getPossibleValues());
            return blockEntity.getBlockState().getValue(ageProperty) == maxValue;
        } else return false;
    }

}
