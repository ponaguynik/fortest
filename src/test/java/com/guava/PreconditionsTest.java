package com.guava;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.junit.Test;

import java.util.Objects;

public class PreconditionsTest {

    @Test(expected = IllegalArgumentException.class)
    public void checkArgumentPrecondition() {
        Preconditions.checkArgument(1 > 4,  "Illegal argument");
    }

    @Test(expected = IllegalStateException.class)
    public void checkStatePrecondition() {
        String state = "NOT_READY";
        Preconditions.checkState(state.equals("READY"), "Not in READY state");
    }
}
