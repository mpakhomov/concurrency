package com.mpakhomov;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.Executors;

/**
 * A queue. One thread calls push() to put an object on the queue.
 * Another calls pop() to get an object off the queue. If there is no
 * data, pop() waits until there is some, using wait()/notify().
 * wait() and notify() must be used within a synchronized method or
 * block. In Java 5.0, use a java.util.concurrent.BlockingQueue instead.
 */
public class WaitingQueue<E> {
    LinkedList<E> q = new LinkedList<E>();  // Where objects are stored

    public synchronized void push(E o) {
        q.add(o);         // Append the object to the end of the list
        this.notifyAll(); // Tell waiting threads that data is ready
    }

    public synchronized E pop() {
        while(q.size() == 0) {
            try { this.wait(); }
            catch (InterruptedException ignore) {}
        }
        return q.remove(0);
    }

    private void logMessage(String msg) {
        System.out.println("Thread: " + Thread.currentThread().getName() + " : "  + msg);
    }


    interface QueueOperations {
        void doSomethingWithTheQueue();
    }


    class Task extends Thread {
        QueueOperations actor;
        String name;

        Task(String name, QueueOperations actor) {
            super(name);
            this.name = name;
            this.actor = actor;
        }

        @Override
        public void run() {
            Thread.currentThread().setName(name);
            while (true) {
                try {
                    actor.doSomethingWithTheQueue();
                    logMessage("Interrupt flag = " + Thread.currentThread().isInterrupted());
                    if (Thread.currentThread().isInterrupted()) {
                        logMessage("was interrupted");
                        break;
                    }
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    logMessage("caught InterruptedException. Exiting");
                    break;
                }
            }
        }

    }

    private void run() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        final WaitingQueue<Integer> queue = new WaitingQueue<Integer>();

        executor.execute(new Task("ConsumerThread", new QueueOperations() {
            @Override
            public void doSomethingWithTheQueue() {
                WaitingQueue.this.logMessage("Blocked until there is some data in the queue");
                WaitingQueue.this.logMessage("Retrieved: " + queue.pop());
            }
        }));

        executor.execute(new Task("ProducerThread", new QueueOperations() {
            @Override
            public void doSomethingWithTheQueue() {
                WaitingQueue.this.logMessage("pushing a value to the queue");
                queue.push(2);
            }
        }));

        //executor.shutdown();
        logMessage("about to run executor.shutdownNow");
        executor.shutdownNow();
        try {
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                System.out.println("Timeout. Calling System.exit(0)...");
                System.exit(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Exiting normally...");
    }

    public static void main(String[] args) {
        WaitingQueue<Integer> tester = new WaitingQueue<Integer>();
        tester.run();
    }
}

