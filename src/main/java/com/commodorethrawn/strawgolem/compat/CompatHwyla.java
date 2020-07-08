package com.commodorethrawn.strawgolem.compat;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.entity.strawgolem.EntityStrawGolem;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Strawgolem.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CompatHwyla {
    @SubscribeEvent
    public static void onTooltip(WailaTooltipEvent event) {
        if (event.getAccessor().getEntity() instanceof EntityStrawGolem) {
            EntityStrawGolem golem = (EntityStrawGolem) event.getAccessor().getEntity();
            int daysLeft = golem.getCurrentLifespan() / 24000;
            if (daysLeft >= 1) {
                event.getCurrentTip().add(new TranslationTextComponent("strawgolem.lifespan", golem.getCurrentLifespan() / 24000));
            } else {
                event.getCurrentTip().add(new TranslationTextComponent("strawgolem.lifespan", "<1"));
            }
        }
    }
}
