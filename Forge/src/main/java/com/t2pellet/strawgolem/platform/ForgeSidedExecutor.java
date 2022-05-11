package com.t2pellet.strawgolem.platform;

import com.t2pellet.strawgolem.platform.services.ISidedExecutor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ForgeSidedExecutor implements ISidedExecutor {
    @Override
    @OnlyIn(Dist.CLIENT)
    public void scheduleClient(Runnable runnable) {
        net.minecraft.client.Minecraft.getInstance().execute(runnable);
    }

    @Override
    public void scheduleServer(Runnable runnable) {
        ServerLifecycleHooks.getCurrentServer().execute(runnable);
    }
}
