package com.mpakhomov;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Executors;

public class BoundedBuffer {
    private final String[] buffer;
    private final int capacity;

    private volatile int front;
    private volatile int rear;
    private volatile int count;

    private final Lock lock = new ReentrantLock();

    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BoundedBuffer(int capacity) {
        //super();
        this.capacity = capacity;
        buffer = new String[capacity];
    }

    public void deposit(String data) throws InterruptedException {
        lock.lock();

        try {
            while (count == capacity) {
                notFull.await();
            }

            buffer[rear] = data;
            rear = (rear + 1) % capacity;
            count++;

            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public String fetch() throws InterruptedException {
        lock.lock();

        try {
            while (count == 0) {
                notEmpty.await();
            }

            String result = buffer[front];
            front = (front + 1) % capacity;
            count--;

            notFull.signal();

            return result;
        } finally {
            lock.unlock();
        }
    }

    static public void main(String[] args) throws InterruptedException {
        final BoundedBuffer buf = new BoundedBuffer(10);
        final Random random = new Random(System.currentTimeMillis());
        final String[] strings = new String[] {
                "openjdk-7-fcs-src-b147-27_jun_2011.zip",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                "Sed a leo vitae dui sagittis sollicitudin.",
                "Suspendisse et enim nibh. Maecenas adipiscing enim a metus",
                "aliquam eu vehicula magna varius. Suspendisse imperdiet posuere",
                "posuere. Donec cursus lacus non sapien molestie facilisis.",
                "Aliquam et erat sapien, ac imperdiet sem.",
                "Maecenas eget mauris a mi fringilla molestie."
        };

        Runnable producerTask = new Runnable() {
            @Override
            public void run() {
                final String str = strings[random.nextInt(strings.length - 1)];
                System.out.println("Producer " + Thread.currentThread().getName() + " : " +
                    "putting a random string: " + str);
                try {
                    buf.deposit(str);
                } catch(InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        };

        Runnable consumerTask = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Consumer " + Thread.currentThread().getName() + " : " +
                        "fetching a string: " + buf.fetch());
                } catch(InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(strings.length * 2);
        for (String s : strings) {
            executor.execute(producerTask);
        }
        for (int i = 0; i < strings.length; i++) {
            executor.execute(consumerTask);
        }
        TimeUnit.SECONDS.sleep(10);
        executor.shutdownNow();
    }
}