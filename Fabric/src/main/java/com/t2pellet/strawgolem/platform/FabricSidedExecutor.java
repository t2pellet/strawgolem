package com.t2pellet.strawgolem.platform;

import com.t2pellet.strawgolem.StrawgolemFabric;
import com.t2pellet.strawgolem.platform.services.ISidedExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class FabricSidedExecutor implements ISidedExecutor {
    @Override
    @Environment(EnvType.CLIENT)
    public void scheduleClient(Runnable runnable) {
        net.minecraft.client.Minecraft.getInstance().execute(runnable);
    }

    @Override
    public void scheduleServer(Runnable runnable) {
        StrawgolemFabric.getServer().execute(runnable);
    }
}
