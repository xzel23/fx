package com.dua3.fx.controls;

import com.dua3.fx.controls.AbstractDialogPaneBuilder.ResultHandler;
import com.dua3.utility.data.Pair;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;


import java.util.stream.Collectors;

public class WizardDialog extends Dialog<Map<String,Object>> {

	/** Logger instance */
    private static final Logger LOG = LoggerFactory.getLogger(WizardDialog.class);

    /** Cancelable flag. */
	private boolean cancelable = true;

	/** Flag: show 'previous'-button? */
	private boolean showPreviousButton = true;

	public WizardDialog() {
		setResultConverter(btn -> {
			// add current page to the stack, then build and return the result map
			pageStack.add(current);

			return pageStack.stream().collect(Collectors.toMap(Pair::first, p -> p.second().result, (a, b) -> b, LinkedHashMap::new));
		});
	}

	/**
	 * Check if dialog can be canceled.
	 * @return true if dialog is cancelable
	 */
	public boolean isCancelable() {
		return cancelable;
	}

	/**
	 * Check if a 'previous' ( or 'navigate-back') button should be displayed.
	 * @return true if dialog is cancelable
	 */
	public boolean isShowPreviousButton() {
		return showPreviousButton;
	}

	/**
	 * Wizard page information class.
	 */
	static class Page<D extends InputDialogPane<R>,R> {
		private D pane;
		private String next;
		private R result;
		private ResultHandler<R> resultHandler;

		String getNext() {
			return next;
		}

		void setNext(String next) {
			this.next = next;
		}

		D getPane() {
			return pane;
		}

		void setPane(D pane, ResultHandler<R> resultHandler) {
			this.pane = pane;
			this.resultHandler = resultHandler;
		}

		boolean apply(ButtonType btn) {
			R r = pane.get();
			boolean done = resultHandler.handleResult(btn, r);
			this.result = done ? r : null;
			return done;
		}
	}

	/** Map {@code <page-name> |-> <page-information>}. */
	private Map<String, Page<?,?>> pages;
	/** The currently displayed page. */
	private Pair<String,Page<?,?>> current;
	/** Stack of displayed pages (for navigating back). */
	private final ObservableList<Pair<String,Page<?,?>>> pageStack = FXCollections.observableArrayList();

	public void setPages(Map<String,Page<?,?>> pages, String startPage) {
		this.pages = pages;

		checkPages();

		setPage(startPage);
	}

	private void checkPages() {
		Set<String> pageNames = pages.keySet();
		for (Entry<String, Page<?,?>> entry: pages.entrySet()) {
			String name = entry.getKey();
			Page<?,?> page = entry.getValue();
			InputDialogPane<?> pane = page.getPane();

			// check page names
			String next = page.getNext();
			if (next != null && !pageNames.contains(next)) {
				throw new IllegalStateException(String.format("Page '%s': next page doesn't exist ['%s']", name, next));
			}

			// prepare buttons
			pane.initButtons();

			// cancel button
			if (isCancelable()) {
				addButtonToDialogPane(page, ButtonType.CANCEL, p -> {}, null);
			}

			// next button
			if (page.getNext()==null) {
				addButtonToDialogPane(page, ButtonType.FINISH, p -> {}, pane.validProperty());
			} else {
				addButtonToDialogPane(
						page,
						ButtonType.NEXT,
						p -> {
							pageStack.add(Pair.of(name,page));
							setPage(page.getNext());
						},
						pane.validProperty());
			}

			// prev button
			if (isShowPreviousButton()) {
				addButtonToDialogPane(
						page,
						ButtonType.PREVIOUS,
						p -> setPage(pageStack.remove(pageStack.size()-1).first()),
						Bindings.isNotEmpty(pageStack)
				);
			}
		}
	}

	private void setPage(String pageName) {
		this.current = Pair.of(pageName, pages.get(pageName));

		InputDialogPane<?> pane = current.second().pane;
		setDialogPane(pane);

		pane.init();
		pane.layout();
		pane.getScene().getWindow().sizeToScene();

		LOG.debug("current page: {}", pageName);
	}

	public Page<?,?> getCurrentPage() {
		return current.second();
	}

	private static void addButtonToDialogPane(
			Page<?, ?> page,
			ButtonType bt,
			Consumer<InputDialogPane<?>> action,
			BooleanExpression enabled) {
		InputDialogPane<?> pane = page.pane;
		List<ButtonType> buttons = pane.getButtonTypes();

		buttons.add(bt);
		Button btn = (Button) pane.lookupButton(bt);

		// it seems counter-intuitive to use an event filter instead of a handler, but
		// when using an event handler, Dialog.close() is called before our own
		// event handler.
		btn.addEventFilter(ActionEvent.ACTION,  evt -> {
			// get and translate result
			if (!page.apply(bt)) {
				LOG.debug("Button {}: result conversion failed", bt);
				evt.consume();
			}

			action.accept(page.getPane());
		});

		if (enabled!=null) {
			btn.disableProperty().bind(Bindings.not(enabled));
		}
	}

}
