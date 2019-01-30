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

package com.dua3.fx.document;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import com.dua3.fx.document.intern.JavaScriptBridge;

public abstract class DocumentPane extends BorderPane {
    private static final Logger LOG = Logger.getLogger(DocumentPane.class.getSimpleName());

	@FXML
	protected WebView webview;

	/** Bridge for interfacing with JS code */
	private JavaScriptBridge bridge = null;

	protected JavaScriptBridge getBridge() {		
		return bridge;
	}
	
	/**
	 * Constructor.
	 */
	protected DocumentPane() {
		LOG.fine(() -> "DocumentPane()");
		
		// load FXML
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("document_pane.fxml"));
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
/*
		// inject JavaScript bridge after loading
		engine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
			if (newState == State.SUCCEEDED) {
				LOG.info("[webengine] " + newState + " - binding bridge");
				bridge.bind();
			}
		});

		// load viewer
		engine.load(getClass().getResource(getClass().getResource(document_pane.html)).toString());
		LOG.fine("viewer component created");
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
 */
	}
	/*
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
	*/
}
