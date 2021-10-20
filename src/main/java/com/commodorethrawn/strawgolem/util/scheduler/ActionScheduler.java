package com.commodorethrawn.strawgolem.util.scheduler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public interface ActionScheduler {

    ActionScheduler INSTANCE = new ActionSchedulerImpl();

    @Environment(EnvType.CLIENT)
    void tickClient(ClientWorld world);

    void tickServer(ServerWorld world);

    void scheduleClientTask(int tickDelay, Runnable task);

    void scheduleServerTask(int tickDelay, Runnable task);

}
