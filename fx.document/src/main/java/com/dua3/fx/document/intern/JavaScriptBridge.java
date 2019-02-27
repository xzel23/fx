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

package com.dua3.fx.document.intern;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dua3.fx.document.DocumentPane;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

/**
 * Bridge to provide a link between the JavaFX-component and the JavaScript code running
 * inside the WebView.
 */
public class JavaScriptBridge {
	/** The logger instance. */
	private final Logger logger;
	
	/** The WebEngine instance. */
	private final WebEngine engine;
	
	/** Property that indicates whether current document is editable. */
	final BooleanProperty readOnlyProperty = new SimpleBooleanProperty(false);
	
	/** Property that indicates editor's dirty state. */
	final BooleanProperty dirtyProperty = new SimpleBooleanProperty(false);
	
	/** Property that indicates whether the editor is ready to be used. */
	final BooleanProperty editorReadyProperty = new SimpleBooleanProperty(false);

	/** Property for the prompt/placeholder that is displayed when the editor is empty. */
	final StringProperty promptTextProperty = new SimpleStringProperty("");
	
    /**
     * Backslash-escape a string.
     * @param s the string
     * @return the escaped string
     */
    public static String escape(String s) {
        StringBuilder out = new StringBuilder(16 + s.length() * 11 / 10);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (0<=c && c<127) {
                // ASCII characters
                switch (c) {
                case '\0':
                    out.append("\\u0000"); // "\0" might be ambiguous if followed by digits
                    break;
                case '\\':
                    out.append("\\\\");
                    break;
                case '\t':
                    out.append("\\t");
                    break;
                case '\b':
                    out.append("\\b");
                    break;
                case '\n':
                    out.append("\\n");
                    break;
                case '\r':
                    out.append("\\r");
                    break;
                case '\f':
                    out.append("\\f");
                    break;
                case '\'':
                    out.append("\\'");
                    break;
                case '\"':
                    out.append("\\\"");
                    break;
                default:
                    out.append(c);
                    break;
                }
            } else {
                // non-ASCII characters
                switch (Character.getType(c)) {
                // numbers: pass through
                case Character.DECIMAL_DIGIT_NUMBER:
                case Character.LETTER_NUMBER:
                case Character.OTHER_NUMBER:
                    out.append(c);
                    break;
                // letters: pass all non-modifying letters through
                case Character.UPPERCASE_LETTER:
                case Character.LOWERCASE_LETTER:
                case Character.OTHER_LETTER:
                case Character.TITLECASE_LETTER:
                    out.append(c);
                    break;
                // escape all remaining characters
                default:
                    out.append("\\u").append(String.format("%04X", (int) c));
                }
            }
        }
        return out.toString();
    }

	/**
	 * Constructor.
	 * 
	 * @param webView
	 *  the WebView instance where the editor is displayed
	 */
	public JavaScriptBridge(WebView webView) {
		this.logger = Logger.getLogger(DocumentPane.class.getSimpleName()+"[JS]");
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
				
				// assert that the init script has run to the last line without error
				// compare against BOOLEAN.TRUE because initState could be either Boolean or JSObject
				Object initState = engine.executeScript("initialised");
				if (!Boolean.TRUE.equals(initState)) {
					throw new IllegalStateException("JS error during initialization of component");
				}
				
				// bind properties
				
				// sync properties

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
		logger.log(level, msg);
	}

	public void logs(Supplier<String> msgSupplier) {
		logger.log(level, msgSupplier);
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
				logger.log(Level.WARNING, e.getMessage()+" - script: "+script);
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
			logger.log(Level.WARNING, e.getMessage()+" - script: "+script);
			throw e;
		}
	}

}