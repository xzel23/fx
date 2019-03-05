package com.dua3.fx.util.controls;

import java.util.LinkedHashMap;
import java.util.Optional;

import com.dua3.fx.util.controls.AbstractDialogPaneBuilder.ResultHandler;
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

	LinkedHashMap<String, Page<?,?>> pages = new LinkedHashMap<>();
	
	public <D extends TypedDialogPane<R>,B extends AbstractPaneBuilder<D,B,R>,R> WizardDialogBuilder page(String name, B builder) {
		Page<D,R> page = new Page<>();
		page.setNext(builder.next);
		D pane = builder.build();
		ResultHandler<R> resultHandler = builder.getResultHandler();
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

		Page<?,?> prev = null;
		for (var entry:pages.entrySet()) {
			String name = entry.getKey();
			Page<?,?> page = entry.getValue();
			
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
