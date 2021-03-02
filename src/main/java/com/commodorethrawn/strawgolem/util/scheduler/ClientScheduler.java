package com.commodorethrawn.strawgolem.util.scheduler;

import net.minecraft.client.world.ClientWorld;

import java.util.PriorityQueue;

public class ClientScheduler {

    public static final ClientScheduler INSTANCE = new ClientScheduler();
    private final PriorityQueue<ActionEntry> queue = new PriorityQueue<>();
    private long ticks = 0;

    public void tick(ClientWorld clientWorld) {
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
