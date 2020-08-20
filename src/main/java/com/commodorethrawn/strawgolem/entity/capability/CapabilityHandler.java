package com.commodorethrawn.strawgolem.entity.capability;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.commodorethrawn.strawgolem.entity.capability.lifespan.LifespanProvider;
import com.commodorethrawn.strawgolem.entity.capability.memory.MemoryProvider;
import com.commodorethrawn.strawgolem.entity.capability.profession.ProfessionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Strawgolem.MODID)
public class CapabilityHandler {

    private CapabilityHandler() {
    }

    public static final ResourceLocation LIFESPAN_RES = new ResourceLocation(Strawgolem.MODID, "lifespan");
    public static final ResourceLocation CROPSLOT_RES = new ResourceLocation(Strawgolem.MODID, "cropslot");
    public static final ResourceLocation MEMORY_RES = new ResourceLocation(Strawgolem.MODID, "memory");
    public static final ResourceLocation PROFESSION_RES = new ResourceLocation(Strawgolem.MODID, "profession");

    /**
     * Attaches the mods capabilities
     */
    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityStrawGolem) {
            event.addCapability(LIFESPAN_RES, new LifespanProvider());
            event.addCapability(CROPSLOT_RES, new InventoryProvider());
            event.addCapability(MEMORY_RES, new MemoryProvider());
            event.addCapability(PROFESSION_RES, new ProfessionProvider());
        }
    }

}
