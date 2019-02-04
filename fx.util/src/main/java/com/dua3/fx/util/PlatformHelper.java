package com.dua3.fx.util;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import javafx.application.Platform;

public class PlatformHelper {

	/**
	 * Run task on the JavaFX application thread and return result.
	 *
	 * @param action the task to run
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

	    try {
	        doneLatch.await();
	    } catch (InterruptedException e) {
	        // ignore exception
	    }
	    
	    return result.get();
	}
	
	/**
	 * Run task on the JavaFX application thread and wait for completion.
	 *
	 * @param action the task to run
	 * @throws NullPointerException if {@code action} is {@code null}
	 */
	public static void runAndWait(Runnable action) {
		runAndWait(() -> { action.run(); return null; });
	}
	
	private PlatformHelper() {
		// utility class
	}
}
