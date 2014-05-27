package org.jooq.java8.goodies.test;

import org.hamcrest.CustomMatcher;
import org.junit.Assert;

import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by Lukas on 27.05.2014.
 */
public class TestGoodies {

    public static void main(String[] args) {
        Object theBiscuit = new Object();
        Object myBiscuit = theBiscuit;

        assertThat(theBiscuit, b -> b == myBiscuit);
        assertThat(Math.sqrt(-1), n -> Double.isNaN(n));
        assertThat(Math.sqrt(-1), Double::isNaN);
    }

    static final String DEFAULT_MESSAGE = "Test failed";

    static <T> void assertThat(T actual, Predicate<T> expected) {
        assertThat(() -> actual, expected, DEFAULT_MESSAGE);
    }

    static <T> void assertThat(T actual, Predicate<T> expected, String message) {
        assertThat(() -> actual, expected, message);
    }

    static <T> void assertThat(Supplier<T> actual, Predicate<T> expected) {
        assertThat(actual, expected, DEFAULT_MESSAGE);
    }

    static <T> void assertThat(Supplier<T> actual, Predicate<T> expected, String message) {
        if (!expected.test(actual.get()))
            throw new AssertionError(message);
    }

    static void assertThat(double actual, DoublePredicate expected) {
        assertThat(() -> actual, expected, DEFAULT_MESSAGE);
    }

    static void assertThat(double actual, DoublePredicate expected, String message) {
        assertThat(() -> actual, expected, message);
    }

    static void assertThat(DoubleSupplier actual, DoublePredicate expected) {
        assertThat(actual, expected, DEFAULT_MESSAGE);
    }

    static void assertThat(DoubleSupplier actual, DoublePredicate expected, String message) {
        if (!expected.test(actual.getAsDouble()))
            throw new AssertionError(message);
    }
}
