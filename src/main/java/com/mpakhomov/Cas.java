package com.mpakhomov;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class illustrates how AtomicInteger is implemented.
 *
 * @author mpakhomov
 * @since 7/8/14
 */
public class Cas {
    // setup to use Unsafe.compareAndSwapInt for updates
    private static final Unsafe unsafe = getUnsafe();
    private static final long valueOffset;

    static {
        try {
            valueOffset = unsafe.objectFieldOffset
                    (AtomicInteger.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    private volatile int value = 0;

    /**
     * Atomically update Java variable to <tt>x</tt> if it is currently
     * holding <tt>expected</tt>.
     * @return <tt>true</tt> if successful
     */
//    public final native boolean compareAndSwapInt(Object o, long offset,
//                                                  int expected,
//                                                  int x);

    /**
     * Atomically increments by one the current value.
     *
     * @return the updated value
     */
    public final int incrementAndGet() {
        int v;
        do {
//            v = unsafe.getIntVolatile(this, valueOffset);
            v = value;
        } while (!unsafe.compareAndSwapInt(this, valueOffset, v, v + 1));
        return v;
    }

    @SuppressWarnings("restriction")
    private static Unsafe getUnsafe() {
        try {

            Field singletoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
            singletoneInstanceField.setAccessible(true);
            return (Unsafe) singletoneInstanceField.get(null);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) {
        Cas cas = new Cas();
        for (int i = 0; i < 10; i++) {
            System.out.println(cas.incrementAndGet());
        }
    }
}
