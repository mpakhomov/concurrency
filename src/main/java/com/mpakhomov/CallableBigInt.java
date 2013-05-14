package com.mpakhomov;

import java.util.concurrent.*;
import java.util.concurrent.Executors;
import java.math.BigInteger;
import java.util.Random;
import java.security.SecureRandom;

/** This is a Callable implementation for computing big primes. */
class RandomPrimeSearch implements Callable<BigInteger> {
    static Random prng = new SecureRandom(); // self-seeding
    int n;
    public RandomPrimeSearch(int bitsize) { n = bitsize; }
    public BigInteger call() { return BigInteger.probablePrime(n, prng); }
}

public class CallableBigInt {

    public void start() {
        // Try to compute two primes at the same time
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        Future<BigInteger> p = threadPool.submit(new RandomPrimeSearch(512));
        Future<BigInteger> q = threadPool.submit(new RandomPrimeSearch(512));
        BigInteger r1 = null;
        BigInteger r2 = null;
        try {
            r1 = p.get();
            r2 = q.get();
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("r1 = " + r1.toString());
        System.out.println("r2 = " + r2.toString());
        BigInteger product = r1.multiply(r2);
        System.out.println("product = " + product.toString());

    }

    public static void main(String[] args) {
        new CallableBigInt().start();
    }

}
