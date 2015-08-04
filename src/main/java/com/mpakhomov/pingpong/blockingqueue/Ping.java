package com.mpakhomov.pingpong.blockingqueue;

import com.mpakhomov.ExecutorsTest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author mpakhomov
 * @since 8/4/2015
 */
public class Ping extends Thread {

    private final BlockingQueue<Integer> queue;

    public Ping(BlockingQueue<Integer> queue) {
        super();
        this.setName("Ping-Thread");
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            // empty queue means that now it's a Ping's turn
            if (queue.isEmpty()) {
                System.out.println(this + " : ping");

                try {
                    TimeUnit.SECONDS.sleep(1);
                    // pass the turn to the other thread
                    queue.put(1);
                } catch (InterruptedException swallow) {} // silently ignore for the sake of simplicity

            } // if
        } // while (true) {
    }

}
