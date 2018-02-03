package co.castillobgr.concurrency;

import java.util.*;

public class FixedThreadPool {

    // Convenience function to generate a pool name.
    private static String generateName() {
        return "pool-" + UUID.randomUUID();
    }

    // The pool's capacity.
    private int capacity;
    // The pool's name.
    private String name;

    // A list to keep a reference of threads.
    private Thread[] pool;

    public FixedThreadPool(int capacity, String name) {
        this.capacity = capacity;
        this.name = name;

        pool = new Thread[capacity];
    }

    public FixedThreadPool(int capacity) {
        this(capacity, generateName());
    }

    // Submit a new task to the pool.
    public boolean submit(Runnable r) {
        if (r == null) throw new IllegalArgumentException("r can't be null");

        for (int i = 0; i < capacity; i++) {
            Thread current = pool[i];
            // TODO: Check into Thread#getState()
            if (current == null || !current.isAlive()) {
                Thread t = new Thread(r);
                t.start();
                pool[i] = t;
                return true;
            }
        }
        return false;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getName() {
        return name;
    }

    public int getAvailability() {
        int availability = 0;
        for (int i = 0; i < capacity; i++) {
            if (pool[i] == null) availability += 1;
        }
        return availability;
    }
}
