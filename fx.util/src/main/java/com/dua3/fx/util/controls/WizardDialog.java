package com.dua3.fx.util.controls;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	
	public WizardDialog() {
		setResultConverter(btn -> {
			if (btn != ButtonType.FINISH) {
				return null;
			}
			
			pageStack.stream().map(name -> pages.get(name)).forEach(page -> page.apply());

			return btn;
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
	public static class Page {
		private DialogPane pane;
		private String previous;
		private String next;
		private Consumer<? super DialogPane> resultHandler;
		
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

		@SuppressWarnings("unchecked")
		public <P extends DialogPane> void setPane(P pane, Consumer<P> resultHandler) {
			this.pane = pane;
			this.resultHandler = (Consumer<? super DialogPane>) resultHandler;
		}
		
		public void apply() {
			resultHandler.accept(pane);
		}
	}

	/** Map {@code <page-name> |-> <page-information>}. */ 
	private Map<String, Page> pages;
	/** The currently displayed page. */
	private Page currentPage;
	/** Stack of displayed pages (for navigating back). */
	private ObservableList<String> pageStack = FXCollections.observableArrayList();

	public void setPages(Map<String,Page> pages, String startPage) {
		this.pages = pages;
	
		checkPages();
		
		setPage(startPage);
	}

	private void checkPages() {		
		Set<String> pageNames = pages.keySet();
		for (Entry<String, Page> entry: pages.entrySet()) {
			String name = entry.getKey();
			Page page = entry.getValue();
			DialogPane pane = page.getPane();
			
			// check page names
			String next = page.getNext();
			if (next != null && !pageNames.contains(next)) {
				throw new IllegalStateException(String.format("Page '%s': next page doesn't exist ['%s']", name, next));
			}
			
			// prepare buttons
			 pane.getButtonTypes().clear();
			 
			// cancel button
			if (isCancelable()) {
				addButtonToDialogPane(pane, ButtonType.CANCEL, null, null);
			}
			
			// next button
			if (page.getNext()==null) {
				addButtonToDialogPane(pane, ButtonType.FINISH, null, null);			
			} else {
				addButtonToDialogPane(pane, ButtonType.NEXT, evt -> {
					pageStack.add(name);
					setPage(page.getNext());
				}, null);
			}
			
			// prev button
			if (isShowPreviousButton()) {
				addButtonToDialogPane(
					pane, 
					ButtonType.PREVIOUS, evt -> { 
						setPage(pageStack.remove(pageStack.size()-1)); 
					}, 
					Bindings.isNotEmpty(pageStack)
				);
			}					
		}
	}

	private void setPage(String pageName) {
		this.currentPage = pages.get(pageName);
		
		DialogPane pane = currentPage.pane;
		setDialogPane(pane);		

		pane.layout();
		pane.getScene().getWindow().sizeToScene();
		
		LOG.log(Level.FINE, () -> "current page: "+pageName);
	}

	public Page getCurrentPage() {
		return currentPage;
	}
	
	private void addButtonToDialogPane(DialogPane pane, ButtonType bt, Consumer<Event> action, BooleanBinding enabled) {
		List<ButtonType> buttons = pane.getButtonTypes();
		
		buttons.add(bt);	
		Button btn = (Button) pane.lookupButton(bt);
		
		if (action!=null) {
			// it seems counter-intuitive to use an event filter instead of a handler, but
			// when using an event handler, Dialog.close() is called before our own
			// event handler.
			btn.addEventFilter(ActionEvent.ACTION,  evt -> {
					action.accept(evt);
					evt.consume();
			});
		}
		
		if (enabled!=null) {
			btn.disableProperty().bind(Bindings.not(enabled));
		}
	}
	
}
