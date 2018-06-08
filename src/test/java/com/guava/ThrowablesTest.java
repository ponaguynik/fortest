package com.guava;

import com.google.common.base.Throwables;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThrowablesTest {

    @Test(expected = ArithmeticException.class)
    public void testThrowables() {
        try {
            int a = 1 / 0;
        } catch (Exception e) {
            Throwables.throwIfInstanceOf(e, ArithmeticException.class);
        }
    }


    public Runnable dotIt(MyRunnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    @FunctionalInterface
    private interface MyRunnable {
        void run() throws Exception;
    }
}
