package com.mpakhomov

import java.util.concurrent.*;
import java.util.concurrent.Executors;

def pool = Executors.newFixedThreadPool(2)

int taskCount = 50;
def countDownLatch = new CountDownLatch(taskCount);

taskCount.times {n ->
    def action = {
        def x = 0;
        sleep(100);
        x++;
        //println "${n} ${x}";
        countDownLatch.countDown()
        println "${n} ${x} countDownLatch.count=${countDownLatch.count}"
    } as Runnable;
    pool.submit(action);
}

countDownLatch.await()

println("finished");
