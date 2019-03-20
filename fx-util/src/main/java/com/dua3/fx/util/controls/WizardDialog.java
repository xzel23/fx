package com.dua3.fx.util.controls;

import com.dua3.fx.util.controls.AbstractDialogPaneBuilder.ResultHandler;
import com.dua3.utility.data.Pair;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WizardDialog extends Dialog<Map<String,Object>> {

	/** Logger instance */
    private static final Logger LOG = Logger.getLogger(WizardDialog.class.getName());

    /** Cancelable flag. */
	private boolean cancelable = true;
	
	/** Flag: show 'previous'-button? */
	private boolean showPreviousButton = true;
	
	public WizardDialog() {
		setResultConverter(btn -> {
			// stay in the dialog if something is not ok or we haven't reached "Finish" yet
			if (btn != ButtonType.FINISH) {
				return null;
			}
			
			// otherwise add current page to the stack, then build and return the result map
			pageStack.add(current);
			
			LinkedHashMap<String, Object> result = new LinkedHashMap<>();
			pageStack.stream()
				.forEach( p -> result.put(p.first, p.second.result) );
				
			return result;
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
			R result = pane.get();
			boolean done = resultHandler.handleResult(btn, result);
			this.result = done ? result : null;
			return done;
		}
	}

	/** Map {@code <page-name> |-> <page-information>}. */ 
	private Map<String, Page<?,?>> pages;
	/** The currently displayed page. */
	private Pair<String,Page<?,?>> current;
	/** Stack of displayed pages (for navigating back). */
	private ObservableList<Pair<String,Page<?,?>>> pageStack = FXCollections.observableArrayList();

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
				addButtonToDialogPane(page, ButtonType.CANCEL, evt -> true, evt -> true, null);
			}
			
			// next button
			if (page.getNext()==null) {
				addButtonToDialogPane(page, ButtonType.FINISH, evt -> true, evt -> true, pane.validProperty());
			} else {
				addButtonToDialogPane(
						page,
						ButtonType.NEXT,
						evt -> true,
						evt -> {
							pageStack.add(Pair.of(name,page));
							setPage(page.getNext());
							return true;
						},
						pane.validProperty());
			}
			
			// prev button
			if (isShowPreviousButton()) {
				addButtonToDialogPane(
						page,
						ButtonType.PREVIOUS, evt -> {
							setPage(pageStack.remove(pageStack.size()-1).first);
							return false;
						},
						evt -> true,
						Bindings.isNotEmpty(pageStack)
				);
			}					
		}
	}

	private void setPage(String pageName) {
		this.current = Pair.of(pageName, pages.get(pageName));
		
		InputDialogPane<?> pane = current.second.pane;
		setDialogPane(pane);		

		pane.init();
		pane.layout();
		pane.getScene().getWindow().sizeToScene();
		
		LOG.log(Level.FINE, () -> "current page: "+pageName);
	}

	public Page<?,?> getCurrentPage() {
		return current.second;
	}
	
	private void addButtonToDialogPane(
			Page<?,?> page,
			ButtonType bt,
			Predicate<Event> precondition,
			Predicate<Event> postcondition,
			BooleanExpression enabled) {
		InputDialogPane<?> pane = page.pane;
		List<ButtonType> buttons = pane.getButtonTypes();
		
		buttons.add(bt);	
		Button btn = (Button) pane.lookupButton(bt);
		
		// it seems counter-intuitive to use an event filter instead of a handler, but
		// when using an event handler, Dialog.close() is called before our own
		// event handler.
		btn.addEventFilter(ActionEvent.ACTION,  evt -> {
			// check precondition
			if (!precondition.test(evt)) {
				LOG.fine(() -> String.format("Button %s: precondition failed", bt));
				evt.consume();
				return;
			}

			// get and translate result
			if (!page.apply(bt)) {
				LOG.fine(() -> String.format("Button %s: result conversion failed", bt));
				evt.consume();
				return;
			}

			// check postcondition
			if (!postcondition.test(evt)) {
				LOG.fine(() -> String.format("Button %s: postcondition failed", bt));
				evt.consume();
				return;
			}
		});
		
		if (enabled!=null) {
			btn.disableProperty().bind(Bindings.not(enabled));
		}
	}
	
}
