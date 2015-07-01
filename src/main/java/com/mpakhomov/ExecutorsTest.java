package com.mpakhomov;

/**
 * Created with IntelliJ IDEA.
 * User: mpakhomo
 * Date: 4/9/13
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/** Execute a Runnable in the current thread. */
class CurrentThreadExecutor implements Executor {
    public void execute(Runnable r) { r.run(); }
}
/** Execute each Runnable using a newly created thread */
class NewThreadExecutor implements Executor {
    public void execute(Runnable r) { new Thread(r).start(); }
}

/**
 * Queue up the Runnables and execute them in order using a single thread
 * created for that purpose.
 */
class SingleThreadExecutor extends Thread implements Executor {
    BlockingQueue<Runnable> q = new LinkedBlockingQueue<Runnable>();

    public void execute(Runnable r) {
        // Don't execute the Runnable here; just put it on the queue.
        // Our queue is effectively unbounded, so this should never block.
        // Since it never blocks, it should never throw InterruptedException.
        try { q.put(r); }
        catch(InterruptedException never) { throw new AssertionError(never); }
    }

    // This is the body of the thread that actually executes the Runnables
    public void run() {
        for(;;) { // Loop forever
            try {
                Runnable r = q.take(); // Get next Runnable, or wait
                System.out.println("SingleThreadExecutor.run: about to run a task: ");
                r.run(); // Run it!
            }
            catch(InterruptedException e) {
                // If interrupted, stop executing queued Runnables.
                return;
            }
        }
    }
}

public class ExecutorsTest {

    private final String closure = "closure";

    public void doIt() {
        SingleThreadExecutor executorService = new SingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                System.out.println("Asynchronous task 1");
            }
        });

        executorService.execute(new Runnable() {
            public void run() {
                System.out.println("Asynchronous task 2");
            }
        });

        executorService.execute(new Runnable() {
            public void run() {
                System.out.println("Asynchronous task 3");
                System.out.println("Outside world: " + closure);
            }
        });
        System.out.println("main: about to run all tasks");
        //executorService.start();
        executorService.run();
    }

    public static void main(String[] args) {
        new ExecutorsTest().doIt();
    }
}
