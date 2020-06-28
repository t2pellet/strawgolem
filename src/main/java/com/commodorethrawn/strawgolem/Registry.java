package com.commodorethrawn.strawgolem;

import com.commodorethrawn.strawgolem.entity.strawgolem.EntityStrawGolem;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Strawgolem.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registry {

    public static EntityType<EntityStrawGolem> STRAW_GOLEM_TYPE;

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void registerEntity(final RegistryEvent.Register<EntityType<?>> event) {
        STRAW_GOLEM_TYPE = (EntityType<EntityStrawGolem>) EntityType.Builder.create(EntityStrawGolem::new, EntityClassification.CREATURE)
                                .setTrackingRange(48).setUpdateInterval(3).size(0.6F, 0.9F)
                                .build("strawgolem").setRegistryName(Strawgolem.MODID, "strawgolem");
        event.getRegistry().register(STRAW_GOLEM_TYPE);
    }

}
