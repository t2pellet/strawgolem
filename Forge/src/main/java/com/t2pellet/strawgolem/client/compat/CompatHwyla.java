package com.t2pellet.strawgolem.client.compat;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin("strawgolem")
public class CompatHwyla implements IWailaPlugin, IEntityComponentProvider {

    protected static final ResourceLocation ID = new ResourceLocation(StrawgolemCommon.MODID, "jade");
    protected static final ResourceLocation LIFESPAN = new ResourceLocation(StrawgolemCommon.MODID, "lifespan");
    protected static final ResourceLocation HUNGER = new ResourceLocation(StrawgolemCommon.MODID, "hunger");
    private static final IEntityComponentProvider INSTANCE = new CompatHwyla();

    @Override
    public void registerClient(IWailaClientRegistration registrar) {
        if (StrawgolemConfig.Miscellaneous.isEnableHwyla()) {
            StrawgolemCommon.LOG.info("Registering Strawgolem HWYLA Compat");
            registrar.registerEntityComponent(INSTANCE, StrawGolem.class);
            registrar.addConfig(LIFESPAN, StrawgolemConfig.Health.getLifespan() >= 0);
            registrar.addConfig(HUNGER, StrawgolemConfig.Health.getHunger() >= 0);
        } else {
            StrawgolemCommon.LOG.info("Strawgolem HWYLA compat is disabled");
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (accessor.getEntity() instanceof StrawGolem golem) {
            if (config.get(LIFESPAN) && StrawgolemConfig.Health.getLifespan() > 0) {
                float daysLeftLife = golem.getLifespan().get() / 24000F;
                if (daysLeftLife >= 1) {
                    tooltip.add(Component.translatable("strawgolem.lifespan", Math.round(daysLeftLife)));
                } else if (golem.getLifespan().get() < 0) {
                    tooltip.add(Component.translatable("strawgolem.lifespan", '\u221e'));
                } else {
                    tooltip.add(Component.translatable("strawgolem.lifespan", "<1"));
                }
            }
            if (config.get(HUNGER) && StrawgolemConfig.Health.getHunger() > 0) {
                float hungerLeft = golem.getHunger().get();
                if (hungerLeft >= StrawgolemConfig.Health.getHunger()) {
                    tooltip.add(Component.translatable("strawgolem.hunger", "Not At All Hungry"));
                } else if (hungerLeft >= StrawgolemConfig.Health.getHunger() / 2F) {
                    tooltip.add(Component.translatable("strawgolem.hunger", "A Little Bit Hungry"));
                } else if (hungerLeft >= StrawgolemConfig.Health.getHunger() / 4F) {
                    tooltip.add(Component.translatable("strawgolem.hunger", "Pretty Hungry"));
                } else if (hungerLeft > 0) {
                    tooltip.add(Component.translatable("strawgolem.hunger", "Very Hungry"));
                } else {
                    tooltip.add(Component.translatable("strawgolem.hunger", "Starving"));
                }
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }
}
