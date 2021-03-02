package com.commodorethrawn.strawgolem.registry;

import com.commodorethrawn.strawgolem.client.renderer.entity.RenderIronGolem;
import com.commodorethrawn.strawgolem.client.renderer.entity.RenderStrawGolem;
import com.commodorethrawn.strawgolem.client.renderer.entity.RenderStrawngGolem;
import com.commodorethrawn.strawgolem.config.ConfigHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.entity.EntityType;

import static com.commodorethrawn.strawgolem.registry.StrawgolemEntities.STRAWNG_GOLEM_TYPE;
import static com.commodorethrawn.strawgolem.registry.StrawgolemEntities.STRAW_GOLEM_TYPE;

public class ClientRegistry {

    @Environment(EnvType.CLIENT)
    public static void register() {
        StrawgolemParticles.register();
        registerEntityRenderer();
    }

    @Environment(EnvType.CLIENT)
    public static void registerEntityRenderer() {
        EntityRendererRegistry.INSTANCE.register(STRAW_GOLEM_TYPE, (dispatcher, ctx) -> {
            return new RenderStrawGolem(dispatcher);
        });
        EntityRendererRegistry.INSTANCE.register(STRAWNG_GOLEM_TYPE, (dispatcher, ctx) -> {
            return new RenderStrawngGolem(dispatcher);
        });
        if (ConfigHelper.doGolemPickup()) {
            EntityRendererRegistry.INSTANCE.register(EntityType.IRON_GOLEM, (dispatcher, ctx) -> {
                return new RenderIronGolem(dispatcher);
            });
        }
    }
}
