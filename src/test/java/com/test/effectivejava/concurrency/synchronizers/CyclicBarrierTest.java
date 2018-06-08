package com.test.effectivejava.concurrency.synchronizers;

import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierTest {

    @Test
    public void testCyclicBarrier() {
        CyclicBarrier barrier = new CyclicBarrier(4, () -> System.out.println("All threads finished!"));
        Thread first = new Thread(() -> runTask(4, barrier), "First");
        Thread second = new Thread(() -> runTask(8, barrier), "Second");
        Thread third = new Thread(() -> runTask(12, barrier), "Third");
        first.start();
        second.start();
        third.start();
        try {
            barrier.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(Thread.currentThread().getName() + " interrupted");
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    private void runTask(int seconds, CyclicBarrier barrier) {
        System.out.println(Thread.currentThread().getName() + " is doing some work");
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " interrupted");
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e1) {
                e1.printStackTrace();
                return;
            }
            return;
        }
        System.out.println(Thread.currentThread().getName() + " done");
        try {
            barrier.await();
            System.out.println(Thread.currentThread().getName() + " woke up");
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " interrupted");
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
