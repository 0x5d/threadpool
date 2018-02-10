package co.castillobgr.concurrency;

import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FixedThreadPoolTest {

    @Test
    public void generateNameTest() {
        FixedThreadPool ftp = new FixedThreadPool(1, new LinkedBlockingQueue<Runnable>(1));
        assertTrue(ftp.getName().startsWith("pool-"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void submitNullRunnableTest() {
        FixedThreadPool pool = new FixedThreadPool(1, new LinkedBlockingQueue<Runnable>(1));

        pool.submit(null);
    }

    @Test
    public void submitWithAvailabilityTest() {
        Runnable r = new Runnable() {
            public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            }
        };

        FixedThreadPool pool = new FixedThreadPool(2, new LinkedBlockingQueue<Runnable>(1));
        pool.submit(r);
        assertEquals(1, pool.getAvailability());
    }

    @Test
    public void submitWithNoAvailabilityTest() {
        FixedThreadPool pool = new FixedThreadPool(5, new LinkedBlockingQueue<Runnable>(1));
        int n = 0;
        for (; n < 10; n++) {
            Runnable r = new Runnable() {
                public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                }
            };
            System.out.println(pool.getAvailability());
            if (!pool.submit(r)) break;
        }
        // 5 total capacity + backlog capacity of 1
        assertEquals(6, n);
    }
}
