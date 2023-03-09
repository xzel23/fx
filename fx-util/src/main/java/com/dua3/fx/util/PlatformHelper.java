package com.dua3.fx.util;

import com.dua3.utility.lang.LangUtil;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class PlatformHelper {

    /**
     * Logger instance
     */
    private static final Logger LOG = LoggerFactory.getLogger(PlatformHelper.class);

    private PlatformHelper() {
        // utility class
    }

    /**
     * Run task on the JavaFX application thread and wait for completion.
     * Consider using {@link #runLater(Runnable)} to avoid executing tasks out of order.
     *
     * @param action the task to run
     * @throws NullPointerException if {@code action} is {@code null}
     */
    public static void runAndWait(Runnable action) {
        runAndWait(() -> {
            action.run();
            return null;
        });
    }

    /**
     * Run task on the JavaFX application thread and return result.
     *
     * @param <T>    the result type
     * @param action the task to run
     * @return the result returned by action
     * @throws NullPointerException if {@code action} is {@code null}
     */
    public static <T> T runAndWait(Supplier<T> action) {
        Objects.requireNonNull(action);

        // run synchronously on JavaFX thread
        if (Platform.isFxApplicationThread()) {
            return action.get();
        }

        // queue on JavaFX thread and wait for completion
        AtomicReference<T> result = new AtomicReference<>();
        final CountDownLatch doneLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                result.set(action.get());
            } finally {
                doneLatch.countDown();
            }
        });

        while (doneLatch.getCount() > 0) {
            try {
                doneLatch.await();
            } catch (InterruptedException e) {
                LOG.debug("interrupted", e);
                Thread.currentThread().interrupt();
            }
        }

        return result.get();
    }

    /**
     * Run task on the JavaFX application thread.
     *
     * @param action the task to run
     * @throws NullPointerException if {@code action} is {@code null}
     */
    public static void runLater(Runnable action) {
        Objects.requireNonNull(action);

        Platform.runLater(action);
    }

    public static void checkApplicationThread() {
        LangUtil.check(Platform.isFxApplicationThread(), "not on FX Application Thread");
    }
}
