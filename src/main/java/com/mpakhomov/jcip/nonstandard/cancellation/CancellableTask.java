package com.mpakhomov.jcip.nonstandard.cancellation;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

/**
 * @author mpakhomov
 * @since 8/13/2015
 */
public interface CancellableTask<T> extends Callable<T> {
    void cancel();
    RunnableFuture<T> newTask();
}
