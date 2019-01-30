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

package com.dua3.fx.application;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import com.dua3.fx.util.AboutDialog;
import com.dua3.fx.util.Dialogs;
import com.dua3.utility.lang.LangUtil;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

public abstract class FxController<A extends FxApplication<A, C>, C extends FxController<A, C>> {

	// - static -

	/** Logger */
	protected static final Logger LOG = Logger.getLogger(FxController.class.getSimpleName());

	/** The application instance. */
	private A app;

	/** The void URI that represents "no document". */
	private static final URI VOID_URI = URI.create("");

	/** Preferece: last document. */
	protected static final String PREF_DOCUMENT = "document_uri";
	
	/** The URI of the currently opened document. */
	protected ObjectProperty<URI> documentProperty = new SimpleObjectProperty<URI>(VOID_URI);
	
	/** The URI of the currently opened document. */
	protected BooleanProperty dirtyProperty = new SimpleBooleanProperty(false);
	
	/**
	 * The Default constructor. Just declared here to reduce visibility.
	 */
	protected FxController() {
	}

	/**
	 * Set application instance.
	 * 
	 * This method must be called exactly once!
	 * 
	 * @param app
	 *  the application instance
	 */
	void setApp(A app) {
		LangUtil.check(this.app==null, "app instance was already set");
		this.app = Objects.requireNonNull(app, "app instance must not be null");
		init(app);
	}

	protected void init(A app) {
		// nop
	}

	/**
	 * Get the App instance.
	 * 
	 * @return 
	 *  the App instance
	 * @throws IllegalStateException
	 *  if called before the App instance was set
	 */
	public A getApp() {
		if ( app == null ) {
			throw new IllegalStateException("App instance was not yet set");
		}
		return app;
	}
	/**
	 * Request application close as if the close-window-button was clicked.
	 * Called from FXML.
	 */
	@FXML
	public void closeApplication() {
		// handle dirty state
		if (isDirty()) {
			AtomicBoolean goOn = new AtomicBoolean(false);
			Dialogs.confirmation()
			.header("Save changes?")
			.text(getDocumentName())
			.buttons(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL)
			.defaultButton(ButtonType.YES)
			.showAndWait()
			.ifPresent(btn -> {
				if (btn==ButtonType.YES) {
					goOn.set(save()); // only continue if save was successful
				}
				if (btn==ButtonType.NO) {
					goOn.set(true);   // don't save, just go on
				}
			});
			
			if (!goOn.get()) {
				LOG.fine("close aborted because of dirty state");
				return;
			}
		}
		
		app.close();
	}
	
	/**
	 * Get current document.
	 * 
	 * @return
	 *  URI of the current document
	 */
	public URI getDocument() {
		return documentProperty.get();
	}
	
	/**
	 * Set current document.
	 * 
	 * @param uri
	 *  URI of the document
	 */
	protected void setDocument(URI uri) {
		documentProperty.set(uri);
		setPreferenceOptional(PREF_DOCUMENT, uri.toString());
	}
	
	/**
	 * Set current document.
	 * 
	 * @param url
	 *  URL of the document
	 */
	protected void setDocument(URL url) {
		try {
			documentProperty.set(url.toURI());
		} catch (URISyntaxException e) {
			documentProperty.set(VOID_URI);
			LOG.warning("could not set document URL");
		}
	}
	
	/**
	 * Clear the document, i.e. inform application that no document is loaded.
	 */
	protected void clearDocument() {
		documentProperty.set(VOID_URI);
	}
	
	/**
	 * Get the current document's name.
	 * @return
	 *  name of the current document, or "" if no document loaded
	 */
	public String getDocumentName() {
		return getDisplayName(getDocument());
	}
	
	/**
	 * Get document property.
	 * 
	 * @return
	 *  read only document property
	 */
	public ReadOnlyObjectProperty<URI> documentProperty() {
		return documentProperty;
	}
	
	/**
	 * Get current dirty state.
	 * 
	 * @return
	 *  dirty state of the current document
	 */
	public boolean isDirty() {
		return dirtyProperty.get();
	}
	
