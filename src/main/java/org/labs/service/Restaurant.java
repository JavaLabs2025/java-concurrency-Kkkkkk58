package org.labs.service;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.Nullable;
import org.labs.resource.Plate;

public class Restaurant implements FoodProvider, CustomerProvider {
    private final AtomicInteger remainingSoupPortions;
    private final PriorityBlockingQueue<Plate> platesToRefill;

    public Restaurant(int totalSoupPortions, int clientsCapacity) {
        if (totalSoupPortions <= 0) {
            throw new IllegalArgumentException("Total soup portions in restaurant must be greater than 0");
        }

        this.remainingSoupPortions = new AtomicInteger(totalSoupPortions);
        this.platesToRefill = new PriorityBlockingQueue<>(
                clientsCapacity,
                Comparator.comparingInt(Plate::previouslyEatenSoupPortions)
        );
    }

    public int getRemainingSoupPortions() {
        return remainingSoupPortions.get();
    }

    @Override
    public boolean isServing() {
        return remainingSoupPortions.get() > 0;
    }

    @Override
    public void refillPlate(Plate plate) {
        platesToRefill.add(plate);
    }

    public int getPlatesToRefillCount() {
        return platesToRefill.size();
    }

    @Override
    public boolean hasPlatesToRefill() {
        return platesToRefill.peek() != null;
    }

    @Override
    @Nullable
    public Plate findPlateToRefill() {
        return platesToRefill.poll();
    }

    @Override
    public boolean getSoup() {
        return remainingSoupPortions.getAndUpdate(v -> v > 0 ? v - 1 : 0) > 0;
    }
}
