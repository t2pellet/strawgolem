package com.t2pellet.strawgolem.platform;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.events.CropGrowthCallback;
import com.t2pellet.strawgolem.events.CropGrowthHandler;
import com.t2pellet.strawgolem.events.WorldInteractHandler;
import com.t2pellet.strawgolem.platform.services.ICommonRegistry;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class FabricCommonRegistry implements ICommonRegistry {

    @Override
    public Supplier<SimpleParticleType> registerParticle(ResourceLocation id) {
        SimpleParticleType type = new SimpleParticleType(true) {
        };
        Registry.register(Registry.PARTICLE_TYPE, id, type);
        return () -> type;
    }

    @Override
    public <T extends LivingEntity> Supplier<EntityType<T>> registerEntity(String name, EntityType.EntityFactory<T> factory, float width, float height, Supplier<AttributeSupplier.Builder> builder) {
        var type = Registry.register(
                Registry.ENTITY_TYPE,
                new ResourceLocation(StrawgolemCommon.MODID, name),
                EntityType.Builder.of(factory, MobCategory.CREATURE)
                        .clientTrackingRange(48).updateInterval(3).sized(width, height)
                        .build(name));
        FabricDefaultAttributeRegistry.register(type, builder.get());
        return () -> type;
    }

    @Override
    public void registerSound(SoundEvent id) {
        Registry.register(Registry.SOUND_EVENT, id.getLocation(), id);
    }

    @Override
    public Supplier<Item> registerItem(ResourceLocation location, Item.Properties properties) {
        Item item = Registry.register(Registry.ITEM, location, new Item(properties));
        return () -> item;
    }

    @Override
    public void registerEvents() {
        // Crop Registry
        RegistryEntryAddedCallback.event(Registry.BLOCK).register((i, id, block) -> ICommonRegistry.registerCrop(block));
        //Crop growth handling
        CropGrowthCallback.EVENT.register(CropGrowthHandler::onCropGrowth);
        //Golem Creation Handling
        UseBlockCallback.EVENT.register(WorldInteractHandler::onGolemBuilt);
        UseBlockCallback.EVENT.register(WorldInteractHandler::onGolemSheared);
        //Chest Handling
        UseBlockCallback.EVENT.register(WorldInteractHandler::setPriorityChest);
    }
}
