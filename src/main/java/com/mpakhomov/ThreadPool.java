package com.mpakhomov;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

// Implementation is a piece of shit

/**
 * Created with IntelliJ IDEA.
 * User: mpakhomo
 * Date: 5/6/13
 * Time: 1:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThreadPool {

    private BlockingQueue taskQueue = null;
    private List<PoolThread> threads = new ArrayList<PoolThread>();
    private boolean isStopped = false;

    public ThreadPool(int noOfThreads, int maxNoOfTasks){
        taskQueue = new ArrayBlockingQueue(maxNoOfTasks);

        for(int i=0; i < noOfThreads; i++){
            threads.add(new PoolThread(taskQueue));
        }
        for(PoolThread thread : threads){
            thread.start();
        }
    }

    synchronized public void  execute(Runnable task){
        if(this.isStopped) throw
                new IllegalStateException("ThreadPool is stopped");

        try {
            this.taskQueue.put(task);
        } catch (InterruptedException ex) {}
    }

    public synchronized void stop(){
        this.isStopped = true;
        for(PoolThread thread : threads){
            thread.stop();
        }
    }

    class PoolThread extends Thread {

        private BlockingQueue taskQueue = null;
        private boolean       isStopped = false;

        public PoolThread(BlockingQueue queue){
            taskQueue = queue;
        }

        public void run(){
            while(!isStopped){
                try{
                    Runnable runnable = (Runnable) taskQueue.take();
                    runnable.run();
                } catch(Exception e){
                    //log or otherwise report exception,
                    //but keep pool thread alive.
                }
            }
        }

        public synchronized void stopThread(){
            isStopped = true;
            this.interrupt(); //break pool thread out of dequeue() call.
        }

        synchronized public boolean isStopped(){
            return isStopped;
        }
    }

}


