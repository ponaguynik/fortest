package com.test.effectivejava.enums;

import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CarTest {
    private Set<Car> cars;

    @Before
    public void init() {
        Car mRedCar = new Car("M", Car.Color.RED);
        Car fRedCar = new Car("F", Car.Color.RED);
        Car gGreenCar = new Car("G", Car.Color.GREEN);
        Car bBlueCar = new Car("B", Car.Color.BLUE);
        this.cars = new HashSet<>(Arrays.asList(mRedCar, fRedCar, gGreenCar, bBlueCar));
    }

    @Test
    public void oldGroupingByEnum() {
        Map<Car.Color, Set<Car>> carsByColor = new EnumMap<>(Car.Color.class);
        for (Car.Color color : Car.Color.values()) {
            carsByColor.put(color, new HashSet<>());
        }
        cars.forEach(car -> carsByColor.get(car.getColor()).add(car));
        System.out.println(carsByColor);
    }

    @Test
    public void groupingByEnum() {
        Map<Car.Color, Set<Car>> carsByColor = cars.stream()
                .collect(Collectors.groupingBy(Car::getColor, () -> new EnumMap<>(Car.Color.class), Collectors.toSet()));
        System.out.println(carsByColor);
    }

    @Test
    public void testMap() {
        Map<String, Integer> map = new HashMap<>();
        map.computeIfAbsent("A", String::length);
        map.computeIfAbsent("ABC", String::length);
        System.out.println(map);
    }

    @Test
    public void testThread() throws Exception {
        CarTest carTest = new CarTest();
        Thread first = new Thread(() -> {
            synchronized (carTest) {
                try {
                    carTest.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                carTest.append("Hey from first");
            }
        }, "First");
        Thread second = new Thread(() -> {
            synchronized (carTest) {
                try {
                    carTest.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                carTest.append("Hey from second");
            }
        }, "Second");
        first.start();
        second.start();
        synchronized (carTest) {
            carTest.notify();
        }
        TimeUnit.SECONDS.sleep(2);
        synchronized (carTest) {
            carTest.notify();
        }
        TimeUnit.SECONDS.sleep(2);
        System.out.println(carTest.sb);
    }

    private StringBuffer sb = new StringBuffer();

    private synchronized void append(String s) {
        sb.append(s).append("\n");
    }
}