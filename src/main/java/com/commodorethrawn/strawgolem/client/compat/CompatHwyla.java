package com.commodorethrawn.strawgolem.client.compat;

import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CompatHwyla {

    private CompatHwyla() {
    }

    @SubscribeEvent
    public static void onTooltip(WailaTooltipEvent event) {
        if (event.getAccessor().getEntity() instanceof EntityStrawGolem && ConfigHelper.isEnableHwyla()) {
            EntityStrawGolem golem = (EntityStrawGolem) event.getAccessor().getEntity();
            float daysLeft = golem.getCurrentLifespan() / 24000F;
            if (daysLeft >= 1) {
                event.getCurrentTip().add(new TranslationTextComponent("strawgolem.lifespan", Math.round(daysLeft)));
            } else if (golem.getCurrentLifespan() < 0) {
                event.getCurrentTip().add(new TranslationTextComponent("strawgolem.lifespan", '\u221e'));
            } else {
                event.getCurrentTip().add(new TranslationTextComponent("strawgolem.lifespan", "<1"));
            }
        }
    }
}
