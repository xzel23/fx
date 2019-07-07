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

package com.dua3.fx.editors.intern;

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
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bridge to provide a link between the JavaFX-component and the JavaScript code running
 * inside the WebView.
 */
public class JavaScriptBridge {
	/** The logger instance. */
	public static final Logger LOG = Logger.getLogger(JavaScriptBridge.class.getName());

	/** The WebEngine instance. */
	private final WebEngine engine;

	private JSObject jsEditor;

	/** Property that indicates whether current document is editable. */
	final BooleanProperty readOnlyProperty = new SimpleBooleanProperty(false);

	/** Property that indicates editor's dirty state. */
	final BooleanProperty dirtyProperty = new SimpleBooleanProperty(false);

	/** Property that indicates whether the editor is ready to be used. */
	final BooleanProperty editorReadyProperty = new SimpleBooleanProperty(false);

	/** Property for the prompt/placeholder that is displayed when the editor is empty. */
	final StringProperty promptTextProperty = new SimpleStringProperty("");

	/**
	 * Execute method of the JavaScript editor.
	 *
	 * @param method
	 * 	the method name
	 * @param args
	 *  the method arguments
	 * @return
	 *  the method return
	 */
	public Object call(String method, Object... args) {
		return PlatformHelper.runAndWait(() -> jsEditor.call(method, args));
	}

	/**
	 * Constructor.
	 *
	 * @param webView
	 *  the WebView instance where the editor is displayed
	 */
	public JavaScriptBridge(WebView webView) {
		this.engine = webView.getEngine();
	}

	/**
	 * Bind bridge to JavaScript editor instance.
	 */
	void bind() {
		Platform.runLater(() -> {
			log("setting bridge");
				// set reference to bridge in editor
				JSObject win = (JSObject) engine.executeScript("window");
				win.setMember("bridge", this);

				// make sure that the editor script has loaded and retrieve handle to JavaScript editor instance
				Object ret = engine.executeScript("window.editorInstance");
				LangUtil.check(ret instanceof JSObject, "editor script failed to load");
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
				log("bridge set.");
		});
	}

	/** The logging level. */
	private Level level = Level.INFO;
	
	/**
	 * Log a debug message. Called from JavaScript.
	 *
	 * @param msg the message
	 */
	public void log(String msg) {
		LOG.log(level, msg);
	}

	public void logs(Supplier<String> msgSupplier) {
		LOG.log(level, msgSupplier);
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
			log("paste() called while clipboard is empty - ignoring");
			return;
		}

		logs(() -> String.format("paste(): '%s'", content));
		call("replaceSelection", content);
	}

	/**
	 * Execute JavaScript code asynchronously.
	 * @param script
	 *  the code to execute
	 */
	public void executeScript(String script) {
		Platform.runLater(() -> {
			try {
				engine.executeScript(script);
			} catch (JSException e) {
				LOG.log(Level.WARNING, e.getMessage()+" - script: "+script);
				throw e;
			}
		});
	}

	/**
	 * Execute JavaScript code synchronously and return result.
	 * @param script
	 *  the code to execute
	 *  
	 *  @return
	 *   the value returned by the script
	 */
	public Object callScript(String script) {
		try {
			return engine.executeScript(script);
		} catch (JSException e) {
			LOG.log(Level.WARNING, e.getMessage()+" - script: "+script);
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
	 * @param task
	 *  task name for logging (usually "copy" or "cut")
	 * @param arg
	 *  the object to copy to clipboard should have the attributes "format" and "content"
	 */
	private void copyToSystemClipboard(String task, JSObject arg) {
		String format = String.valueOf(arg.getMember("format"));
		Object content = arg.getMember("content");
		switch (format) {
		case "text":
			Clipboard.getSystemClipboard()
					.setContent(Collections.singletonMap(DataFormat.PLAIN_TEXT, String.valueOf(content)));
			logs(() -> String.format("%s: plain text '%s'", task, content));
			break;
		default:
			logs(() -> String.format("%s: unkknown format %s", task, format));
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