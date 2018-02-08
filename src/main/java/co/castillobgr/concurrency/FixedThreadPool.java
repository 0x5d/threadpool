package co.castillobgr.concurrency;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FixedThreadPool {

    // Convenience function to generate a pool name.
    private static String generateName() {
        return "pool-" + UUID.randomUUID();
    }

    // The pool's capacity.
    private int capacity;
    // The pool's name.
    private String name;
    // Maximum amount of time a thread can be idle.
    private long maxIdleTime;
    // Queue of pending tasks.
    private BlockingQueue<Runnable> backlog;
    // Maximum size of pending tasks queue.
    private long maxBacklogSize;
    // A list to keep a reference of threads.
    private Worker[] pool;

    public FixedThreadPool(int capacity, long maxIdleTime, BlockingQueue<Runnable> backlog, String name) {
        if (capacity < 0) throw new IllegalArgumentException("capacity can't be negative");
        if (maxIdleTime < 0) throw new IllegalArgumentException("maxIdleTime can't be negative");
        if (maxBacklogSize < 0) throw new IllegalArgumentException("maxBacklogSize can't be negative");
        this.capacity = capacity;
        this.name = name;
        this.maxIdleTime = maxIdleTime;
        this.maxBacklogSize = maxBacklogSize;
        this.backlog = backlog;
        this.pool = new Worker[capacity];
    }

    public FixedThreadPool(int capacity, BlockingQueue<Runnable> backlog) {
        this(capacity, 0, backlog, generateName());
    }

    // Submit a new task to the pool.
    public boolean submit(Runnable r) {
        if (r == null) throw new IllegalArgumentException("r can't be null");

        for (int i = 0; i < this.capacity; i++) {
            Worker current = this.pool[i];
            // TODO: Check into Thread#getState()
            if (current == null || !current.thread.isAlive()) {
                Worker w = new Worker(r);
                w.thread.start();
                this.pool[i] = w;
                return true;
            }
        }
        // When we get here, it might happen that all Worker threads have stopped, so even though we add r to the
        // backlog, it might not get picked up.
        return backlog.offer(r);
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
            if (pool[i] == null || !pool[i].thread.isAlive()) availability += 1;
        }
        return availability;
    }

    private class Worker implements Runnable {

        private Runnable task;

        private Thread thread;

        Worker(Runnable task) {
            if (task == null) throw new IllegalArgumentException("task can't be null");
            this.task = task;
            // Look into Executors.defaultThreadFactory()
            this.thread = new Thread(this.task);
        }

        public void run() {
            while (this.task != null || (this.task = backlog.poll()) != null) {
                this.task.run();
                this.task = null;
            }
        }
    }
}
