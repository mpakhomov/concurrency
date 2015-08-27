package com.mpakhomov;

import java.util.concurrent.*;

/**
 * Create an array and write there by multiple threads simultaneously.
 * This is just to prove that the code doesn't produce any exceptions.
 * However, without synchronization such access method is completely
 * broken and the result would be rubbish
 *
 * @author mpakhomov
 * @since 8/27/15
 */
public class UnsynchronizedUseOfArray {

    private static final int[] array = new int[8];

    public static void main(String[] args) {
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }

        Runnable task = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                int randomIndex = ThreadLocalRandom.current().nextInt(array.length);
                array[randomIndex] = randomIndex;
            }
            System.out.println("Thread: " + Thread.currentThread() + " was interrupted");
        };

        ExecutorService pool = Executors.newFixedThreadPool(array.length);
        for (int i = 0; i < array.length; i++) {
            pool.execute(task);
        }

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException ignored) {}

        pool.shutdownNow();
        try {
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ignored) {}
    }
}
