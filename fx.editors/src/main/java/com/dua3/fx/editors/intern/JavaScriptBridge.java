package com.dua3.fx.editors.intern;

import java.util.Collections;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dua3.fx.editors.CodeEditor;

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
		this.logger = Logger.getLogger(CodeEditor.class.getSimpleName()+"[JS]");
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
				Object initState = engine.executeScript("editor_initialised");
				if (!Boolean.TRUE.equals(initState)) {
					throw new IllegalStateException("JS error during initialization of editor component");
				}
				
				// bind properties
				readOnlyProperty.addListener((v, ov, nv) -> {
					Platform.runLater(() -> engine.executeScript("jSetReadOnly("+nv+");"));
				});
				promptTextProperty.addListener((v, ov, nv) -> {
					Platform.runLater(() -> engine.executeScript("jSetPromptText(\""+escape(nv)+"\");"));
				});
				
				// sync properties
				String script = String.format(
					  "jSetReadOnly(%s);%n"
					+ "jSetPromptText(\"%s\");%n",
					Boolean.toString(readOnlyProperty.get()),
					escape(promptTextProperty.get()));				
				engine.executeScript(script);

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
		String script = "jReplaceSelection('" + escape(content) + "');";
		executeScript(script);
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
		String script = "jReplaceSelection('');";
		executeScript(script);

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