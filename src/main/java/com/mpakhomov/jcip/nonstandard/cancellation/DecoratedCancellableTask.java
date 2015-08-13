package com.mpakhomov.jcip.nonstandard.cancellation;

import java.util.concurrent.*;

/**
 * @author mpakhomov
 * @since 8/13/2015
 */
public abstract class DecoratedCancellableTask<T> implements CancellableTask<T> {
    @Override
    synchronized public void cancel() {
        System.out.println("Task-specific cancellation code");

    }

    @Override
    synchronized public RunnableFuture<T> newTask() {
        return new FutureTask<T>(this) {
            public boolean cancel(boolean mayInterruptIfRunning) {
                try {
                    DecoratedCancellableTask.this.cancel();
                } finally {
                    return super.cancel(mayInterruptIfRunning);
                }
            }
        };
    }

    public static void main(String[] args) {
        final int NTHREADS = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor exec = new CancellingExecutor(NTHREADS);

        DecoratedCancellableTask<Integer> task = new DecoratedCancellableTask<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println("inside DecoratedCancellableTask");
                try {
                    TimeUnit.SECONDS.sleep(60);
                } catch (InterruptedException e) {
                    System.out.println("Caught InterruptedException. Exiting.");
                }
                return Integer.valueOf(42);
            }
        };

        Future<Integer> result = exec.submit(task);
    }
}
