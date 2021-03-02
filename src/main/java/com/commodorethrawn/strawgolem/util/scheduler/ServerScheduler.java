package com.commodorethrawn.strawgolem.util.scheduler;

import net.minecraft.server.world.ServerWorld;

import java.util.PriorityQueue;

public class ServerScheduler {

    public static final ServerScheduler INSTANCE = new ServerScheduler();
    private final PriorityQueue<ActionEntry> queue = new PriorityQueue<>();
    private long ticks = 0;

    public void tick(ServerWorld serverWorld) {
        while (!queue.isEmpty() && queue.peek().getRunTick() <= ticks) {
            queue.remove().execute();
        }
        ++ticks;
    }

    public void scheduleTask(int tickDelay, Runnable function) {
        long runTick = tickDelay + ticks;
        queue.add(new ActionEntry(runTick, function));
        System.out.println("Scheduled task in: " + tickDelay + " ticks");
    }

}
