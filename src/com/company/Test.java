package com.company;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Test {
    private static final List<Integer> randoms = new ArrayList<>();
    private static final Lock randomsLock = new ReentrantLock();
    private static final Condition randomsIsEmpty = randomsLock.newCondition();

    private static final Thread writer = new Thread(() -> {
        while (true) {
            randomsLock.lock();
            try {
                while (!randoms.isEmpty()) {
                    randomsIsEmpty.await();
                }
                System.out.println("\nПоток writer");
                int length = (int) (Math.random() * 50 + 10);
                for (int i = 0; i < length; i++) {
                    randoms.add((int) (Math.random() * 1000));
                }
                randomsIsEmpty.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                randomsLock.unlock();
            }
        }
    });

    private static final Thread reader = new Thread(() -> {
        while (true) {
            randomsLock.lock();
            try {
                while (randoms.isEmpty()) {
                    randomsIsEmpty.await();
                }
                System.out.println("Поток reader");
                System.out.println(randoms);
                randoms.clear();
                randomsIsEmpty.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                randomsLock.unlock();
            }
        }
    });

    public static void main(String[] args) throws Exception {
        reader.setDaemon(true);
        writer.setDaemon(true);
        writer.start();
        reader.start();
        Thread.sleep(5000);
    }
}
