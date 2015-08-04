package com.mpakhomov.pingpong.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author mpakhomov
 * @since 8/4/2015
 */
public class PingPongGame {

    public static void main(String[] args) {
        final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1);
        new Ping(queue).start();
        new Pong(queue).start();
    }
}
