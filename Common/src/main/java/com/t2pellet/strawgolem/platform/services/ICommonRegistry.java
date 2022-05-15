package com.t2pellet.strawgolem.platform.services;

import com.t2pellet.strawgolem.crop.CropRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.Arrays;
import java.util.function.Supplier;

public interface ICommonRegistry {


    Supplier<ParticleType<SimpleParticleType>> registerParticle(ResourceLocation id);

    <T extends LivingEntity> Supplier<EntityType<T>> registerEntity(String name, EntityType.EntityFactory<T> factory, float width, float height, Supplier<AttributeSupplier.Builder> builder);

    void registerSound(ResourceLocation id);

    void registerEvents();

    static void registerCrop(Block block) {
        if (block instanceof StemGrownBlock) {
            CropRegistry.INSTANCE.register(block, new CropRegistry.IHarvestData<>() {
                @Override
                public boolean isMature(BlockState input) {
                    return true;
                }

                @Override
                public void doReplant(Level level, BlockPos pos, BlockState input) {
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            });
        } else if (block instanceof SweetBerryBushBlock) {
            CropRegistry.INSTANCE.register(block, new CropRegistry.DefaultHarvestData(SweetBerryBushBlock.AGE, 3, 1));
        } else if (block instanceof BushBlock && (block instanceof BonemealableBlock || block instanceof NetherWartBlock)) {
            IntegerProperty[] ageProperties = {BlockStateProperties.AGE_2, BlockStateProperties.AGE_3, BlockStateProperties.AGE_5, BlockStateProperties.AGE_7};
            Arrays.stream(ageProperties)
                    .filter(age -> block.defaultBlockState().hasProperty(age))
                    .findFirst().ifPresent(integerProperty -> {
                        CropRegistry.INSTANCE.register(block, new CropRegistry.DefaultHarvestData(integerProperty));
                    });
        }
    }

}
