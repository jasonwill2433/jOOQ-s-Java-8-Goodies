package org.jooq.java8.goodies.lang;

import org.junit.Assert;

import java.sql.SQLException;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

/**
 * Created by Lukas on 22.05.2014.
 */
public class ExceptionGoodies {
    public static void main(String[] args) {

        // Pass:
        assertThrows(Exception.class, () -> { throw new Exception(); });
        assertThrows(Exception.class, () -> { throw new Exception("Message"); },
                e -> assertEquals("Message", e.getMessage()));

        // Fails
        try {
            assertThrows(SQLException.class, () -> {
                throw new Exception();
            });
        }
        catch (Throwable t) {
            t.printStackTrace();
        }

        withExceptions(
            () -> assertThrows(SQLException.class, () -> {
                throw new Exception();
            }),
            t -> t.printStackTrace()
        );
    }

    static void withExceptions(
            ThrowableRunnable runnable
    ) {
        withExceptions(runnable, t -> {});
    }

    static void withExceptions(
            ThrowableRunnable runnable,
            Consumer<Throwable> exceptionConsumer
    ) {
        try {
            runnable.run();
        }
        catch (Throwable t) {
            exceptionConsumer.accept(t);
        }
    }

    static void assertThrows(
            Class<? extends Throwable> throwable,
            ThrowableRunnable runnable
    ) {
        assertThrows(throwable, runnable, t -> {});
    }

    static void assertThrows(
            Class<? extends Throwable> throwable,
            ThrowableRunnable runnable,
            Consumer<Throwable> exceptionConsumer
    ) {
        boolean fail = false;
        try {
            runnable.run();
            fail = true;
        }
        catch (Throwable t) {
            if (!throwable.isInstance(t))
                Assert.fail("Bad exception type. Expected: " + throwable.getSimpleName() + " Got: " + t.getClass().getSimpleName() + "");

            exceptionConsumer.accept(t);
        }

        if (fail)
            Assert.fail("No exception was thrown");
    }
}

@FunctionalInterface
interface ThrowableRunnable {
    void run() throws Throwable;
}

