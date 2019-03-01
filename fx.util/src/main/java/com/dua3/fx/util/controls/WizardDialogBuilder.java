package com.dua3.fx.util.controls;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Consumer;

import com.dua3.fx.util.controls.WizardDialog.Page;

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
	
	public WizardDialogBuilder page(String name, StandardDialogPaneBuilder<?,?,?> builder) {
		Page page = new Page();
		page.setNext(builder.next);
		DialogPane pane = (DialogPane) builder.build();
		Consumer<DialogPane> resultHandler = builder.getResultHandler();
		page.setPane(pane, resultHandler);
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

		Page prev = null;
		for (var entry:pages.entrySet()) {
			String name = entry.getKey();
			Page page = entry.getValue();
			
			if (prev!= null && prev.getNext()==null) {
				prev.setNext(name);
			}
			
			prev = page;
		}

		dlg.setTitle(title);	
		dlg.setPages(new LinkedHashMap<>(pages), getStartPage());

		return dlg;
	}
	
}
