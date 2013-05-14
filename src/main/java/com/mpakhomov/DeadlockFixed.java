package com.mpakhomov;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockFixed {
    // When two threads try to lock two objects, deadlock can occur unless
    // they always request the locks in the same order.
    final Lock lock1 = new ReentrantLock(); // Here are two objects to lock
    final Lock lock2 = new ReentrantLock();
    private final Random random = new Random(System.currentTimeMillis());

    Runnable r1 = new Runnable() { // Locks resource1 then resource2
        public void run() {
            try {
                lock1.tryLock(1, TimeUnit.SECONDS);
                System.out.println(Thread.currentThread().getName()  +
                        " obtained lock1");
                int ms = random.nextInt(1000);
                System.out.println("Thread " + Thread.currentThread().getName() +
                        " is sleeping for " + ms + " seconds");
                Thread.sleep(ms);
                lock2.tryLock(1, TimeUnit.SECONDS);
                System.out.println(Thread.currentThread().getName()  +
                        " obtained lock2");
                compute();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } finally {
                System.out.println(Thread.currentThread().getName()  +
                        " is releasing locks");
                lock2.unlock();
                lock1.unlock();
            }
        }
    };

    Runnable r2 = new Runnable() { // Locks resource2 then resource1
        public void run() {
            try {
                lock2.tryLock(1, TimeUnit.SECONDS);
                System.out.println(Thread.currentThread().getName()  +
                        " obtained lock2");
                int ms = random.nextInt(1000);
                System.out.println("Thread " + Thread.currentThread().getName() +
                        " is sleeping for " + ms + " seconds");
                Thread.sleep(ms);
                lock1.tryLock(1, TimeUnit.SECONDS);
                System.out.println(Thread.currentThread().getName()  +
                        " obtained lock1");
                compute();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } finally {
                System.out.println(Thread.currentThread().getName()  +
                        " is releasing locks");
                lock1.unlock();
                lock2.unlock();
            }
        }
    };

    public void compute() {
        Random random = new Random(System.currentTimeMillis());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception ex) {}
    }

    public static void main(String[] args) {
        DeadlockFixed tester = new DeadlockFixed();
        //ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (;;) {
            //executorService.execute(tester.r1);
            //executorService.execute(tester.r2);
            Thread t1 = new Thread(tester.r1, "Thread1");
            Thread t2 = new Thread(tester.r2, "Thread2");
            t1.start(); // Locks resource1 and then resource2
            t2.start(); // Locks resource2 and then resource1
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
