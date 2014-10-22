package com.mpakhomov.concurrency.tutorial;

/**
 * @author mpakhomov
 */
public class Deadlock {

    static class Friend {
        private final String name;
        public Friend(String name) {
            this.name = name;
        }
        public String getName() {
            return this.name;
        }
        public synchronized void bow(Friend bower) {
            System.out.format("%s: %s"
                            + "  has bowed to me!%n",
                    this.name, bower.getName());
            bower.bowBack(this);
        }
        public synchronized void bowBack(Friend bower) {
            System.out.format("%s: %s"
                            + " has bowed back to me!%n",
                    this.name, bower.getName());
        }
    }

    public static void main(String[] args) {
        final Friend alphonse =
                new Friend("Alphonse");
        final Friend gaston =
                new Friend("Gaston");

        new Thread(new Runnable() {
            public void run() { alphonse.bow(gaston); }
        }).start();

        // this fixes a deadlock (gaston doesn't acquire a lock, therefore alphonse successfully acquires a lock on gaston object)
//        try {
//            Thread.sleep(5000);
//        } catch (Exception e) {}

        new Thread(new Runnable() {
            public void run() { gaston.bow(alphonse); }
        }).start();
        System.out.println("Exiting");
    }
}
