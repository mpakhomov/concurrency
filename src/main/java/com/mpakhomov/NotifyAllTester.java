package com.mpakhomov;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * (1) Ensure that all threads get a notification after NotifyAll invocation
 * (2) Just for fun: instead of submitting 10 tasks with a period 1 sec in for loop using Thread.sleep,
 *     I decided to use ScheduledExecutorService.scheduleAtFixedRate
 * (3) It turns out that there is a nice way to cancel periodic tasks by throwing a RuntimeException
 * @author mpakhomov
 * @since: 7/1/2015
 */
public class NotifyAllTester {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
    private final AtomicInteger numberOfTasks = new AtomicInteger(10);
    private final CountDownLatch allTasksObtainedTheLock = new CountDownLatch(10);
    private final CountDownLatch allTasksCompleted = new CountDownLatch(10);
    private final Object lock = new Object();
    private volatile ScheduledFuture<?> scheduledFuture;

    public static void main(String[] args) throws InterruptedException {
        new NotifyAllTester().run();
    }

    public void run() throws InterruptedException{
        Runnable task = () -> {
            System.out.println(Thread.currentThread() + " is running");
            synchronized (this.lock) {
                System.out.println("Thread " + Thread.currentThread() + " obtained the lock");
                allTasksObtainedTheLock.countDown();
                try {
                    this.lock.wait();
                    System.out.println("Thread " + Thread.currentThread() + " got a notification");
                    // this is to prove that the next thread will be notified only after this thread
                    // exits the critical section (releases the lock)
                    Thread.sleep(1000);
                    allTasksCompleted.countDown();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                System.out.println("Thread " + Thread.currentThread() + " released the lock");
            }
        };

        Runnable schedulerTask = () -> {
            System.out.println("In schedulerTask");
            if (numberOfTasks.decrementAndGet() >= 0) {
                fixedThreadPool.submit(task);
            } else {
                //throw new RuntimeException("Cancel this periodic task");
                this.scheduledFuture.cancel(false);
            }
        };

        scheduledExecutorService.scheduleAtFixedRate(schedulerTask, 1, 1, TimeUnit.SECONDS);


        allTasksObtainedTheLock.await();

        System.out.println("All tasks obtained the lock");

        // wait/notifyAll should be called only from within synchronized section.
        // otherwise java.lang.IllegalMonitorStateException is thrown
//        this.lock.notifyAll();

        synchronized (this.lock) {
            System.out.println("Thread " + Thread.currentThread() + " is about to call NotifyAll on the lock");
            this.lock.notifyAll();
        }

        allTasksCompleted.await();
        System.out.println("All tasks completed");

        closeThreadPool(scheduledExecutorService);
        closeThreadPool(fixedThreadPool);
    }


    private void closeThreadPool(ExecutorService pool) {
        System.out.println("Shutting down thread pool: " + pool);
        pool.shutdown();
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(30, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
