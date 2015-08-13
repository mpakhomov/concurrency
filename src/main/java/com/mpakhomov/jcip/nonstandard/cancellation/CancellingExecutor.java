package com.mpakhomov.jcip.nonstandard.cancellation;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.*;

/**
 * @author mpakhomov
 * @since 8/13/2015
 */
@ThreadSafe
public class CancellingExecutor extends ThreadPoolExecutor {

    public CancellingExecutor(int poolSize) {
        super(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS,
              new LinkedBlockingQueue<>());
    }


    protected<T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        if (callable instanceof CancellableTask) {
            return ((CancellableTask<T>) callable).newTask();
        } else {
            return super.newTaskFor(callable);
        }
    }
}
