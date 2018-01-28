package co.castillobgr.concurrency;

import java.util.*;

public class FixedThreadPool {

    private static String generateName() {
        return "pool-" + UUID.randomUUID();
    }

    private int capacity;
    private String name;

    private List<Runnable> pool;

    public FixedThreadPool(int capacity, String name) {
        this.capacity = capacity;
        this.name = name;

        pool = new ArrayList<Runnable>();
    }

    public FixedThreadPool(int capacity) {
        this(capacity, generateName());
    }

    public boolean submit(Runnable r) {
        if (r == null) throw new IllegalArgumentException("r can't be null");
        if (pool.size() < capacity) {
            r.run();
            pool.add(r);
            return true;
        }
        else return false;
    }
}
