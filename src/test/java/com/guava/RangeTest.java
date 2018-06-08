package com.guava;

import com.google.common.base.Throwables;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import org.junit.Test;

public class RangeTest {

    @Test
    public void testRange() {
        Range<Integer> integerRange = Range.closed(1, 10);
        ContiguousSet<Integer> integers = ContiguousSet.create(integerRange, DiscreteDomain.integers());
        for (Integer integer : integers) {
            System.out.print(integer + ", ");
        }
    }
}
