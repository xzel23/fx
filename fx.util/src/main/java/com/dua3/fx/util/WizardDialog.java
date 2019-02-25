package com.dua3.fx.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

public class WizardDialog extends Dialog<ButtonType> {

	/** Logger instance */
    private static final Logger LOG = Logger.getLogger(WizardDialog.class.getName());

	private boolean cancelable = true;
	
	public boolean isCancelable() {
		return cancelable;
	}
	
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

	private Map<String, Page> pages;
	private Page currentPage;

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
		
		DialogPane dialogPane = currentPage.pane;
		setDialogPane(dialogPane);
		
		List<ButtonType> buttons = dialogPane.getButtonTypes();
		buttons.clear();
		
		if (isCancelable()) {
			buttons.add(ButtonType.CANCEL);
		}
		
		if (currentPage.getNext()==null) {
			buttons.add(ButtonType.FINISH);			
		} else {
			buttons.add(ButtonType.NEXT);	
			Button btn = (Button) dialogPane.lookupButton(ButtonType.NEXT);
			btn.setOnAction( evt -> setPage(currentPage.getNext()) );
		}
		
		LOG.log(Level.FINE, () -> "current page: "+pageName);
	}
}
