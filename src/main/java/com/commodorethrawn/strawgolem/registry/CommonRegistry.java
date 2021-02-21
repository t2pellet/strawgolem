package com.commodorethrawn.strawgolem.registry;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.List;

public class CommonRegistry implements ModInitializer {

    static EntityType<EntityStrawGolem> STRAW_GOLEM_TYPE;

    public static EntityType<EntityStrawGolem> strawGolemEntityType() {
        return STRAW_GOLEM_TYPE;
    }

    @Override
    public void onInitialize() {
        registerEntity();
        registerSound();
    }

    /**
     * Registers the entities of the mod
     */
    public static void registerEntity() {
        STRAW_GOLEM_TYPE = Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(Strawgolem.MODID, "strawgolem"),
                EntityType.Builder.create(EntityStrawGolem::new, SpawnGroup.CREATURE)
                        .maxTrackingRange(48).trackingTickInterval(3).setDimensions(0.6F, 0.9F)
                        .build("strawgolem"));
        FabricDefaultAttributeRegistry.register(STRAW_GOLEM_TYPE, EntityStrawGolem.createMob());
    }

    /**
     * Registers the sounds of the mod
     */
    public static void registerSound() {
        List<StrawgolemSounds.Sound> sounds = Arrays.asList(
                StrawgolemSounds.GOLEM_AMBIENT,
                StrawgolemSounds.GOLEM_STRAINED,
                StrawgolemSounds.GOLEM_HURT,
                StrawgolemSounds.GOLEM_DEATH,
                StrawgolemSounds.GOLEM_HEAL,
                StrawgolemSounds.GOLEM_SCARED,
                StrawgolemSounds.GOLEM_INTERESTED);
        sounds.forEach(StrawgolemSounds.Sound::register);
    }
}
