package com.test.effectivejava.enums;


import java.util.*;

class Car {
    private final String brand;
    private Color color;

    public Car(String brand, Color color) {
        this.brand = brand;
        this.color = color;
    }

    enum Color {
        RED, GREEN, BLUE
    }

    public String getBrand() {
        return brand;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(brand, car.brand) &&
                color == car.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, color);
    }

    @Override
    public String toString() {
        return "Car{" +
                "brand='" + brand + '\'' +
                ", color=" + color +
                '}';
    }
}
