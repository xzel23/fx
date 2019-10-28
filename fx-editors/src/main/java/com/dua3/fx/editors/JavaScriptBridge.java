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

import com.dua3.fx.util.PlatformHelper;
import com.dua3.fx.web.WebViews;
import com.dua3.utility.lang.LangUtil;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bridge to provide a link between the JavaFX-component and the JavaScript code running
 * inside the WebView.
 */
public class JavaScriptBridge {
	/**
	 * The logger instance.
	 */
	public static final Logger LOG = Logger.getLogger(JavaScriptBridge.class.getName());
	/**
	 * Property that indicates whether current document is editable.
	 */
	final BooleanProperty readOnlyProperty = new SimpleBooleanProperty(false);
	/**
	 * Property that indicates editor's dirty state.
	 */
	final BooleanProperty dirtyProperty = new SimpleBooleanProperty(false);
	/**
	 * Property that indicates whether the editor is ready to be used.
	 */
	final BooleanProperty editorReadyProperty = new SimpleBooleanProperty(false);
	/**
	 * The WebEngine instance.
	 */
	private final WebEngine engine;
	private JSObject jsEditor;
	/**
	 * The logging level.
	 */
	private Level level = Level.INFO;

	/**
	 * Constructor.
	 *
	 * @param webView the WebView instance where the editor is displayed
	 */
	public JavaScriptBridge(WebView webView) {
		this.engine = webView.getEngine();
	}

	/**
	 * Execute method of the JavaScript editor.
	 *
	 * @param method the method name
	 * @param args   the method arguments
	 * @return the method return
	 */
	public Object call(String method, Object... args) {
		return PlatformHelper.runAndWait(() -> jsEditor.call(method, args));
	}

	/**
	 * Bind bridge to JavaScript editor instance.
	 * @param jsEditorInstance
	 *  the JavaScript variable that holds the editor instance
	 */
	void bind(String jsEditorInstance) {
		Platform.runLater(() -> {
			// get some references to objects and ethods
			JSObject win = (JSObject) engine.executeScript("window");
			win.setMember("bridge", this);

			// get editor instance
			Object ret = win.getMember(jsEditorInstance);
			LangUtil.check(ret instanceof JSObject, "editor construction failed");
			jsEditor = (JSObject) ret;

			// readonly-property
			readOnlyProperty.addListener((v, ov, nv) ->
					Platform.runLater(() -> call("setReadOnly", nv))
			);
			call("setReadOnly", readOnlyProperty.get());

			// bind dirty-property
			Object dirtyCallback = engine.executeScript("(function (flag) { bridge.setDirty(flag); })");
			jsEditor.call("setOnChangedDirtyState", dirtyCallback);

			// mark editor as ready for use
			editorReadyProperty.set(true);
		});
	}

	private Object createObject(JSObject win, String createMethod, Object... args) {
		return WebViews.callMethod(win, createMethod, args);
	}

	/**
	 * Set the dirty flag. Called from JavaScript.
	 *
	 * @param dirty the dirty flag
	 */
	public void setDirty(boolean dirty) {
		dirtyProperty.set(dirty);
	}

	/**
	 * Execute JavaScript code asynchronously.
	 *
	 * @param script the code to execute
	 */
	public void executeScript(String script) {
		Platform.runLater(() -> {
			try {
				engine.executeScript(script);
			} catch (JSException e) {
				LOG.log(Level.WARNING, e.getMessage() + " - script: " + script);
				throw e;
			}
		});
	}

	/**
	 * Execute JavaScript code synchronously and return result.
	 *
	 * @param script the code to execute
	 * @return the value returned by the script
	 */
	public Object callScript(String script) {
		try {
			return engine.executeScript(script);
		} catch (JSException e) {
			LOG.log(Level.WARNING, e.getMessage() + " - script: " + script);
			throw e;
		}
	}

	public void markEditorClean() {
		jsEditor.call("markEditorClean");
	}

	public String getSelection() {
		return Objects.toString(jsEditor.call("getSelection"));
	}

	public void replaceSelection(String text) {
		jsEditor.call("replaceSelection", text);
	}

	public void unselectText() {
		jsEditor.call("unselectText");
	}
}
