package com.t2pellet.strawgolem.util.crop;

import com.t2pellet.strawgolem.Constants;
import com.t2pellet.strawgolem.compat.HarvestableBlock;
import com.t2pellet.strawgolem.compat.HarvestableState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class CropUtil {

    private static final TagKey<Block> HARVESTABLE_CROPS = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(Constants.MOD_ID, "crops"));

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
        } else if (state.is(HARVESTABLE_CROPS)) {
            if (state.hasProperty(BlockStateProperties.AGE_1)) {
                return state.getValue(BlockStateProperties.AGE_1).intValue() == BlockStateProperties.MAX_AGE_1;
            } else if (state.hasProperty(BlockStateProperties.AGE_2)) {
                return state.getValue(BlockStateProperties.AGE_2).intValue() == BlockStateProperties.MAX_AGE_2;
            } else if (state.hasProperty(BlockStateProperties.AGE_3)) {
                return state.getValue(BlockStateProperties.AGE_3).intValue() == BlockStateProperties.MAX_AGE_3;
            } else if (state.hasProperty(BlockStateProperties.AGE_4)) {
                return state.getValue(BlockStateProperties.AGE_4).intValue() == BlockStateProperties.MAX_AGE_4;
            } else if (state.hasProperty(BlockStateProperties.AGE_5)) {
                return state.getValue(BlockStateProperties.AGE_5).intValue() == BlockStateProperties.MAX_AGE_5;
            } else if (state.hasProperty(BlockStateProperties.AGE_7)) {
                return state.getValue(BlockStateProperties.AGE_7).intValue() == BlockStateProperties.MAX_AGE_7;
            } else if (state.hasProperty(BlockStateProperties.AGE_15)) {
                return state.getValue(BlockStateProperties.AGE_15).intValue() == BlockStateProperties.MAX_AGE_15;
            } else if (state.hasProperty(BlockStateProperties.AGE_25)) {
                return state.getValue(BlockStateProperties.AGE_25).intValue() == BlockStateProperties.MAX_AGE_25;
            } else return false;
        }
        else return state.getBlock() instanceof StemGrownBlock;
    }

    // TODO : Should probably check here that it has one of the age properties if its from the tag system
    public static boolean isCrop(BlockState state) {
        return state.getBlock() instanceof CropBlock || state.getBlock() instanceof HarvestableBlock || state instanceof HarvestableState || state.is(HARVESTABLE_CROPS);
    }

}