	/**
	 * Set current document's dirty state.
	 * 
	 * @param flag
	 *  dirty state of the document
	 */
	protected void setDirty(boolean flag) {
		dirtyProperty.set(flag);
	}
	
	/**
	 * Get dirty property.
	 * 
	 * @return
	 *  read only dirty property
	 */
	public ReadOnlyBooleanProperty dirtyProperty() {
		return dirtyProperty;
	}
	
	/** 
	 * Test if document is set.
	 * @return
	 *  true, if document is set
	 */
	public boolean hasDocument() {
		return documentProperty.get() != VOID_URI;
	}

	protected boolean handleDirtyState() {
		if (isDirty()) {
			AtomicBoolean goOn = new AtomicBoolean(false);
			Dialogs.confirmation()
			.header("Save changes?")
			.buttons(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL)
			.defaultButton(ButtonType.YES)
			.showAndWait()
			.ifPresent(btn -> {
				if (btn==ButtonType.YES) {
					goOn.set(save()); // only continue if save was successful
				}
				if (btn==ButtonType.NO) {
					goOn.set(true);   // don't save, just go on
				}
			});
			
			if (!goOn.get()) {
				LOG.fine("open aborted because of dirty state");
				return false;
			}
		}
		return true;
	}

	@FXML
	public boolean newDocument() {
		// handle dirty state
		if (!handleDirtyState()) {
			LOG.fine("open aborted because of dirty state");
			return false;			
		}
		
		clearDocument();
		try {
			createDocument();
			return true;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "error creating document", e);
			Dialogs.error()
			.title("Error")
			.header("Could not create a new document.")
			.text(e.getMessage())
			.build()
			.showAndWait();
			return false;
		}
	}
	
	@FXML
	protected boolean open() {
		// handle dirty state
		if (!handleDirtyState()) {
			LOG.fine("open aborted because of dirty state");
			return false;			
		}
		
		// choose file to open
		File initialDir = null;
		String initialFileName = "";
		try {
			if (hasDocument()) {
				initialDir = Paths.get(getDocument()).getParent().toFile();
			} else {
				String lastDocument = getPreference(PREF_DOCUMENT, "");
				if (lastDocument.isBlank()) {
					initialDir = new File(System.getProperty("user.home"));
				} else {
					Path path = Paths.get(URI.create(lastDocument));
					initialDir = path.getParent().toFile();
					initialFileName = path.getFileName().toString();
				}
			}
		} catch (IllegalStateException e) {
			// might for example be thrown by URI.create()
			LOG.log(Level.WARNING, "could not determine initial folder", e);
		}
		
		if (initialDir == null || !initialDir.isDirectory()) {
			initialDir = new File(System.getProperty("user.home"));
		}
		
		Optional<File> file = Dialogs
				.chooseFile()
				.initialDir(initialDir)
				.initialFileName(initialFileName)
				.filter(openFilters())
				.showOpenDialog(getApp().getStage());
		
		if (file.isEmpty()) {
			LOG.fine("open(): no file was chosen");
			return false;
		}

		// open the document and handle errors
		URI uri = file.get().toURI();
		try {
			openDocument(uri);
			setDocument(uri);
			return true;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "error opening document", e);
			Dialogs.error()
			.title("Error")
			.header("'%s' could not be opened.", getDisplayName(uri))
			.text(e.getMessage())
			.build()
			.showAndWait();
			return false;
		}
	}
	
	@FXML
	protected boolean save() {
		if (!hasDocument()) {
			LOG.fine("save: no document set, delegating to saveAs()");
			return saveAs();
		}
		
		return saveDocumentAndHandleErrors(getDocument());
	}

	protected List<FileChooser.ExtensionFilter> openFilters() {
		List<FileChooser.ExtensionFilter> filters = new LinkedList<>();
		filters.add(new FileChooser.ExtensionFilter("all files", "*.*"));
		return filters;
	}
	
	protected List<FileChooser.ExtensionFilter> saveFilters() {
		List<FileChooser.ExtensionFilter> filters = new LinkedList<>();
		filters.add(new FileChooser.ExtensionFilter("all files", "*.*"));
		return filters;
	}
	
	@FXML
	protected boolean saveAs() {
		// choose file to open
		File initialDir = null;
		String initialFileName = "";
		try {
			if (hasDocument()) {
				initialDir = Paths.get(getDocument()).getParent().toFile();
			} else {
				String lastDocument = getPreference(PREF_DOCUMENT, "");
				if (lastDocument.isBlank()) {
					initialDir = new File(System.getProperty("user.home"));
				} else {
					Path path = Paths.get(URI.create(lastDocument));
					initialDir = path.getParent().toFile();
					initialFileName = path.getFileName().toString();
				}
			}
		} catch (IllegalStateException e) {
			// might for example be thrown by URI.create()
			LOG.log(Level.WARNING, "could not determine initial folder", e);
		}
		
		if (initialDir == null || !initialDir.isDirectory()) {
			initialDir = new File(System.getProperty("user.home"));
		}
		
		Optional<File> file = Dialogs
				.chooseFile()
				.initialDir(initialDir)
				.initialFileName(initialFileName)
				.filter(saveFilters())
				.showSaveDialog(getApp().getStage());

		if (file.isEmpty()) {
			LOG.fine("saveAs(): no file was chosen");
			return false;
		}
		
		// save document content
		return saveDocumentAndHandleErrors(file.get().toURI());
	}

	private boolean saveDocumentAndHandleErrors(URI uri) {
		try {
			saveDocument(uri);
			setDocument(uri);			
			return true;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "error saving document", e);
			Dialogs.error()
			.title("Error")
			.header("'%s' could not be saved.", getDisplayName(uri))
			.text("%s: %s", e.getClass().getSimpleName(), e.getMessage())
			.build()
			.showAndWait();
			return false;
		}		
	}
	
	@SuppressWarnings("static-method")
	protected String getDisplayName(URI uri) {
		return uri != VOID_URI ? uri.toString() : "<unnamed>";
	}
	
	@SuppressWarnings("static-method")
	protected void createDocument() {
		throw new UnsupportedOperationException("not implemented");
	}

	@SuppressWarnings({ "static-method", "unused" })
	protected void openDocument(URI uri) throws IOException {
		throw new UnsupportedOperationException("not implemented");
	}

	@SuppressWarnings({ "static-method", "unused" })
	protected void saveDocument(URI uri) throws IOException {
		throw new UnsupportedOperationException("not implemented");
	}

	boolean hasPreferences() {
		return getApp().hasPreferences();
	}
	
	protected Preferences getPreferences() {
		return getApp().getPreferences();
	}
	
	protected boolean setPreferenceOptional(String key, String value) {
		if (hasPreferences()) {
			LOG.fine(() -> String.format("setting preference '%s'", key));
			setPreference(key, value);
			return true;
		}
		LOG.fine(() -> String.format("not setting preference '%s': preferences not initialised", key));
		return false;
	}

	protected void setPreference(String key, String value) {
		getPreferences().put(key, value);
	}
	
	protected String getPreference(String key, String def) {
		return hasPreferences() ? getPreferences().get(key, def) : def;
	}
	
	protected AboutDialog createAboutDialog() {		
		try {
			return Dialogs.about()
				.title("About")
				.name(getApp().getApplicationName())
				.version(getApp().getVersionString())
				.copyright(getApp().getCopyright())
				.mail(
						getApp().getContactMail(), 
					String.format(
							"mailto:%s?subject=%s", 
							getApp().getContactMail(),
							URLEncoder.encode(getApp().getApplicationName()+" "+getApp().getVersionString(), StandardCharsets.UTF_8.name())))
				.build();
		} catch (UnsupportedEncodingException e) {
			LOG.log(Level.WARNING, "unsupported encoding", e);
			throw new IllegalStateException(e);
		}
	}
}
