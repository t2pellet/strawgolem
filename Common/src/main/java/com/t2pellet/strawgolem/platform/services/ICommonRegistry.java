package com.t2pellet.strawgolem.platform.services;

import com.t2pellet.strawgolem.crop.CropRegistry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static com.t2pellet.strawgolem.registry.CommonRegistry.Sounds.GOLEM_STRAINED;

public interface ICommonRegistry {

    Supplier<ParticleType<SimpleParticleType>> registerParticle(ResourceLocation id);

    <T extends LivingEntity> Supplier<EntityType<T>> registerEntity(String name, EntityType.EntityFactory<T> factory, float width, float height, Supplier<AttributeSupplier.Builder> builder);

    void registerSound(SoundEvent id);

    void registerEvents();

    static void registerCrop(Block block) {
        if (block instanceof StemGrownBlock) {
            CropRegistry.INSTANCE.register(block, input -> true, (level, golem, pos, input) -> {
                golem.playSound(GOLEM_STRAINED, 1.0F, 1.0F);
                return List.of(new ItemStack(Item.BY_BLOCK.getOrDefault(block, Items.AIR)));
            }, (level, pos, input) -> level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState()));
        } else if (block instanceof SweetBerryBushBlock) {
            CropRegistry.INSTANCE.register(block, CropRegistry.IHarvestChecker.getDefault(SweetBerryBushBlock.AGE, 3), CropRegistry.IHarvestLogic.RIGHT_CLICK, CropRegistry.IReplantLogic.getDefault(SweetBerryBushBlock.AGE, 1));
        } else if (block instanceof BushBlock && (block instanceof BonemealableBlock || block instanceof NetherWartBlock) && !(block instanceof StemBlock)) {
            IntegerProperty[] ageProperties = {BlockStateProperties.AGE_2, BlockStateProperties.AGE_3, BlockStateProperties.AGE_5, BlockStateProperties.AGE_7};
            Arrays.stream(ageProperties)
                    .filter(age -> block.defaultBlockState().hasProperty(age))
                    .findFirst().ifPresent(integerProperty -> CropRegistry.INSTANCE.register(block, CropRegistry.IHarvestChecker.getDefault(integerProperty), CropRegistry.IHarvestLogic.DEFAULT, CropRegistry.IReplantLogic.getDefault(integerProperty)));
        }
    }

}
