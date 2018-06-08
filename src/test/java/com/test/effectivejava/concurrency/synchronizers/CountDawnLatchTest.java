package com.test.effectivejava.concurrency.synchronizers;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CountDawnLatchTest {

    @Test
    public void countDownLatch() {
        CountDownLatch countDownLatch = new CountDownLatch(3);
        Thread first = new Thread(() -> runTask(4, countDownLatch), "First");
        Thread second = new Thread(() -> runTask(8, countDownLatch), "Second");
        Thread third = new Thread(() -> runTask(12, countDownLatch), "Third");
        first.start();
        second.start();
        third.start();
        try {
            TimeUnit.SECONDS.sleep(2);
            second.interrupt();
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(Thread.currentThread().getName() + " interrupted");
        }
    }

    private void runTask(int seconds, CountDownLatch countDownLatch) {
        System.out.println(Thread.currentThread().getName() + " is doing some work");
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " interrupted");
            countDownLatch.countDown();
            return;
        }
        System.out.println(Thread.currentThread().getName() + " done");
        countDownLatch.countDown();
        try {
            countDownLatch.await();
            System.out.println(Thread.currentThread().getName() + " woke up");
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " interrupted");
        }
    }

    static class Item {
        private Set<Bid> bids;

        public void addBid(Bid bid) {
            bids.add(bid);
        }
    }

    static class Bid {
        private Item item;

        Bid() {
        }

        Bid(Item item) {
           this.item = item;
           item.addBid(this);
        }
    }
}
