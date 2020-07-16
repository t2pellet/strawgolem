package com.commodorethrawn.strawgolem.events;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.ai.PickupGolemGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Strawgolem.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class IronGolemHandler {

    @SubscribeEvent
    public static void ironGolemSpawn(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote
                && event.getEntity() instanceof IronGolemEntity
                && ConfigHelper.doGolemPickup()) {
            IronGolemEntity golem = (IronGolemEntity) event.getEntity();
            golem.goalSelector.addGoal(2, new PickupGolemGoal(golem, 0.8D));
        }
    }
}
