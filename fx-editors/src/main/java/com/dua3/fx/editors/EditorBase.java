// Copyright 2019 Axel Howind
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.dua3.fx.editors;

import com.dua3.fx.web.WebViews;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class EditorBase extends BorderPane {
    private static final Logger LOG = Logger.getLogger(EditorBase.class.getName());

    @FXML
    protected WebView webview;

    /**
     * Bridge for interfacing with JS code
     */
    private JavaScriptBridge bridge = null;

    protected JavaScriptBridge getBridge() {
        return bridge;
    }

    /**
     * Constructor.
     *
     * @param fxml the FXML resource to load the user interface definition from (filename)
     * @param html the HTML resource to load (filename)
     */
    protected EditorBase(URL fxml, URL html) {
        LOG.fine(() -> "creating Editor component");

        // load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(fxml);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            LOG.log(Level.WARNING, "exception while loading FXML", e);
            throw new UncheckedIOException(e);
        }

        // disable context menu
        webview.setContextMenuEnabled(false);

        // get the engine
        WebEngine engine = webview.getEngine();
        LOG.fine(() -> "user agent:" + engine.getUserAgent());

        // instantiate bridge
        bridge = new JavaScriptBridge(webview);

        // log load worker exceptions
        engine.getLoadWorker().exceptionProperty().addListener(
                (v, o, n) -> LOG.log(Level.WARNING, "[webengine] loadworker exception", n)
        );

        // log exceptions
        engine.onErrorProperty().set(
                evt -> LOG.warning("[webengine] " + evt.getMessage())
        );

        // enable alert, prompt, and confirmation
        WebViews.setAlertHandler(engine);
        WebViews.setConfirmationHandler(engine);
        WebViews.setPromptHandler(engine);

        // inject JavaScript bridge after loading
        engine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == State.SUCCEEDED) {
                LOG.info("[webengine] " + newState + " - binding bridge");
                bridge.bind();
            }
        });

        // load editor
        engine.load(html.toString());
        LOG.fine("Editor component created");
    }

    public boolean waitReady(int millis) {
        final CountDownLatch latch = new CountDownLatch(1);

        ChangeListener<? super Boolean> cl = (ov, oldValue, newValue) -> {
            if (newValue.booleanValue()) {
                latch.countDown();
            }
        };

        bridge.editorReadyProperty.addListener(cl);

        long timeRemaining = millis;
        while (timeRemaining > 0 && !bridge.editorReadyProperty.get()) {
            try {
                long s = System.currentTimeMillis();
                if (latch.await(millis, TimeUnit.MILLISECONDS)) {
                    break;
                }
                long e = System.currentTimeMillis();
                long elapsed = Math.max(e - s, 0);
                timeRemaining = Math.max(0, timeRemaining - elapsed);
            } catch (InterruptedException e) {
                LOG.log(Level.INFO, "interrupted", e);
                Thread.currentThread().interrupt();
            }
        }

        bridge.editorReadyProperty.removeListener(cl);

        return bridge.editorReadyProperty.get();
    }

    public ReadOnlyBooleanProperty editorReadyProperty() {
        return bridge.editorReadyProperty;
    }

    public BooleanProperty dirtyProperty() {
        return bridge.dirtyProperty;
    }

    public BooleanProperty readOnlyProperty() {
        return bridge.readOnlyProperty;
    }

    public void setText(String text) {
        LOG.fine("setting editor content");
        callJS("setText", text);
    }

    public void setText(String text, String ext) {
        LOG.fine("setting editor content");
        callJS("setContent", text, ext);
    }

    public String getText() {
        return (String) callJS("getText");
    }

    public boolean isDirty() {
        return bridge.dirtyProperty.get();
    }

    public void setDirty(boolean flag) {
        bridge.setDirty(flag);
    }

    public boolean isReadOnly() {
        return bridge.readOnlyProperty.get();
    }

    public void setReadOnly(boolean flag) {
        bridge.readOnlyProperty.set(flag);
    }

    public StringProperty promptTextProperty() {
        return bridge.promptTextProperty;
    }

    public String getPromptText() {
        return promptTextProperty().get();
    }

    public void setPromptText(String text) {
        promptTextProperty().set(text);
    }

    public Iterator<String> lineIterator() {
        return new Iterator<String>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < getLineCount();
            }

            @Override
            public String next() {
                if (i >= getLineCount()) {
                    throw new NoSuchElementException();
                }
                return getLine(i++);
            }
        };
    }

    protected Object callJS(String command, Object... args) {
        LOG.finer(() -> "JS: " + command + " " + args);
        return bridge.call(command, args);
    }

    /**
     * Cut selection to system clipboard.
     * This method will be called from the UI to be processed by the editor.
     */
    public void cut() {
        callJS("cut");
    }

    /**
     * Copy selection to system clipboard.
     * This method will be called from the UI to be processed by the editor.
     */
    public void copy() {
        callJS("copy");
    }

    /**
     * Paste system clipboard content.
     * This method will be called from the UI to be processed by the editor.
     */
    public void paste() {
        callJS("paste");
    }

    public int getLineCount() {
        return (int) callJS("getLineCount");
    }

    public int getLineNumber() {
        return (int) callJS("getLineNumber");
    }

    public String getLine(int idx) {
        return (String) callJS("getLine", idx);
    }

    public void addLine(String s) {
        callJS("addLine", s);
    }

    public void setLine(int i, String s) {
        callJS("setLine", i, s);
    }

    public void search() {
        callJS("search");
    }

    public abstract EditorSettingsDialog settingsDialog();

    public abstract EditorSettings getSettings();

    public abstract void apply(EditorSettings setting);
}
