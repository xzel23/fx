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
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class EditorBase extends BorderPane {
    private static final Logger LOG = Logger.getLogger(EditorBase.class.getName());

    public static final String LOGGER_TAG_JAVA_SCRIPT = "[JavaScript]";
    private static final Logger LOG_JS = Logger.getLogger(EditorBase.class.getName() + LOGGER_TAG_JAVA_SCRIPT);

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
     * @param jsEditorInstance
     *  the JavaScript variable that holds the editor instance
     */
    protected EditorBase(URL fxml, URL html, String jsEditorInstance) {
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
        WebViews.filterEvents(webview,
                k -> k.getEventType().equals(KeyEvent.KEY_PRESSED) && k.isShortcutDown()
                        && ((k.getCode().equals(KeyCode.C) || k.getCode().equals(KeyCode.INSERT))
                        || (k.getCode().equals(KeyCode.X) || k.getCode().equals(KeyCode.CUT))
                        || (k.getCode().equals(KeyCode.V) || k.getCode().equals(KeyCode.PASTE))),
                m -> false);

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

        // enable alert, prompt, and confirmation and logging
        WebViews.setupEngine(engine, LOG_JS);

        // inject JavaScript bridge after loading
        engine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == State.SUCCEEDED) {
                LOG.info("[webengine] " + newState + " - binding bridge");
                bridge.bind(jsEditorInstance);
            }
        });

        // load editor
        engine.load(html.toString());
        LOG.fine("Editor component created");
    }

    /**
     * Wait until the editor's implementation is ready to be used, i. e. the HTML and Javascript resources have
     * been loaded.
     * @param millis
     *  timeout
     * @return
     *  true, if editor is ready
     *  false, if timed out before editor ready
     */
    public boolean waitReady(int millis) {
        final CountDownLatch latch = new CountDownLatch(1);

        ChangeListener<? super Boolean> cl = (ov, oldValue, newValue) -> {
            if (newValue) {
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

    /**
     * Get editor-ready-property.
     * @return
     *  this editor's editor-ready-property
     */
    public ReadOnlyBooleanProperty editorReadyProperty() {
        return bridge.editorReadyProperty;
    }

    /**
     * Get dirty-property.
     * @return
     *  this editor's dirty-property
     */
    public BooleanProperty dirtyProperty() {
        return bridge.dirtyProperty;
    }

    /**
     * Get readonly-property.
     * @return
     *  this editor's readonly-property
     */
    public BooleanProperty readOnlyProperty() {
        return bridge.readOnlyProperty;
    }

    /**
     * Set the text of this editor, keeping the current language.
     * @param text
     *  the text to set
     */
    public void setText(String text) {
        LOG.fine("setting editor text");
        callJS("setText", text);
    }

    /**
     * Clear the text of this editor, keeping the current language.
     */
    public void clear() {
        LOG.fine("clearing the editor");
        callJS("clear");
    }

    /**
     * Set the text of this editor instance.
     * @param text
     *  the text to set
     * @param uri
     *  the uri (i. e. used to determine the correct type)
     */
    public void setContent(String text, URI uri) {
        LOG.fine("setting editor content");
        callJS("setContent", text, uri.toString());
    }

    /**
     * Set the text of this editor instance.
     *
     * @param text the text to set
     * @param arg  either the document language or the URI to determine the language
     */
    public void setContent(String text, String arg) {
        LOG.fine("setting editor content");
        callJS("setContent", text, arg);
    }

    /**
     * Get the text edited.
     * @return
     *  the current text
     */
    public String getText() {
        return (String) callJS("getText");
    }

    public boolean isDirty() {
        return bridge.dirtyProperty.get();
    }

    public void markEditorClean() {
        bridge.markEditorClean();
    }

    public boolean isReadOnly() {
        return bridge.readOnlyProperty.get();
    }

    public void setReadOnly(boolean flag) {
        bridge.readOnlyProperty.set(flag);
    }

    public void undo() {
        bridge.undo();
    }

    public void redo() { bridge.redo(); }
    
    protected Object callJS(String command, Object... args) {
        LOG.finer(() -> "JS: " + command + " " + Arrays.toString(args));
        return bridge.call(command, args);
    }

    /**
     * Create settings dialog for editor.
     * @return
     *  An instance of EditorSettingsDialog for this editor instance
     */
    public abstract EditorSettingsDialog settingsDialog();

    /**
     * Get editor settings.
     * @return
     *  the settings for this editor instance
     */
    public abstract EditorSettings getSettings();

    /**
     * Apply editor settings.
     * @param settings
     *  the settings to apply
     */
    public abstract void apply(EditorSettings settings);

    /**
     * Get Array with Controls to show in ToolBar.
     * @return
     *  Array with toolbar controls
     * @param orientation
     *  horizontal or vertical (to instantiate the correct Separator)
     */
    public Node[] toolbarControls(Orientation /*unused*/ orientation) {
        return new Node[0];
    }

    private final Clipboard clipboard = Clipboard.getSystemClipboard();

    public void copy() {
        String text = bridge.getSelection();
        clipboard.setContent(Map.of(DataFormat.PLAIN_TEXT, text));
    }

    public void cut() {
        String text = bridge.getSelection();
        clipboard.setContent(Map.of(DataFormat.PLAIN_TEXT, text));
        bridge.replaceSelection("");
    }

    public void paste() {
        String text = Objects.toString(clipboard.getContent(DataFormat.PLAIN_TEXT));
        bridge.replaceSelection(text);
        bridge.unselectText();
    }
}
