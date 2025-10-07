package org.labs.utils;

import java.time.Duration;

public class TimeWatch {
    private long startTime;

    public TimeWatch() { }

    public void start() {
        startTime = System.nanoTime();
    }

    public Duration tick() {
        return Duration.ofNanos(System.nanoTime() - startTime);
    }
}
