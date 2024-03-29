package com.dua3.fx.controls;

import com.dua3.fx.controls.AbstractDialogPaneBuilder.ResultHandler;
import com.dua3.fx.controls.WizardDialog.Page;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class WizardDialogBuilder {

    final LinkedHashMap<String, Page<?, ?>> pages = new LinkedHashMap<>();
    private String title = "";
    private String startPage = null;

    WizardDialogBuilder() {}

    public WizardDialogBuilder title(String title) {
        this.title = title;
        return this;
    }

    public <D extends InputDialogPane<R>, B extends AbstractPaneBuilder<D, B, R>, R> WizardDialogBuilder page(String name, B builder) {
        Page<D, R> page = new Page<>();
        page.setNext(builder.next);
        D pane = builder.build();
        ResultHandler<R> resultHandler = builder.getResultHandler();
        page.setPane(pane, resultHandler);
        pages.put(name, page);

        if (startPage == null) {
            setStartPage(name);
        }

        return this;
    }

    @SuppressWarnings("OptionalContainsCollection")
    public Optional<Map<String, Object>> showAndWait() {
        return build().showAndWait();
    }

    public WizardDialog build() {
        WizardDialog dlg = new WizardDialog();

        Page<?, ?> prev = null;
        for (var entry : pages.entrySet()) {
            String name = entry.getKey();
            Page<?, ?> page = entry.getValue();

            if (prev != null && prev.getNext() == null) {
                prev.setNext(name);
            }

            prev = page;
        }

        dlg.setTitle(title);
        dlg.setPages(new LinkedHashMap<>(pages), getStartPage());

        return dlg;
    }

    public String getStartPage() {
        return startPage;
    }

    public void setStartPage(String startPage) {
        this.startPage = startPage;
    }

}
