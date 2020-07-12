package com.commodorethrawn.strawgolem.compat;

import com.commodorethrawn.strawgolem.entity.strawgolem.EntityStrawGolem;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CompatHwyla {
    @SubscribeEvent
    public static void onTooltip(WailaTooltipEvent event) {
        if (event.getAccessor().getEntity() instanceof EntityStrawGolem) {
            EntityStrawGolem golem = (EntityStrawGolem) event.getAccessor().getEntity();
            float daysLeft = golem.getCurrentLifespan() / 24000F;
            if (daysLeft >= 1) {
                event.getCurrentTip().add(new TranslationTextComponent("strawgolem.lifespan", Math.round(daysLeft)));
            } else {
                event.getCurrentTip().add(new TranslationTextComponent("strawgolem.lifespan", "<1"));
            }
        }
    }
}
