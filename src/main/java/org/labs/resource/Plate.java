package org.labs.resource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Plate {
    private static final Logger log = LoggerFactory.getLogger(Plate.class);
    private static final long DEFAULT_TIMEOUT_MILLIS = 1000;

    private final int programmerId;
    private final int previouslyEatenSoupPortions;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private boolean isEmpty = true;

    public Plate(int programmerId, int previouslyEatenSoupPortions) {
        this.programmerId = programmerId;
        this.previouslyEatenSoupPortions = previouslyEatenSoupPortions;
    }

    public int getProgrammerId() {
        return programmerId;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public int previouslyEatenSoupPortions() {
        return previouslyEatenSoupPortions;
    }

    public void askToRefill() throws InterruptedException {
        while (!countDownLatch.await(DEFAULT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
            log.debug("[Plate] [programmerId={}] Plate is still empty, keep waiting...", programmerId);
        }
    }

    public void refill() {
        isEmpty = false;
        countDownLatch.countDown();
    }

    public void refuseToRefill() {
        isEmpty = true;
        countDownLatch.countDown();
    }
}
