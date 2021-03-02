package com.commodorethrawn.strawgolem.registry;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.commodorethrawn.strawgolem.entity.EntityStrawngGolem;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
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
        STRAW_GOLEM_TYPE = Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(Strawgolem.MODID, "strawgolem"),
                EntityType.Builder.create(EntityStrawGolem::new, SpawnGroup.CREATURE)
                        .maxTrackingRange(48).trackingTickInterval(3).setDimensions(0.6F, 0.9F)
                        .build("strawgolem"));
        FabricDefaultAttributeRegistry.register(STRAW_GOLEM_TYPE, EntityStrawGolem.createMob());
        STRAWNG_GOLEM_TYPE = Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(Strawgolem.MODID, "strawnggolem"),
                EntityType.Builder.create(EntityStrawngGolem::new, SpawnGroup.CREATURE)
                        .maxTrackingRange(48).trackingTickInterval(3).setDimensions(1.25F, 3.5F)
                        .build("strawnggolem"));
        FabricDefaultAttributeRegistry.register(STRAWNG_GOLEM_TYPE, EntityStrawngGolem.createMob());
    }

}
