package com.commodorethrawn.strawgolem.entity;

import com.commodorethrawn.strawgolem.Strawgolem;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Strawgolem.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRegistry {

    public static EntityType STRAW_GOLEM_TYPE;

    @SubscribeEvent
    public static void registerEntity(final RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(STRAW_GOLEM_TYPE = EntityType.Builder.create(EntityStrawGolem::new, EntityClassification.CREATURE)
                .setShouldReceiveVelocityUpdates(true).setTrackingRange(48).setUpdateInterval(2).size(0.6F, 0.9F)
                                                             .build("strawgolem").setRegistryName(Strawgolem.MODID, "strawgolem"));
    }

}
