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
import com.dua3.utility.lang.LangUtil;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

import java.util.Collections;
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
	 * Property for the prompt/placeholder that is displayed when the editor is empty.
	 */
	final StringProperty promptTextProperty = new SimpleStringProperty("");
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
	 */
	void bind() {
		Platform.runLater(() -> {
			// get some references to objects and ethods
			JSObject win = (JSObject) engine.executeScript("window");

			// set  reference to bridge
			win.setMember("bridge", JavaScriptBridge.this);

			// create method that logs to Java
			Object ret = callScript("(name,level,msg) => window.bridge.logJSMessage(name,level,msg)");
			LangUtil.check(ret instanceof JSObject, "error creating logging method");
			JSObject log = (JSObject) ret;

			// create editor instance
			String name = "@" + Integer.toHexString(System.identityHashCode(this));
			String container = "container";
			ret = win.call("createTextEditor", name, container);
			LangUtil.check(ret instanceof JSObject, "editor construction failed");
			jsEditor = (JSObject) ret;

			// bind properties
			readOnlyProperty.addListener((v, ov, nv) ->
					Platform.runLater(() -> call("setReadOnly", nv))
			);
			promptTextProperty.addListener((v, ov, nv) ->
					Platform.runLater(() -> call("setPromptText", nv))
			);

			// sync properties
			call("setReadOnly", readOnlyProperty.get());
			call("setPromptText", promptTextProperty.get());

			// mark editor as ready for use
			editorReadyProperty.set(true);
		});
	}

	public void logJSMessage(String name, Integer level, String message) {
		Level logLevel;
		switch (level) {
			case 3:
				logLevel = Level.SEVERE;
				break;
			case 2:
				logLevel = Level.WARNING;
				break;
			case 1:
				logLevel = Level.INFO;
				break;
			case 0:
			default:
				logLevel = Level.FINE;
				break;
		}
		LOG.log(logLevel, () -> String.format("[%s] %s", name, message));
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
	 * Paste content of system clipboard. Called from JavaScript.
	 */
	public void paste() {
		String content = (String) Clipboard.getSystemClipboard().getContent(DataFormat.PLAIN_TEXT);
		if (content == null) {
			LOG.fine("paste() called while clipboard is empty - ignoring");
			return;
		}

		LOG.fine(() -> String.format("paste(): '%s'", content));
		call("replaceSelection", content);
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

	/**
	 * Copy to system clipboard. Called from JavaScript.
	 *
	 * @param arg the data to be copied to the clipboard
	 */
	public void copy(JSObject arg) {
		copyToSystemClipboard("copy()", arg);
	}

	/**
	 * Cut text to system clipboard. Called from JavaScript.
	 *
	 * @param arg the data to be cut to the clipboard ("cut" = copy to clipboard and
	 *            clear selection)
	 */
	public void cut(JSObject arg) {
		// 1. paste the empty string to remove current selection
		call("replaceSelection", "");

		// 2. copy content to system clipboard
		copyToSystemClipboard("cut()", arg);
	}

	/**
	 * Copy JavaScript object to system clipboard.
	 *
	 * @param task task name for logging (usually "copy" or "cut")
	 * @param arg  the object to copy to clipboard should have the attributes "format" and "content"
	 */
	private void copyToSystemClipboard(String task, JSObject arg) {
		String format = String.valueOf(arg.getMember("format"));
		Object content = arg.getMember("content");
		switch (format) {
			case "text":
				LOG.fine(() -> String.format("copyToSystemClipboard, task='%s'  - unknown format '%s'", task, format));
				Clipboard.getSystemClipboard()
						.setContent(Collections.singletonMap(DataFormat.PLAIN_TEXT, String.valueOf(content)));
				break;
			default:
				LOG.warning(() -> String.format("%s: unknown format %s", task, format));
				break;
		}
	}

	/**
	 * Get content of system clipboard. Called by JavaScript.
	 *
	 * @return (plaintext) content of system clipboard
	 */
	public String getClipboardContent() {
		Object content = Clipboard.getSystemClipboard().getContent(DataFormat.PLAIN_TEXT);
		return content == null ? null : String.valueOf(content);
	}

}