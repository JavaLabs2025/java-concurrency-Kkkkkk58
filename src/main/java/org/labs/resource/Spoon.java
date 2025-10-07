package org.labs.resource;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Spoon {
    private final int id;
    private final Lock lock = new ReentrantLock();

    public Spoon(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void acquire() {
        lock.lock();
    }

    public void release() {
        lock.unlock();
    }
}
