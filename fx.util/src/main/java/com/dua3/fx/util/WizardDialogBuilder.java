package com.dua3.fx.util;

import java.util.LinkedHashMap;
import java.util.Optional;

import com.dua3.fx.util.WizardDialog.Page;

import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

public class WizardDialogBuilder {

	private String title = "";
	private String startPage = null;
	
	public WizardDialogBuilder title(String title) {
		this.title = title;
		return this;
	}

	LinkedHashMap<String, Page> pages = new LinkedHashMap<>();
	
	public WizardDialogBuilder page(String name, AbstractDialogPaneBuilder<?,?> builder) {
		Page page = new Page();
		page.setNext(builder.next);
		DialogPane pane = (DialogPane) builder.build();
		page.setPane(pane);
		pages.put(name, page);
		
		if (startPage==null) {
			setStartPage(name);
		}
		
		return this;
	}
	
	public void setStartPage(String startPage) {
		this.startPage = startPage;
	}
	
	public String getStartPage() {
		return startPage;
	}

	public Optional<ButtonType> showAndWait() {
		return build().showAndWait();
	}

	public WizardDialog build() {
		WizardDialog dlg = new WizardDialog();

		dlg.setTitle(title);	
		dlg.setPages(new LinkedHashMap<>(pages), getStartPage());

		return dlg;
	}
	
}
