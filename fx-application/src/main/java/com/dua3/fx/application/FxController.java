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

import com.dua3.fx.util.Dialogs;
import com.dua3.fx.util.FxTask;
import com.dua3.fx.util.controls.AboutDialog;
import com.dua3.utility.lang.LangUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
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

public abstract class FxController<A extends FxApplication<A, C>, C extends FxController<A, C>>  {

	// - static -

	/** Logger */
	protected static final Logger LOG = Logger.getLogger(FxController.class.getSimpleName());
	public static final String TITLE_ERROR = "Error";
	public static final File USER_HOME = new File(System.getProperty("user.home"));

	/** The application instance. */
	private A app;

	/** The list of current tasks. */
	protected final ObservableList<FxTask<?>> tasks = FXCollections.observableArrayList();
	
	/** The "all files" filter. */
	protected static final ExtensionFilter EXTENSIONFILTER_ALL_FILES = new FileChooser.ExtensionFilter("all files", "*.*");
	
	/** Preferece: last document. */
	protected static final String PREF_DOCUMENT = "document_uri";
	
	/** The URI of the currently opened document. */
	protected ObjectProperty<FxDocument> currentDocumentProperty = new SimpleObjectProperty<>();
	
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
	
	public abstract List<? extends FxDocument> dirtyDocuments();
	
	/**
	 * Request application close as if the close-window-button was clicked.
	 * Called from FXML.
	 */
	@FXML
	public void closeApplication() {
		// handle dirty state
		if (!handleDirtyState()) {
			LOG.fine("close aborted because of dirty state");
			return;
		}
		app.close();
	}

	protected boolean handleDirtyState() {
		boolean rc = true;
		List<? extends FxDocument> dirtyList = dirtyDocuments();
		if (!dirtyList.isEmpty()) {
			AtomicBoolean goOn = new AtomicBoolean(false);
			Dialogs.confirmation()
			.header("Save changes?")
			.text("%s",
					String.join(
					"\n",
					dirtyList.stream().map(Object::toString).toArray(String[]::new)
			))
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
			rc = goOn.get();
		}
		return rc;
	}
	
	/**
	 * Get current document.
	 * 
	 * @return
	 *  URI of the current document
	 */
	public FxDocument getCurrentDocument() {
		return currentDocumentProperty.get();
	}
	
	/**
	 * Set current document.
	 * 
	 * @param document
	 *  the document
	 */
	protected void setCurrentDocument(FxDocument document) {		
		currentDocumentProperty.set(document);
		setPreferenceOptional(PREF_DOCUMENT, document.getLocation().toString());
		LOG.fine(() -> "current document: "+document);
	}
		
	/**
	 * Clear the document, i.e. inform application that no document is loaded.
	 */
	protected void clearDocument() {
		currentDocumentProperty.set(null);
	}
	
	/** 
	 * Test if document is set.
	 * @return
	 *  true, if document is set
	 */
	public boolean hasCurrentDocument() {
		return currentDocumentProperty.get()!=null;
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
			.title(TITLE_ERROR)
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
		Path parent = null;
		String initialFileName = "";
		try {
			if (hasCurrentDocument() && getCurrentDocument().hasLocation()) {
				parent = getCurrentDocument().getPath().getParent();
			} else {
				String lastDocument = getPreference(PREF_DOCUMENT, "");
				if (lastDocument.isBlank()) {
					parent = USER_HOME.toPath();
				} else {
					Path path = Paths.get(URI.create(lastDocument));
					parent = path.getParent();
					initialFileName = Objects.toString(path.getFileName(), "");
				}
			}
		} catch (IllegalStateException e) {
			// might for example be thrown by URI.create()
			LOG.log(Level.WARNING, "could not determine initial folder", e);
		}
		File initialDir = parent != null ? parent.toFile() : null;
		
		if (initialDir == null || !initialDir.isDirectory()) {
			initialDir = USER_HOME;
		}
		
		Optional<File> file = Dialogs
				.chooseFile()
				.initialDir(initialDir)
				.initialFileName(initialFileName)
				.filter(openFilters())
				.selectedFilter(selectedOpenFilter())
				.showOpenDialog(getApp().getStage());
		
		if (file.isEmpty()) {
			LOG.fine("open(): no file was chosen");
			return false;
		}

		// open the document and handle errors
		return open(file.get().toURI());
	}

