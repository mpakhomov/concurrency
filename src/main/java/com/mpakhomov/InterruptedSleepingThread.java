package com.mpakhomov;

/**
 * @author: mpakhomov
 * created: 6/20/15
 * It's an example that illustrates importance of not swallowing InterruptedException
 */
public class InterruptedSleepingThread extends Thread {

    @Override
    public void run() {
        doAPseudoHeavyWeightJob();
    }

    private void doAPseudoHeavyWeightJob() {
        for (int i=0;i<Integer.MAX_VALUE;i++) {
            //You are kidding me
            System.out.println(i + " " + i*2);
            //Let me sleep <evil grin>
            if(Thread.currentThread().isInterrupted()) {
                System.out.println("Thread interrupted\n Exiting...");
                break;
            }else {
                sleepBabySleep();
            }
        }
    }

    /**
     *
     */
    protected void sleepBabySleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            //e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
