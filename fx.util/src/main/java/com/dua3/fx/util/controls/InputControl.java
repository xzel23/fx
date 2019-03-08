package com.dua3.fx.util.controls;

import java.util.Optional;

import javafx.scene.Node;

/**
 * Interface for an input field.
 *
 * @param <R> the input result type
 */
public interface InputControl<R> {
	/**
	 * Get the Node for this input element.
	 * 
	 * @return the node
	 */
	Node node();

	/**
	 * Get value.
	 * 
	 * @return the current value
	 */
	R get();

	/**
	 * Set value.
	 * 
	 * @param arg the value to set
	 */
	void set(R arg);

	/**
	 * Validate user input.
	 * 
	 * @return if not valid, an Optional containing the error; otherwise an empty
	 *         Optional
	 */
	default Optional<String> validate() {
		return Optional.empty();
	}
	
	/**
	 * Set/update control state.
	 */
	default void init() {
		// nop
	}
}