	protected boolean open(URI uri) {
		try {
			setCurrentDocument(loadDocument(uri));
			return true;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "error opening document", e);
			Dialogs.error()
			.title(TITLE_ERROR)
			.header("'%s' could not be opened.", getDisplayName(uri))
					.text(Objects.toString(e.getMessage()))
			.build()
			.showAndWait();
			return false;
		}
	}

	protected ExtensionFilter selectedOpenFilter() {
		return null;
	}

	protected ExtensionFilter selectedSaveFilter() {
		return null;
	}

	@FXML
	protected boolean save() {
		if (!hasCurrentDocument()) {
			LOG.info("no document; not saving");
			return false;
		}

		if (!getCurrentDocument().hasLocation()) {
			LOG.fine("save: no URI set, delegating to saveAs()");
			return saveAs();
		}
		
		return saveDocumentAndHandleErrors(getCurrentDocument());
	}

	private boolean saveDocumentAndHandleErrors(FxDocument document) {
		return saveDocumentAndHandleErrors(document, document.getLocation());
	}

	protected List<FileChooser.ExtensionFilter> openFilters() {
		List<FileChooser.ExtensionFilter> filters = new LinkedList<>();
		filters.add(EXTENSIONFILTER_ALL_FILES);
		return filters;
	}
	
	protected List<FileChooser.ExtensionFilter> saveFilters() {
		List<FileChooser.ExtensionFilter> filters = new LinkedList<>();
		filters.add(EXTENSIONFILTER_ALL_FILES);
		return filters;
	}
	
	@FXML
	protected boolean saveAs() {
		if (!hasCurrentDocument()) {
			LOG.info("no document; not saving");
			return false;
		}
		
		Path parent = null;
		String initialFileName = "";
		FxDocument document = getCurrentDocument();
		try {
			if (document.hasLocation()) {
				document = getCurrentDocument();
				parent = document.getPath().getParent();
			} else {
				String lastDocument = getPreference(PREF_DOCUMENT, "");
				if (lastDocument.isBlank()) {
					parent = USER_HOME.toPath();
				} else {
					Path path = Paths.get(URI.create(lastDocument));
					parent = path.getParent();
					initialFileName = Objects.toString(path.getFileName(), "");
				}
			}
		} catch (IllegalStateException e) {
			// might for example be thrown by URI.create()
			LOG.log(Level.WARNING, "could not determine initial folder", e);
		}
		
		File  initialDir = parent!=null ? parent.toFile() :  null;

		if (initialDir == null || !initialDir.isDirectory()) {
			initialDir = USER_HOME;
		}
		
		Optional<File> file = Dialogs
				.chooseFile()
				.initialDir(initialDir)
				.initialFileName(initialFileName)
				.filter(saveFilters())
				.selectedFilter(selectedSaveFilter())
				.showSaveDialog(getApp().getStage());

		if (file.isEmpty()) {
			LOG.fine("saveAs(): no file was chosen");
			return false;
		}
		
		// save document content
		return saveDocumentAndHandleErrors(document, file.get().toURI());
	}

	private boolean saveDocumentAndHandleErrors(FxDocument document, URI uri) {
		try {
			document.saveAs(uri);
			return true;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "error saving document", e);
			Dialogs.error()
			.title(TITLE_ERROR)
			.header("'%s' could not be saved.", getDisplayName(uri))
			.text("%s: %s", e.getClass().getSimpleName(), e.getMessage())
			.build()
			.showAndWait();
			return false;
		}		
	}
	
	protected String getDisplayName(URI uri) {
		return uri.toString();
	}
	
	@SuppressWarnings("static-method")
	protected void createDocument() {
		throw new UnsupportedOperationException("not implemented");
	}

	@SuppressWarnings({ "static-method", "unused" })
	protected FxDocument loadDocument(URI uri) throws IOException {
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
	
	public void setStatusText(String s) {
		LOG.fine(() -> "status: "+s);
	}
	
}
