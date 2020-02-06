package com.dua3.fx.util;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dua3.utility.lang.LangUtil;

import javafx.application.Platform;

public final class PlatformHelper {

    /** Logger instance */
    private static final Logger LOG = Logger.getLogger(PlatformHelper.class.getName());

    /**
     * Run task on the JavaFX application thread and return result.
     *
     * @param                       <T>
     *                              the result type
     * @param  action
     *                              the task to run
     * @return
     *                              the result returned by action
     * @throws NullPointerException
     *                              if {@code action} is {@code null}
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
                LOG.log(Level.FINE, "interrupted", e);
                Thread.currentThread().interrupt();
            }
        }

        return result.get();
    }

    /**
     * Run task on the JavaFX application thread and wait for completion.
     *
     * @param  action
     *                              the task to run
     * @throws NullPointerException
     *                              if {@code action} is {@code null}
     */
    public static void runAndWait(Runnable action) {
        runAndWait(() -> {
            action.run();
            return null;
        });
    }

    /**
     * Run task on the JavaFX application thread.
     *
     * @param  action
     *                              the task to run
     * @throws NullPointerException
     *                              if {@code action} is {@code null}
     */
    public static void runLater(Runnable action) {
        Objects.requireNonNull(action);

        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }

    public static void checkThread() {
        LangUtil.check(Platform.isFxApplicationThread(), "not on FX Application Thread");
    }

    private PlatformHelper() {
        // utility class
    }
}
