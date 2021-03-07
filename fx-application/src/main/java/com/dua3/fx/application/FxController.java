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
import com.dua3.utility.data.Pair;
import com.dua3.utility.lang.LangUtil;
import com.dua3.utility.text.TextUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.dua3.fx.application.FxDocument.VOID_URI;

public abstract class FxController<A extends FxApplication<A, C>, C extends FxController<A, C>>  {

	// - static -

	/** Logger */
	protected static final Logger LOG = Logger.getLogger(FxController.class.getName());
	
	/** The application instance. */
	private A app;

	@FXML
	protected URL location;

	@FXML
	protected ResourceBundle resources;
	
	/** The list of current tasks. */
	protected final ObservableList<FxTask<?>> tasks = FXCollections.observableArrayList();

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
	public void closeApplicationWindow() {
		// handle dirty state
		if (!handleDirtyState()) {
			LOG.fine("close aborted because of dirty state");
			return;
		}
		app.closeApplicationWindow();
	}

	/**
	 * Check for changes. If unsaved changes are detected, display a dialog with the following options (this is done 
	 * for each unsaved document containing changes):
	 * <ul>
	 *     <li> Save the current document
	 *     <li> Do not save the current document
	 *     <li> Cancel
	 * </ul>
	 * If the user selects "save", the current document is saved before the method returns.
	 * 
	 * @return 
	 * 	true, if either "save" (in which case the document is automatically save) or "don't save are selected
	 * 	false, if the dialog was cancelled
	 */
	protected boolean handleDirtyState() {
		boolean rc = true;
		List<? extends FxDocument> dirtyList = dirtyDocuments();

		AtomicBoolean goOn = new AtomicBoolean(false);
		switch (dirtyList.size()) {
			case 0:
				goOn.set(true);
				break;
				
			case 1:	{
				FxDocument doc = dirtyList.get(0);

				String header;
				if (!doc.hasLocation()) {
					header = resources.getString("fx.application.message.unsaved_changes_single_document_untitled");
				} else {
					header = getApp().getMessage(
							"fx.application.message.unsaved_changes_single_document",
							Pair.of("document", dirtyList.get(0).getName())
					);
				}

				ButtonType bttSave = new ButtonType(resources.getString("fx.application.button.save"), ButtonBar.ButtonData.YES);
				ButtonType bttDontSave = new ButtonType(resources.getString("fx.application.button.dont_save"), ButtonBar.ButtonData.NO);
				
				Dialogs.confirmation(getApp().getStage())
						.header(header)
						.text(resources.getString("fx.application.message.changes_will_be_lost"))
						.buttons(bttDontSave, bttSave, ButtonType.CANCEL)
						.showAndWait()
						.ifPresent(btn -> {
							if (btn == bttSave) {
								goOn.set(save()); // only continue if save was successful
							}
							if (btn == bttDontSave) {
								goOn.set(true);   // don't save, just go on
							}
						});
				break;
			}

			default: {
				String header = getApp().getMessage(
						"fx.application.message.unsaved_changes_multiple_documents",
						Pair.of("count", String.valueOf(dirtyList.size()))
				);

				Dialogs.confirmation(getApp().getStage())
						.header(header)
						.text(resources.getString("fx.application.message.continue_without_saving"))
						.buttons(ButtonType.YES, ButtonType.CANCEL)
						.defaultButton(ButtonType.CANCEL)
						.showAndWait()
						.ifPresent(btn -> {
							goOn.set(btn == ButtonType.YES); // only continue if "YES" was clicked
						});
			}
		}
		
		if (!dirtyList.isEmpty()) {
			rc = goOn.get();
		}
		return rc;
	}

	/**
	 * Get current document.
	 * 
	 * @return
	 *  the current document or {@code null}
	 */
	public FxDocument getCurrentDocument() {
		return currentDocumentProperty.get();
	}

	/**
	 * Get current document locationn.
	 *
	 * @return
	 *  URI of the current document or {@link FxDocument#VOID_URI}
	 */
	public URI getCurrentDocumentLocation() {
		FxDocument doc = getCurrentDocument();
		return doc != null ? doc.getLocation() : VOID_URI;
	}

	/**
	 * Set current document.
	 * 
	 * @param document
	 *  the document
	 */
	protected void setCurrentDocument(FxDocument document) {		
		currentDocumentProperty.set(document);
		onDocumentUriChanged(document.getLocation());
	}

