package com.commodorethrawn.strawgolem.util.scheduler;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.PriorityQueue;

class ActionSchedulerImpl implements ActionScheduler {

    private final PriorityQueue<ActionEntry> clientQueue = new PriorityQueue<>();
    private final PriorityQueue<ActionEntry> serverQueue = new PriorityQueue<>();
    private long ticks = 0;

    ActionSchedulerImpl() {
    }

    @Override
    public void tick(World world) {
        if (world instanceof ClientWorld) {
            while (!clientQueue.isEmpty() && clientQueue.peek().getRunTick() <= ticks) {
                clientQueue.remove().execute();
            }
            ++ticks;
        } else if (world instanceof ServerWorld) {
            while (!serverQueue.isEmpty() && serverQueue.peek().getRunTick() <= ticks) {
                serverQueue.remove().execute();
            }
            ++ticks;
        }
    }

    @Override
    public void scheduleClientTask(int tickDelay, Runnable task) {
        long runTick = tickDelay + ticks;
        clientQueue.add(new ActionEntry(runTick, task));
    }

    @Override
    public void scheduleServerTask(int tickDelay, Runnable task) {
        long runTick = tickDelay + ticks;
        serverQueue.add(new ActionEntry(runTick, task));
    }

}
