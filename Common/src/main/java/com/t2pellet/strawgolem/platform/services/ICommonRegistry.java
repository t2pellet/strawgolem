package com.t2pellet.strawgolem.platform.services;

import com.t2pellet.strawgolem.crop.CropRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ICommonRegistry {


    Supplier<ParticleType<SimpleParticleType>> registerParticle(ResourceLocation id);

    <T extends LivingEntity> Supplier<EntityType<T>> registerEntity(String name, EntityType.EntityFactory<T> factory, float width, float height, Supplier<AttributeSupplier.Builder> builder);

    void registerSound(ResourceLocation id);

    void registerEvents();

    static void registerCrop(Block block) {
        if (block instanceof CropBlock) CropRegistry.INSTANCE.register(block, ((CropBlock) block).getAgeProperty());
        else if (block instanceof StemGrownBlock) CropRegistry.INSTANCE.register(block, null);
        else if (block instanceof NetherWartBlock) CropRegistry.INSTANCE.register(block, NetherWartBlock.AGE);
        else if (block instanceof BushBlock && block instanceof BonemealableBlock) {
            // Register any Fertilizable PlantBlock with 3, 5 or 7 age states
            IntegerProperty[] ageProperties = {BlockStateProperties.AGE_3, BlockStateProperties.AGE_5, BlockStateProperties.AGE_7};
            Arrays.stream(ageProperties)
                    .filter(age -> block.defaultBlockState().hasProperty(age))
                    .findFirst()
                    .ifPresent(ageProperty -> CropRegistry.INSTANCE.register(block, ageProperty));
        }
    }

}
