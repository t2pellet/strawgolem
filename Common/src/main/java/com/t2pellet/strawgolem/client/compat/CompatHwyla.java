package com.t2pellet.strawgolem.client.compat;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import mcp.mobius.waila.api.*;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class CompatHwyla implements IWailaPlugin, IEntityComponentProvider {

    private static final ResourceLocation LIFESPAN = new ResourceLocation(StrawgolemCommon.MODID, "lifespan");
    private static final ResourceLocation HUNGER = new ResourceLocation(StrawgolemCommon.MODID, "hunger");

    @Override
    public void register(IRegistrar registrar) {
        if (StrawgolemConfig.Miscellaneous.isEnableHwyla()) {
            StrawgolemCommon.LOG.info("Registering Strawgolem HWYLA Compat");
            registrar.addConfig(LIFESPAN, StrawgolemConfig.Health.getLifespan() > 0);
            registrar.addConfig(HUNGER, StrawgolemConfig.Health.getHunger() > 0);
            registrar.addComponent(this, TooltipPosition.BODY, StrawGolem.class);
        } else {
            StrawgolemCommon.LOG.info("Strawgolem HWYLA compat is disabled");
        }
    }

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (accessor.getEntity() instanceof StrawGolem golem) {
            if (config.getBoolean(LIFESPAN) && StrawgolemConfig.Health.getLifespan() > 0) {
                float daysLeftLife = golem.getLifespan().get() / 24000F;
                if (daysLeftLife >= 1) {
                    tooltip.addLine(new TranslatableComponent("strawgolem.lifespan", Math.round(daysLeftLife)));
                } else if (golem.getLifespan().get() < 0) {
                    tooltip.addLine(new TranslatableComponent("strawgolem.lifespan", '\u221e'));
                } else {
                    tooltip.addLine(new TranslatableComponent("strawgolem.lifespan", "<1"));
                }
            }
            if (config.getBoolean(HUNGER) && StrawgolemConfig.Health.getHunger() > 0) {
                float hungerLeft = golem.getHunger().get();
                if (hungerLeft >= StrawgolemConfig.Health.getHunger()) {
                    tooltip.addLine(new TranslatableComponent("strawgolem.hunger", "Not At All Hungry"));
                } else if (hungerLeft >= StrawgolemConfig.Health.getHunger() / 2F) {
                    tooltip.addLine(new TranslatableComponent("strawgolem.hunger", "A Little Bit Hungry"));
                } else if (hungerLeft >= StrawgolemConfig.Health.getHunger() / 4F) {
                    tooltip.addLine(new TranslatableComponent("strawgolem.hunger", "Pretty Hungry"));
                } else if (hungerLeft > 0) {
                    tooltip.addLine(new TranslatableComponent("strawgolem.hunger", "Very Hungry"));
                } else {
                    tooltip.addLine(new TranslatableComponent("strawgolem.hunger", "Starving"));
                }
            }
        }
    }
}
