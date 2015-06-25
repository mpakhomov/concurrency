package com.mpakhomov.jcip;

import java.util.Random;

/**
 * @author mpakhomov
 * @since: 6/25/2015
 */
public class DemonstrateDeadlock {

    private static final int NUM_THREADS = 20;
    private static final int NUM_ACCOUNTS = 5;
    private static final int NUM_ITERATIONS = 1000000;

    public static class InsufficientFundsException extends Exception {
        public InsufficientFundsException() {}
        public InsufficientFundsException(String msg) {
            super(msg);
        }
    }

    public static class DollarAmount implements Comparable<DollarAmount> {
        public final int amount;

        public DollarAmount(int amount) {
            this.amount = amount;
        }

        @Override
        public int compareTo(DollarAmount o) {
            return Integer.compare(this.amount, o.amount);
        }
    }

    public static class Account implements Comparable<Account> {
        private int balance;

        public int debit(DollarAmount amount) {
            balance += amount.amount;
            System.out.println("debit amount " + amount.amount);
            return balance;
        }

        public int credit(DollarAmount amount) {
            balance -= amount.amount;
            System.out.println("credit amount " + amount.amount);
            return balance;
        }

        public DollarAmount getBalance() {
            return new DollarAmount(balance);
        }

        @Override
        public int compareTo(Account other) {
            return new Integer(hashCode()).compareTo(other.hashCode());
        }
    }



    static public void transferMoney(Account fromAccount,
                              Account toAccount,
                              DollarAmount amount)
            throws InsufficientFundsException {
        System.out.println("Thread " +  Thread.currentThread() + " about to acquire a lock on account "
                + fromAccount.hashCode());
        synchronized (fromAccount) {
            System.out.println("Thread " +  Thread.currentThread() + " acquired a lock on account "
                    + fromAccount.hashCode());
            System.out.println("Thread " + Thread.currentThread() + "about to acquire a lock on account "
                    + toAccount.hashCode());
            synchronized (toAccount) {
                System.out.println("Thread " +  Thread.currentThread() + " acquired a lock on account "
                        + toAccount.hashCode());
//                if (fromAccount.getBalance().compareTo(amount) < 0)
//                    throw new InsufficientFundsException();
//                else {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
//                }
            }
        }
    }

    public static void main(String[] args) {
        final Random rnd = new Random();
        final Account[] accounts = new Account[NUM_ACCOUNTS];
        for (int i = 0; i < accounts.length; i++)
            accounts[i] = new Account();
        class TransferThread extends Thread {
            public void run() {
                for (int i = 0; i < NUM_ITERATIONS; i++) {
                    int fromAcct = rnd.nextInt(NUM_ACCOUNTS);
                    int toAcct = rnd.nextInt(NUM_ACCOUNTS);
                    DollarAmount amount =
                            new DollarAmount(rnd.nextInt(1000));
                    try {
                        transferMoney(accounts[fromAcct],
                                accounts[toAcct], amount);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        for (int i = 0; i < NUM_THREADS; i++)
            new TransferThread().start();
    }
}
