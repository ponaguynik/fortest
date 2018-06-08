package com.guava;

import com.google.common.collect.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CollectionsTest {

    @Test
    public void testMultiset() {
        Multiset<String> multiset = HashMultiset.create();
        multiset.add("hello");
        multiset.add("hello");
        multiset.add("hello");
        System.out.println(multiset.count("hello"));
        multiset.forEach(System.out::println);
    }

    @Test
    public void testMultimap() {
        Multimap<Integer, String> multimap = ArrayListMultimap.create();
        multimap.put(1, "a");
        multimap.put(1, "b");
        multimap.put(1, "c");
        multimap.put(2, "d");
        multimap.put(2, "e");
        System.out.println(multimap.get(1));
    }

    @Test
    public void testImmutableList() {
        List<String> list = ImmutableList.<String>builder()
                .add("a")
                .add("b")
                .build();
        System.out.println(list);
    }
}
