package com.commodorethrawn.strawgolem.registry;

import com.commodorethrawn.strawgolem.client.renderer.entity.RenderIronGolem;
import com.commodorethrawn.strawgolem.client.renderer.entity.RenderStrawGolem;
import com.commodorethrawn.strawgolem.client.renderer.entity.RenderStrawngGolem;
import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.commodorethrawn.strawgolem.util.scheduler.ActionScheduler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.entity.EntityType;

import static com.commodorethrawn.strawgolem.registry.StrawgolemEntities.STRAWNG_GOLEM_TYPE;
import static com.commodorethrawn.strawgolem.registry.StrawgolemEntities.STRAW_GOLEM_TYPE;

public class ClientRegistry {

    @Environment(EnvType.CLIENT)
    public static void register() {
        ClientTickEvents.END_WORLD_TICK.register(ActionScheduler.INSTANCE::tick);
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
        if (StrawgolemConfig.Miscellaneous.isGolemInteract()) {
            EntityRendererRegistry.INSTANCE.register(EntityType.IRON_GOLEM, (dispatcher, ctx) -> {
                return new RenderIronGolem(dispatcher);
            });
        }
    }
}
