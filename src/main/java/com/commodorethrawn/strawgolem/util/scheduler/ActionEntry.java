package com.commodorethrawn.strawgolem.util.scheduler;

import org.jetbrains.annotations.NotNull;

class ActionEntry implements Comparable<ActionEntry> {

    private final long runTick;
    private final Runnable function;

    ActionEntry(long runTick, Runnable function) {
        this.runTick = runTick;
        this.function = function;
    }

    @Override
    public int compareTo(@NotNull ActionEntry o) {
        return Long.compare(runTick, o.runTick);
    }

    long getRunTick() {
        return runTick;
    }

    void execute() {
        function.run();
    }
}
