package com.mpakhomov.pingpong.blockingqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author mpakhomov
 * @since 8/4/2015
 */
public class Pong extends Thread {

    private final BlockingQueue<Integer> queue;

    public Pong(BlockingQueue<Integer> queue) {
        super();
        this.setName("Pong-Thread");
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            // non-empty queue means that now it's a Pong's turn
            if (!queue.isEmpty()) {
                System.out.println(this + " : pong");

                try {
                    TimeUnit.SECONDS.sleep(1);
                    // pass the turn to the other thread
                    queue.take();
                } catch (InterruptedException swallow) {
                } // silently ignore for the sake of simplicity

            } // if
        } // while
    }

}
