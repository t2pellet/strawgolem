package com.commodorethrawn.strawgolem;

import com.commodorethrawn.strawgolem.client.particle.FlyParticle;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Strawgolem.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registry {

    public static EntityType<EntityStrawGolem> STRAW_GOLEM_TYPE;

    /**
     * Registers the entities of the mod
     *
     * @param event
     */
    @SubscribeEvent
    public static void registerEntity(final RegistryEvent.Register<EntityType<?>> event) {
        STRAW_GOLEM_TYPE = EntityType.Builder.create(EntityStrawGolem::new, EntityClassification.CREATURE)
                .setTrackingRange(48).setUpdateInterval(3).size(0.6F, 0.9F)
                .build("strawgolem");
        STRAW_GOLEM_TYPE.setRegistryName(Strawgolem.MODID, "strawgolem");
        event.getRegistry().register(STRAW_GOLEM_TYPE);
    }

    /**
     * Registers the sounds of the mod
     *
     * @param event
     */
    @SubscribeEvent
    public static void registerSound(final RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
                EntityStrawGolem.GOLEM_AMBIENT.setRegistryName(Strawgolem.MODID, "golem_ambient"),
                EntityStrawGolem.GOLEM_STRAINED.setRegistryName(Strawgolem.MODID, "golem_strained"),
                EntityStrawGolem.GOLEM_HURT.setRegistryName(Strawgolem.MODID, "golem_hurt"),
                EntityStrawGolem.GOLEM_DEATH.setRegistryName(Strawgolem.MODID, "golem_death"),
                EntityStrawGolem.GOLEM_HEAL.setRegistryName(Strawgolem.MODID, "golem_heal"),
                EntityStrawGolem.GOLEM_SCARED.setRegistryName(Strawgolem.MODID, "golem_scared"),
                EntityStrawGolem.GOLEM_INTERESTED.setRegistryName(Strawgolem.MODID, "golem_interested")
        );
    }

}
