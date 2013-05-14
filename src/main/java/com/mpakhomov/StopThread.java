package com.mpakhomov;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: mpakhomo
 * Date: 4/9/13
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class StopThread {

    public static void main(String[] args) throws Exception {
        Thread thread = new Thread(new Runnable() {

            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        System.out.println("interrupt flag = " + Thread.currentThread().isInterrupted());
                        TimeUnit.SECONDS.sleep(5);
                        System.out.println("After sleeping for 5 sec");
                    }
                    System.out.println("Exiting. Interrupt flag = " + Thread.currentThread().isInterrupted());
                } catch (InterruptedException e) {
                    System.out.println("The thread caught an InterruptedException: ");
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                    System.out.println("Exiting. Inside the catch block: check & clear the interrupt flag = " +
                            Thread.interrupted());
                    System.out.println("Exiting. Inside the catch block: Interrupt flag = " +
                            Thread.currentThread().isInterrupted());
                }
            }
        });
        thread.start();
        System.out.println("Press any key to quit");
        System.in.read();
        thread.interrupt();
    }
}
