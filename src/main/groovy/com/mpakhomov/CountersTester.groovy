package com.mpakhomov

import java.util.concurrent.*
import java.util.concurrent.Executors
import java.util.HashMap

def pool = Executors.newFixedThreadPool(3)
def counters = new Counters()

def task1 = {
    println "${Thread.currentThread().getName()} count=${counters.count1()}"
} as Runnable

def task2 = {
    println "${Thread.currentThread().getName()} count=${counters.count2()}"
} as Runnable

def task3 = {
    println "${Thread.currentThread().getName()} count=${counters.count3()}"
} as Runnable

pool.submit(task1)
pool.submit(task2)
pool.submit(task3)

Thread.sleep(3000)
pool.shutdownNow()

println("finished");
