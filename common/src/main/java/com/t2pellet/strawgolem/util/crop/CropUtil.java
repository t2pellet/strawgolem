package com.t2pellet.strawgolem.util.crop;

import com.t2pellet.strawgolem.Constants;
import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.compat.api.HarvestableBlock;
import com.t2pellet.strawgolem.compat.api.HarvestableState;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CropUtil {

    private static final TagKey<Block> HARVESTABLE_CROPS = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(Constants.MOD_ID, "crops"));
    private static final Set<ResourceLocation> BLACKLISTED_CROPS = new HashSet<>();
    private static final Set<ResourceLocation> WHITELISTED_CROPS = new HashSet<>();

    private CropUtil() {}

    static {
        List<String> blacklistKeys = StrawgolemConfig.Harvesting.blacklist.get();
        for (String blacklistKey : blacklistKeys) {
            BLACKLISTED_CROPS.add(new ResourceLocation(blacklistKey));
        }
        List<String> whitelistKeys = StrawgolemConfig.Harvesting.whitelist.get();
        for (String whitelistKey : whitelistKeys) {
            WHITELISTED_CROPS.add(new ResourceLocation(whitelistKey));
        }
    }

    public static boolean isCrop(LevelAccessor level, BlockPos pos) {
        return pos != null && isCrop(level.getBlockState(pos));
    }

    public static boolean isGrownCrop(LevelAccessor level, BlockPos pos) {
        return pos != null && isGrownCrop(level.getBlockState(pos));
    }

    public static boolean isGrownCrop(BlockState state) {
        if (!isCrop(state)) return false;

        if (state.getBlock() instanceof CropBlock cropBlock) {
            return cropBlock.isMaxAge(state);
        } else if (state.getBlock() instanceof HarvestableBlock cropBlock) {
            return cropBlock.isMaxAge(state);
        } else if (state instanceof HarvestableState cropBlock) {
            return cropBlock.isMaxAge();
        } else if (state.getBlock() instanceof StemGrownBlock) {
            return true;
        }

        boolean whitelistedCrop = StrawgolemConfig.Harvesting.enableWhitelist.get() && isWhitelisted(state.getBlock());
        if (state.is(HARVESTABLE_CROPS) || whitelistedCrop) {
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
            }
        }
        return false;
    }

    // TODO : Should probably check here that it has one of the age properties if its from the tag system
    public static boolean isCrop(BlockState state) {
        boolean isCrop = state.getBlock() instanceof CropBlock
                || state.getBlock() instanceof HarvestableBlock
                || state instanceof HarvestableState
                || state.is(HARVESTABLE_CROPS)
                || StrawgolemConfig.Harvesting.shouldHarvestBlocks.get() && state.getBlock() instanceof StemGrownBlock;
        if (StrawgolemConfig.Harvesting.enableWhitelist.get()) {
            return isCrop || isWhitelisted(state.getBlock());
        }
        return isCrop && !isBlacklisted(state.getBlock());
    }

    private static boolean isBlacklisted(Block block) {
        ResourceLocation location = Registry.BLOCK.getKey(block);
        return BLACKLISTED_CROPS.contains(location);
    }

    private static boolean isWhitelisted(Block block) {
        ResourceLocation location = Registry.BLOCK.getKey(block);
        return WHITELISTED_CROPS.contains(location);
    }

}
