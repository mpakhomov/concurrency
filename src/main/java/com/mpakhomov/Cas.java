package com.mpakhomov;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author mpakhomov
 * @since 7/8/14
 */
public class Cas {
    AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) {
        Cas cas = new Cas();
        AtomicInteger counter =  cas.counter;
        counter.compareAndSet(1, 2);
        System.out.println(counter);
    }
}
