package com.test.effectivejava.concurrency.synchronizers;

import org.junit.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreTest {

    @Test
    public void semaphoreTest() throws InterruptedException {
        TargetObject target = new TargetObject(2);
        Thread first = new Thread(() -> target.doIt(4), "First");
        Thread second = new Thread(() -> target.doIt(8), "Second");
        Thread third = new Thread(() -> target.doIt(4), "Third");
        first.start();
        second.start();
        third.start();
        first.join();
        second.join();
        third.join();
    }

    public static class TargetObject {
        private final Semaphore semaphore;

        public TargetObject(int permits) {
            this.semaphore = new Semaphore(permits);
        }

        public void doIt(int seconds) {
            try {
                semaphore.acquire();
                System.out.println(Thread.currentThread().getName() + " doing work");
                TimeUnit.SECONDS.sleep(seconds);
                System.out.println(Thread.currentThread().getName() + " done");
                semaphore.release();
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " interrupted");
                semaphore.release();
            }
        }
    }
}
