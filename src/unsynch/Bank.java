package unsynch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipFile;

public class Bank {
    private final double[] accounts;
    private Lock bankLock;
    private Condition sufficientFunds;

    public Bank(int n, double initialBalance) {
        accounts = new double[n];
        Arrays.fill(accounts, initialBalance);
        bankLock = new ReentrantLock();
        sufficientFunds = bankLock.newCondition();
    }

    public synchronized void transfer(int from, int to, double amount) {
        try {
            while (accounts[from] < amount) {
                wait();
            }
            System.out.print(Thread.currentThread());
            accounts[from] -= amount;
            System.out.printf(" %10.2f from %d to %d", amount, from, to);
            accounts[to] += amount;
            System.out.printf(" Total Balance: %10.2f%n", getTotalBalance());
            notifyAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public double getTotalBalance() {
        bankLock.lock();
        try {
            double total = 0;
            for (double a : accounts) {
                total += a;
            }
            return total;
        } finally {
            bankLock.unlock();
        }
    }

    public int size() {
        return accounts.length;
    }

}
