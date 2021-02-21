package com.commodorethrawn.strawgolem.client.compat;

import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import net.minecraft.text.TranslatableText;

public class CompatHwyla {

    public static void patchTooltip(WailaTooltipEvent event) {
        if (event.getAccessor().getEntity() instanceof EntityStrawGolem && ConfigHelper.isEnableHwyla()) {
            EntityStrawGolem golem = (EntityStrawGolem) event.getAccessor().getEntity();
            float daysLeft = golem.getLifespan().get() / 24000F;
            if (daysLeft >= 1) {
                event.getCurrentTip().add(new TranslatableText("strawgolem.lifespan", Math.round(daysLeft)));
            } else if (golem.getLifespan().get() < 0) {
                event.getCurrentTip().add(new TranslatableText("strawgolem.lifespan", '\u221e'));
            } else {
                event.getCurrentTip().add(new TranslatableText("strawgolem.lifespan", "<1"));
            }
        }
    }
}
