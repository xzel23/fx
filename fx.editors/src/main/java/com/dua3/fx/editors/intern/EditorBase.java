package com.dua3.fx.editors.intern;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dua3.fx.util.Dialogs;
import com.dua3.fx.util.WebViews;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public abstract class EditorBase extends BorderPane {
    private static final Logger LOG = Logger.getLogger(EditorBase.class.getSimpleName());

    protected static String escape(String text) {
    	return JavaScriptBridge.escape(text);
    }
    
	@FXML
	protected WebView webview;

	/** Bridge for interfacing with JS code */
	private JavaScriptBridge bridge = null;

	protected JavaScriptBridge getBridge() {		
		return bridge;
	}
	
	/**
	 * Constructor.
	 * @param fxml
	 *  the FXML resource to load the user interface definition from (filename)
	 * @param html
	 *  the HTML resource to load (filename)
	 */
	protected EditorBase(String fxml, String html) {
		LOG.fine(() -> "creating Editor component");
		
		// load FXML
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
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
		engine.load(getClass().getResource(html).toString());
		LOG.fine("Editor component created");
	}

	public boolean waitReady(int millis) {
		final CountDownLatch latch = new CountDownLatch(1);

		ChangeListener<? super Boolean> cl = (ov,oldValue,newValue) -> {
			if (newValue.booleanValue()) {
				latch.countDown();
			}
		};

		bridge.editorReadyProperty.addListener(cl);

		long timeRemaining = millis;
		while (timeRemaining>0 && !bridge.editorReadyProperty.get()) {
			try {
				long s = System.currentTimeMillis();
				if (latch.await(millis, TimeUnit.MILLISECONDS)) {
					break;
				}
				long e = System.currentTimeMillis();
				long elapsed = Math.max(e-s, 0);
				timeRemaining = Math.max(0, timeRemaining-elapsed);
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
		String script = String.format("jSetContent('%s');", escape(text));
		bridge.executeScript(script);
	}

	public void setText(String text, String ext) {
		LOG.fine("setting editor content");
		String script = String.format("jSetContent('%s','%s');", escape(text), escape(ext));
		bridge.executeScript(script);
	}

	public String getText() {
		return  (String) bridge.callScript("jGetText()");
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
		promptTextProperty().set(text);;
	}
	
	public void cut() {
		bridge.executeScript("jCut();");
	}

	public void copy() {
		bridge.executeScript("jCopy();");
	}

	public void paste() {
		bridge.executeScript("jPaste();");
	}

	public int getLineCount() {
		return (int) bridge.callScript("jGetLineCount();");
	}
	
	public int getLineNumber() {
		return (int) bridge.callScript("jGetLineNumber();");
	}
	
	public String getLine(int idx) {
		return (String) bridge.callScript("jGetLine("+idx+");");
	}

	public void search() {
		bridge.executeScript("jSearch();");
	}
	
}
