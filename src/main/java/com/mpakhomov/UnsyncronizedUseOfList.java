package com.mpakhomov;

import java.util.*;
import java.util.concurrent.*;

/**
 * Create a list and write there by multiple threads simultaneously.
 * {@link java.util.List#set(int, Object)} doesn't produce any errors and to me it's a surprising result.
 * Also, I expected that both {@link java.util.List#add(Object)} and {@link java.util.List#remove(int)}
 * should throw {@link ConcurrentModificationException}, but I got only {@link ArrayIndexOutOfBoundsException}.
 * <p>
 * But, finally, I managed to get {@link ConcurrentModificationException}
 *
 * @author mpakhomov
 * @since 8/27/15
 */
public class UnsyncronizedUseOfList {
    private final static int LIST_INITIAL_SIZE = 8;
    private static final List<Integer> list = new ArrayList<>(2048);

    public static void main(String[] args) {
        for (int i = 0; i < LIST_INITIAL_SIZE; i++) {
            list.add(i);
        }

        final CountDownLatch allTasksStarted = new CountDownLatch(LIST_INITIAL_SIZE);

        Runnable task = () -> {
            allTasksStarted.countDown();
            // start all tasks at the same time
            try { allTasksStarted.await(); } catch (InterruptedException e) {}
            System.out.println("Thread " + Thread.currentThread() + " started");

            while (!Thread.currentThread().isInterrupted()) {
                try { Thread.sleep(ThreadLocalRandom.current().nextInt(100));
                } catch (InterruptedException e) {
                    // restore Interrupted status
                    Thread.currentThread().interrupt();
                }
                int randomIndex = ThreadLocalRandom.current().nextInt(list.size());
//                list.set(randomIndex, randomIndex);
                list.remove(randomIndex);
                list.add(42);
                boolean deadCodeElimination = false;
                for (Integer i : list) {
                    deadCodeElimination = !deadCodeElimination;
                }
            }
            System.out.println("Thread: " + Thread.currentThread() + " was interrupted");
        };

        ExecutorService pool = Executors.newFixedThreadPool(LIST_INITIAL_SIZE);
        for (int i = 0; i < LIST_INITIAL_SIZE; i++) {
            pool.execute(task);
        }

        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException ignored) {}

        pool.shutdownNow();
        try {
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ignored) {}
    }
}
