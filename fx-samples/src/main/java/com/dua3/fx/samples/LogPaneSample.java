package com.dua3.fx.samples;

import com.dua3.fx.icons.IconUtil;
import com.dua3.fx.util.FxLogPane;
import com.dua3.utility.logging.LogLevel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class LogPaneSample extends Application {

    static {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    }

    public static final int AVERAGE_SLEEP_MILLIS = 100;
    public static final int LOG_BUFFER_SIZE = 1000;
    private final org.slf4j.Logger SLF4J_LOGGER = LoggerFactory.getLogger("SLF4J." + getClass().getName());
    private final java.util.logging.Logger JUL_LOGGER = java.util.logging.Logger.getLogger("JUL." + getClass().getName());
    private final org.apache.logging.log4j.Logger LOG4J_LOGGER = org.apache.logging.log4j.LogManager.getLogger("LOG4J." + getClass().getName());
    private final AtomicInteger n = new AtomicInteger();
    private volatile boolean done = false;

    public static void main(String[] args) {
        System.out.println("available icon providers: " + IconUtil.iconProviderNames());
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FxLogPane logPane = new FxLogPane(LOG_BUFFER_SIZE);

        Scene scene = new Scene(logPane, 300, 250);

        primaryStage.setTitle(getClass().getSimpleName());
        primaryStage.setScene(scene);
        primaryStage.show();

        startLoggingThreads();
    }

    private void startLoggingThreads() {
        // start threads
        final int numberOfImplementations = 3;
        Random random = new Random();
        for (final int implementation : IntStream.range(0, numberOfImplementations).toArray()) {// for (int implementation = 0; implementation < 3; implementation++) {
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                while (!done) {
                    long wait = random.nextLong(2 * AVERAGE_SLEEP_MILLIS * numberOfImplementations);
                    try {
                        Thread.sleep(wait);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    int nr = n.incrementAndGet();

                    int bound = implementation == 1 ? 6 : 5;
                    int levelInt = random.nextInt(bound);
                    LogLevel level = LogLevel.values()[implementation == 1 ? Math.max(0, levelInt - 1) : levelInt];

                    String msg = "Message #%d, imp %s, original integer level %d, level %s".formatted(nr, implementation, levelInt, level);

                    switch (implementation) {
                        case 0 -> {
                            switch (levelInt) {
                                case 0 -> SLF4J_LOGGER.trace(msg);
                                case 1 -> SLF4J_LOGGER.debug(msg);
                                case 2 -> SLF4J_LOGGER.info(msg);
                                case 3 -> SLF4J_LOGGER.warn(msg);
                                case 4 -> SLF4J_LOGGER.error(msg, generateThrowable(random));
                                default -> throw new IllegalStateException("integer out of range");
                            }
                        }
                        case 1 -> {
                            switch (levelInt) {
                                case 0 -> JUL_LOGGER.finest(msg);
                                case 1 -> JUL_LOGGER.finer(msg);
                                case 2 -> JUL_LOGGER.fine(msg);
                                case 3 -> JUL_LOGGER.info(msg);
                                case 4 -> JUL_LOGGER.warning(msg);
                                case 5 -> JUL_LOGGER.log(java.util.logging.Level.SEVERE, msg, generateThrowable(random));
                                default -> throw new IllegalStateException("integer out of range");
                            }
                        }
                        case 2 -> {
                            switch (levelInt) {
                                case 0 -> LOG4J_LOGGER.trace(msg);
                                case 1 -> LOG4J_LOGGER.debug(msg);
                                case 2 -> LOG4J_LOGGER.info(msg);
                                case 3 -> LOG4J_LOGGER.warn(msg);
                                case 4 -> LOG4J_LOGGER.error(msg, generateThrowable(random));
                                default -> throw new IllegalStateException("integer out of range");
                            }
                        }
                    }

                    int current = n.get();
                    if (current % 100 == 0) {
                        System.err.format("That was %d messages%n", current);
                    } else if (current % 10 == 0) {
                        System.out.format("That was %d messages%n", current);
                    }
                }
            }, "Logger-Thread-" + implementation);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private IllegalStateException generateThrowable(Random random) {
        if (random.nextBoolean()) {
            return new IllegalStateException("Why?", new UnsupportedOperationException("Because of me!"));
        } else {
            return new IllegalStateException("What happened?");
        }
    }

}
