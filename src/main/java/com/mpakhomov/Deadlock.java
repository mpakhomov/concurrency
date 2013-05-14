package com.mpakhomov;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class Deadlock {
    // When two threads try to lock two objects, deadlock can occur unless
    // they always request the locks in the same order.
    final Object resource1 = new Object(); // Here are two objects to lock
    final Object resource2 = new Object();
    private final Random random = new Random(System.currentTimeMillis());

    Runnable r1 = new Runnable() { // Locks resource1 then resource2
        public void run() {
            synchronized(resource1) {
                System.out.println(Thread.currentThread().getName()  +
                        " obtained lock on resource 1");
                int secs = random.nextInt(5);
                System.out.println("Thread " + Thread.currentThread().getId() +
                        " is sleeping for " + secs + " seconds");
                try { TimeUnit.SECONDS.sleep(secs); } catch (Exception ex) {}
                System.out.println(Thread.currentThread().getName()  +
                        " is going to obtain a lock on resource 2");
                synchronized(resource2) {
                    System.out.println(Thread.currentThread().getName()  +
                            " obtained lock on resource 2");
                    compute();
                }
            }
        }
    };
    Runnable r2 = new Runnable() { // Locks resource2 then resource1
        public void run() {
            synchronized(resource2) {
                System.out.println(Thread.currentThread().getName()  +
                        " obtained lock on resource 2");
                int secs = random.nextInt(5);
                System.out.println("Thread " + Thread.currentThread().getId() +
                        " is sleeping for " + secs + " seconds");
                try { TimeUnit.SECONDS.sleep(secs); } catch (Exception ex) {}
                System.out.println(Thread.currentThread().getName()  +
                        " is going to obtain a lock on resource 1");
                synchronized(resource1) {
                    System.out.println(Thread.currentThread().getName()  +
                            " obtained lock on resource 1");
                    compute();
                }
            }
        }
    };

    public void compute() {
        Random random = new Random(System.currentTimeMillis());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception ex) {}
    }

    public static void main(String[] args) {
        Deadlock tester = new Deadlock();
        //ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (;;) {
            //executorService.execute(tester.r1);
            //executorService.execute(tester.r2);
            Thread t1 = new Thread(tester.r1, "Thread1");
            Thread t2 = new Thread(tester.r2, "Thread2");
            t1.start(); // Locks resource1
            t2.start();
            try {
                t1.join();
                t2.join();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            //new Thread(tester.r2).start(); // Locks resource2 and now neither thread can progress!
        }
    }
}
