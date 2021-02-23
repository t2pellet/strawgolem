package com.commodorethrawn.strawgolem.client.compat;

import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import net.minecraft.text.TranslatableText;

public class CompatHwyla {

    public static void patchTooltip(WailaTooltipEvent event) {
        if (event.getAccessor().getEntity() instanceof EntityStrawGolem && ConfigHelper.isEnableHwyla()) {
            EntityStrawGolem golem = (EntityStrawGolem) event.getAccessor().getEntity();
            float daysLeftLife = golem.getLifespan().get() / 24000F;
            float hungerLeft = golem.getHunger().get();
            if (daysLeftLife >= 1) {
                event.getCurrentTip().add(new TranslatableText("strawgolem.lifespan", Math.round(daysLeftLife)));
            } else if (golem.getLifespan().get() < 0) {
                event.getCurrentTip().add(new TranslatableText("strawgolem.lifespan", '\u221e'));
            } else {
                event.getCurrentTip().add(new TranslatableText("strawgolem.lifespan", "<1"));
            }
            if (hungerLeft >= ConfigHelper.getHunger()) {
                event.getCurrentTip().add(new TranslatableText("strawgolem.hunger", "Not At All Hungry"));
            } else if (hungerLeft >= ConfigHelper.getHunger() / 2F) {
                event.getCurrentTip().add(new TranslatableText("strawgolem.hunger", "A Little Bit Hungry"));
            } else if (hungerLeft >= ConfigHelper.getHunger() / 4F) {
                event.getCurrentTip().add(new TranslatableText("strawgolem.hunger", "Pretty Hungry"));
            } else if (hungerLeft > 0) {
                event.getCurrentTip().add(new TranslatableText("strawgolem.hunger", "Very Hungry"));
            } else {
                event.getCurrentTip().add(new TranslatableText("strawgolem.hunger", "Starving"));
            }
        }
    }
}
