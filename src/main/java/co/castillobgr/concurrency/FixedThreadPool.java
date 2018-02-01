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
    private List<Thread> pool;

    public FixedThreadPool(int capacity, String name) {
        this.capacity = capacity;
        this.name = name;

        pool = new ArrayList<Thread>();
    }

    public FixedThreadPool(int capacity) {
        this(capacity, generateName());
    }

    // Submit a new task to the pool.
    public boolean submit(Runnable r) {
        if (r == null) throw new IllegalArgumentException("r can't be null");
        int poolSize = pool.size();
        if (poolSize < capacity) {
            Thread t = new Thread(r);
            t.run();
            pool.add(t);
            return true;
        }
        else {
            for (int i = 0; i < poolSize; i++) {
                Thread current = pool.get(i);
                // TODO: Check into Thread#getState()
                if (!current.isAlive()) {
                    Thread t = new Thread(r);
                    t.run();
                    pool.add(i, t);
                    return true;
                }
            }
            return false;
        }
    }
}
