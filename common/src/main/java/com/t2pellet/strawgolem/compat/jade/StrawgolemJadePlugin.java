package com.t2pellet.strawgolem.compat.jade;

import com.t2pellet.strawgolem.Constants;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class StrawgolemJadePlugin implements IWailaPlugin, IEntityComponentProvider {

    private static final ResourceLocation UUID = new ResourceLocation(Constants.MOD_ID, "strawgolem");
    private static final ResourceLocation ENABLED = new ResourceLocation(Constants.MOD_ID, "strawgolem_enabled");

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(this, StrawGolem.class);
        registration.addConfig(ENABLED, true);
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        if (iPluginConfig.get(ENABLED)) {
            StrawGolem golem = (StrawGolem) entityAccessor.getEntity();
            String decay = golem.getDecay().getState().getDescription();
            iTooltip.add(Component.translatable(decay));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UUID;
    }
}
