package com.dua3.fx.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

public class WizardDialog extends Dialog<ButtonType> {

	/** Logger instance */
    private static final Logger LOG = Logger.getLogger(WizardDialog.class.getName());

    /** Cancelable flag. */
	private boolean cancelable = true;
	
	/** Flag: show 'previous'-button? */
	private boolean showPreviousButton = true;
	
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
	public static class Page {
		private DialogPane pane;
		private String previous;
		private String next;
		
		public String getPrevious() {
			return previous;
		}
		
		public void setPrevious(String previous) {
			this.previous = previous;
		}
		
		public String getNext() {
			return next;
		}
		
		public void setNext(String next) {
			this.next = next;
		}
		
		public DialogPane getPane() {
			return pane;
		}

		public void setPane(DialogPane pane) {
			this.pane = pane;
		}
	}

	/** Map {@code <page-name> |-> <page-information>}. */ 
	private Map<String, Page> pages;
	/** The currently displayed page. */
	private Page currentPage;
	/** Stack of displayed pages (for navigating back). */
	private Stack<String> pageStack = new Stack<>();
			
	public void setPages(Map<String,Page> pages, String startPage) {
		this.pages = pages;
	
		checkPages();
		
		setPage(startPage);
	}

	private void checkPages() {
		Set<String> pageNames = pages.keySet();
		for (Entry<String, Page> entry: pages.entrySet()) {
			String name = entry.getKey();
			String next = entry.getValue().getNext();
			if (next != null && !pageNames.contains(next)) {
				throw new IllegalStateException(String.format("Page '%s': next page doesn't exist ['%s']", name, next));
			}
		}
	}

	private void setPage(String pageName) {
		this.currentPage = pages.get(pageName);
		
		setDialogPane(currentPage.pane);		
		
		getDialogPane().getButtonTypes().clear();
		
		// cancel button
		if (isCancelable()) {
			addButtonToDialogPane(ButtonType.CANCEL);
		}
		
		// next button
		if (currentPage.getNext()==null) {
			addButtonToDialogPane(ButtonType.FINISH);			
		} else {
			addButtonToDialogPane(ButtonType.NEXT, evt -> {
				pageStack.push(pageName);
				setPage(currentPage.getNext());
			});
		}
		
		// prev button
		if (isShowPreviousButton() && !pageStack.isEmpty()) {
			addButtonToDialogPane(ButtonType.PREVIOUS, evt -> { 
				setPage(pageStack.pop()); 
			});
		}
		
		LOG.log(Level.FINE, () -> "current page: "+pageName);
	}

	private void addButtonToDialogPane(ButtonType bt) {
		currentPage.pane.getButtonTypes().add(bt);
	}
	
	private void addButtonToDialogPane(ButtonType bt, Consumer<Event> action) {
		DialogPane dialogPane = currentPage.pane;
		List<ButtonType> buttons = dialogPane.getButtonTypes();
		
		buttons.add(bt);	
		Button btn = (Button) dialogPane.lookupButton(bt);
		
		// it seems counter-intuitive to use an event filter instead of a handler, but
		// when using an event handler, Dialog.close() is called before our own
		// event handler.
		btn.addEventFilter(ActionEvent.ACTION,  evt -> {
				action.accept(evt);
				evt.consume();
		});
	}
	
}
