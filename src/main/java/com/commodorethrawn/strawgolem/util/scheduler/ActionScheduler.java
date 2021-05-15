package com.commodorethrawn.strawgolem.util.scheduler;

import net.minecraft.world.World;

public interface ActionScheduler {

    ActionScheduler INSTANCE = new ActionSchedulerImpl();

    void tick(World world);

    void scheduleClientTask(int tickDelay, Runnable task);

    void scheduleServerTask(int tickDelay, Runnable task);

}
