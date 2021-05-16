package com.commodorethrawn.strawgolem.registry;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.commodorethrawn.strawgolem.entity.EntityStrawngGolem;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class StrawgolemEntities {

    static EntityType<EntityStrawGolem> STRAW_GOLEM_TYPE;
    static EntityType<EntityStrawngGolem> STRAWNG_GOLEM_TYPE;

    public static EntityType<EntityStrawGolem> strawGolemEntityType() {
        return STRAW_GOLEM_TYPE;
    }

    public static EntityType<EntityStrawngGolem> strawngGolemEntityType() {
        return STRAWNG_GOLEM_TYPE;
    }

    public static void register() {
        STRAW_GOLEM_TYPE = registerEntity("strawgolem", EntityStrawGolem::new, SpawnGroup.CREATURE, 0.6F, 0.9F);
        FabricDefaultAttributeRegistry.register(STRAW_GOLEM_TYPE, EntityStrawGolem.createMob());
        STRAWNG_GOLEM_TYPE = registerEntity("strawnggolem", EntityStrawngGolem::new, SpawnGroup.CREATURE, 1.25F, 3.5F);
        FabricDefaultAttributeRegistry.register(STRAWNG_GOLEM_TYPE, EntityStrawngGolem.createMob());
    }

    private static <T extends Entity> EntityType<T> registerEntity(String name, EntityType.EntityFactory<T> factory, SpawnGroup spawnGroup, float width, float height) {
        return Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(Strawgolem.MODID, name),
                EntityType.Builder.create(factory, spawnGroup)
                    .maxTrackingRange(48).trackingTickInterval(3).setDimensions(width, height)
                    .build(name));
    }

}