	/**
	 * Called when the location of the main document changes. Updates the last document in the preferences.
	 * Implementing classes can override this method to implement a recently used documents list.
	 * @param uri the document's URI
	 */
	protected void onDocumentUriChanged(URI uri) {
		if (VOID_URI.equals(uri)) {
			return;
		}
		
		getApp().setPreferenceOptional(PREF_DOCUMENT, uri.toString());
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
			getApp().showErrorDialog(resources.getString("fx.application.dialog.error.new_document"), e.getLocalizedMessage());
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

		FxDocument document = getCurrentDocument();
		File initialDir = initialDir(document);
		
		if (initialDir == null || !initialDir.isDirectory()) {
			initialDir = getApp().getUserHome();
		}
		
		Optional<File> file = Dialogs
				.chooseFile()
				.initialDir(initialDir)
				.initialFileName("")
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
			getApp().showErrorDialog(
					String.format("%s '%s'", resources.getString("fx.application.dialog.error.open_document"), getDisplayName(uri)),
					Objects.toString(e.getLocalizedMessage())
			);
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
		filters.add(getApp().getExtensionfilterAllFiles());
		return filters;
	}
	
	protected List<FileChooser.ExtensionFilter> saveFilters() {
		List<FileChooser.ExtensionFilter> filters = new LinkedList<>();
		filters.add(getApp().getExtensionfilterAllFiles());
		return filters;
	}
	
	@FXML
	protected boolean saveAs() {
		if (!hasCurrentDocument()) {
			LOG.info("no document; not saving");
			return false;
		}
		
		FxDocument document = getCurrentDocument();
		File initialDir = initialDir(document);

		Optional<File> file = Dialogs
				.chooseFile()
				.initialDir(initialDir)
				.initialFileName("")
				.filter(saveFilters())
				.selectedFilter(selectedSaveFilter())
				.showSaveDialog(getApp().getStage());

		if (file.isEmpty()) {
			LOG.fine("saveAs(): no file was chosen");
			return false;
		}
		
		// save document content
		boolean rc = saveDocumentAndHandleErrors(document, file.get().toURI());
		
		if (rc) {
			setCurrentDocument(document);
		}
		
		return rc;
	}

	/**
	 * Determine the parent folder to set for open/save dialogs.
	 * @param document the current document
	 * @return the initial folder to set
	 */
	private File initialDir(FxDocument document) {
		if (document==null) {
			return getApp().getUserHome();
		}
		
		Path parent = null;
		try {
			if (document.hasLocation()) {
				parent = document.getPath().getParent();
				LOG.fine("initialDir() - using parent fokder of current document as parent: "+parent);
			} else {
				String lastDocument = getApp().getPreference(PREF_DOCUMENT, "");
				if (lastDocument.isBlank()) {
					parent = getApp().getUserHome().toPath();
					LOG.fine("initialDir() - last document location not set, using user home as parent: "+parent);
				} else {
					try {
						Path path = Paths.get(URI.create(lastDocument));
						parent = path.getParent();
						LOG.fine("initialDir() - using last document location as parent: " + parent);
					} catch (IllegalArgumentException|NullPointerException e) {
						LOG.log(Level.WARNING, "could not retrieve last document location", e);
						parent = app.getUserHome().toPath();
					}
				}
			}
		} catch (IllegalStateException e) {
			// might for example be thrown by URI.create()
			LOG.log(Level.WARNING, "initialDir() - could not determine initial folder", e);
		}

		File  initialDir = parent!=null ? parent.toFile() :  null;

		if (initialDir == null || !initialDir.isDirectory()) {
			LOG.log(Level.WARNING, "initialDir() - initial directory invalid, using user home instead: "+initialDir);
			initialDir = getApp().getUserHome();
		}
		return initialDir;
	}

	private boolean saveDocumentAndHandleErrors(FxDocument document, URI uri) {
		try {
			document.saveAs(uri);
			return true;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "error saving document", e);
			getApp().showErrorDialog(
				String.format("%s '%s'" , resources.getString("fx.application.dialog.error.save_document"), getDisplayName(uri)),
				String.format("%s: %s", e.getClass().getSimpleName(), e.getLocalizedMessage())
			);
			return false;
		}		
	}

	protected String getDisplayName(URI uri) {
		return URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8);
	}
	
	@SuppressWarnings("static-method")
	protected void createDocument() {
		throw new UnsupportedOperationException("not implemented");
	}

	@SuppressWarnings({ "static-method", "unused" })
	protected FxDocument loadDocument(URI uri) throws IOException {
		throw new UnsupportedOperationException("not implemented");
	}
	
	public void setStatusText(String s) {
		LOG.fine(() -> "status: "+s);
	}

	public File getCurrentDir() {
		if (hasCurrentDocument() && getCurrentDocument().hasLocation()) {
			Path parent = getCurrentDocument().getPath().getParent();
			if (parent != null) {
				try {
					return parent.toFile();
				} catch (UnsupportedOperationException e) {
					LOG.log(Level.WARNING, "cannot get current directory, using home");
				}
			}
		}
		return getApp().getUserHome();
	}

}
