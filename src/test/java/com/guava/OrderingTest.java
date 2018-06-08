package com.guava;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Ordering;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrderingTest {
    List<Guest> guests = new ArrayList<>();

    @Before
    public void init() {
        guests.add(new Guest("a", 5));
        guests.add(new Guest("b", 3));
        guests.add(new Guest("c", 3));
        guests.add(new Guest("d", 2));
        guests.add(new Guest("b", 1));
        guests.add(new Guest("a", 4));
    }

    @Test
    public void ordering() {
        Ordering<Guest> ordering = Ordering
                .from(Guest.nameComparator)
                .reverse()
                .compound(Guest.ageComparator);
        guests.sort(ordering);
        System.out.println(guests);
    }

    static class Guest {
        private final String name;
        private final int age;

        static final Comparator<Guest> nameComparator = Comparator.comparing(Guest::getName);
        static final Comparator<Guest> ageComparator = Comparator.comparing(Guest::getAge);

        Guest(String name, int age) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Name is null");
            Preconditions.checkArgument(age > 0, "Age less than 1");
            this.name = name;
            this.age = age;
        }

        String getName() {
            return name;
        }

        int getAge() {
            return age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Guest guest = (Guest) o;
            return age == guest.age &&
                    Objects.equal(name, guest.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, age);
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("Guest [\n\t")
                    .append(String.format("name: \"%s\"%n\t", name))
                    .append(String.format("age: %d%n", age))
                    .append("]")
                    .toString();
        }
    }
}
