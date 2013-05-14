package com.mpakhomov;

import java.util.concurrent.atomic.AtomicInteger;

// The count1(), count2() and count3() methods are all threadsafe.  Two
// threads can call these methods at the same time, and they will never
// see the same return value.
public class Counters {
    // A counter using a synchronized method and locking
    int count1 = 0;
    public synchronized int count1() { return ++count1; }

    // A counter using an atomic increment on an AtomicInteger
    AtomicInteger count2 = new AtomicInteger(0);
    public int count2() { return count2.incrementAndGet(); }

    // An optimistic counter using compareAndSet()
    AtomicInteger count3 = new AtomicInteger(0);
    public int count3() {
        // Get the counter value with get() and set it with compareAndSet().
        // If compareAndSet() returns false, try again until we get 
        // through the loop without interference.
        int result;
        do {
            result = count3.get();
        } while(!count3.compareAndSet(result, result+1));
        return count3.get();
    }
}
