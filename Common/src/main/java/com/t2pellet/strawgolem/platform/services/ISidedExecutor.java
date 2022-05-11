package com.t2pellet.strawgolem.platform.services;

public interface ISidedExecutor {

    void scheduleClient(Runnable runnable);

    void scheduleServer(Runnable runnable);

}
