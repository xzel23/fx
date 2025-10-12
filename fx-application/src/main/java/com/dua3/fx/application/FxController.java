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

import com.dua3.utility.fx.controls.Dialogs;
import com.dua3.utility.i18n.I18N;
import com.dua3.utility.lang.LangUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract controller class for handling JavaFX applications with documents.
 *
 * @param <A> The type of the FxApplication
 * @param <C> The type of the FxController
 * @param <D> The type of the FxDocument
 */
public abstract class FxController<A extends FxApplication<A, C>, C extends FxController<A, C, D>, D extends FxDocument> {

    // - static -

    /**
     * Logger
     */
    protected static final Logger LOG = LogManager.getLogger(FxController.class);
    /**
     * Preference: last document.
     */
    protected static final String PREF_DOCUMENT = "document_uri";
    /**
     * The list of current tasks.
     */
    protected final ObservableList<Task<?>> tasks = FXCollections.observableArrayList();
    /**
     * The URI of the currently opened document.
     */
    protected final ObjectProperty<@Nullable D> currentDocumentProperty = new SimpleObjectProperty<>();

    /**
     * The {@link I18N} instance.
     */
    protected final I18N i18n = I18N.getInstance();

    /**
     * The application instance.
     */
    private @Nullable A app;

    /**
     * The Default constructor. Just declared here to reduce visibility.
     */
    protected FxController() {
    }

    /**
     * Retrieves a list of documents that have unsaved changes.
     *
     * @return a list of documents with unsaved changes.
     */
    public abstract List<D> dirtyDocuments();

    /**
     * Request application close as if the close-window-button was clicked.
     */
    public void closeApplicationWindow() {
        // handle dirty state
        if (!handleDirtyState()) {
            LOG.debug("close aborted because of dirty state");
            return;
        }
        assert app != null;
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
     * @return true, if either "save" (in which case the document is automatically saved) or "don't save are selected
     * false, if the dialog was canceled
     */
    protected boolean handleDirtyState() {
        boolean rc = true;
        List<? extends D> dirtyList = dirtyDocuments();

        AtomicBoolean goOn = new AtomicBoolean(false);
        switch (dirtyList.size()) {
            case 0 -> goOn.set(true);
            case 1 -> {
                D doc = dirtyList.getFirst();

                String header;
                if (!doc.hasLocation()) {
                    header = i18n.get("fx.application.message.unsaved.changes.untitled");
                } else {
                    header = i18n.format("fx.application.message.unsaved.changes.{0.document}", dirtyList.getFirst().getName());
                }

                ButtonType bttSave = new ButtonType(i18n.get("fx.application.button.save"), ButtonBar.ButtonData.YES);
                ButtonType bttDontSave = new ButtonType(i18n.get("fx.application.button.no.save"), ButtonBar.ButtonData.NO);

                Dialogs.alert(getApp().getStage(), AlertType.CONFIRMATION)
                        .header(header)
                        .text(i18n.get("fx.application.message.changes_will_be_lost"))
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
            }
            default -> {
                String header = i18n.format("fx.application.message.unsaved.changes.multiple.documents", String.valueOf(dirtyList.size()));

                Dialogs.alert(getApp().getStage(), AlertType.CONFIRMATION)
                        .header(header)
                        .text(i18n.get("fx.application.message.continue_without_saving"))
                        .buttons(ButtonType.YES, ButtonType.CANCEL)
                        .defaultButton(ButtonType.CANCEL)
                        .showAndWait()
                        .ifPresent(btn -> goOn.set(btn == ButtonType.YES)); // only continue if "YES" was clicked
            }
        }

        if (!dirtyList.isEmpty()) {
            rc = goOn.get();
        }

        return rc;
    }

    /**
     * Get current document location.
     *
     * @return URI of the current document
     */
    public Optional<URI> getCurrentDocumentLocation() {
        return getCurrentDocument().map(FxDocument::getLocation);
    }

    /**
     * Get current document.
     *
     * @return the current document
     */
    public Optional<D> getCurrentDocument() {
        return Optional.ofNullable(currentDocumentProperty.get());
    }

    /**
     * Set current document.
     *
     * @param document the document
     */
    protected void setCurrentDocument(D document) {
        currentDocumentProperty.set(document);
        onDocumentUriChanged(document.getLocation());
    }

    /**
     * Called when the location of the main document changes. Updates the last document in the preferences.
     * Implementing classes can override this method to implement a recently used documents list.
     *
     * @param uri the document's URI
     */
    protected void onDocumentUriChanged(URI uri) {
        if (FxDocument.VOID_URI.equals(uri)) {
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
     * Handles the creation of a new document by first processing any unsaved changes, then clearing the
     * current document, and finally creating a new one.
     *
     * @return true if the new document was successfully created, false otherwise
     */
    public boolean newDocument() {
        // handle dirty state
        if (!handleDirtyState()) {
            LOG.debug("new aborted because of dirty state");
            return false;
        }

        clearDocument();
        try {
            createDocument();
            return true;
        } catch (Exception e) {
            LOG.warn("error creating document", e);
            getApp().showErrorDialog(i18n.get("fx.application.dialog.error.new_document"), e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Opens a file dialog to allow the user to select a file to open.
     * <p>
     * The method first handles any unsaved changes by calling `handleDirtyState()`.
     * If there are unsaved changes and the user chooses to cancel, the open operation is aborted.
     * Otherwise, it attempts to determine an initial directory for the file chooser dialog based on the current document.
     * If no directory is found, the user's home directory is used. The file chooser dialog is then displayed to the user.
     * If the user selects a file, the document is opened.
     *
     * @return true if a file was successfully selected and opened, false otherwise
     */
    public boolean open() {
        // handle dirty state
        if (!handleDirtyState()) {
            LOG.debug("open aborted because of dirty state");
            return false;
        }

        Path initialDir = initialDir(getCurrentDocument().orElse(null));

        if (!Files.isDirectory(initialDir)) {
            initialDir = FxApplication.getUserHome();
        }

        Optional<Path> file = Dialogs
                .chooseFile(getApp().getStage())
                .initialDir(initialDir)
                .initialFileName("")
                .filter(openFilters())
                .selectedFilter(selectedOpenFilter())
                .showOpenDialog();

        if (file.isEmpty()) {
            LOG.debug("open(): no file was chosen");
            return false;
        }

        // open the document and handle errors
        return open(file.get().toUri());
    }

    /**
     * Opens a document from the specified URI and updates the document URI upon success.
     *
     * @param uri the URI from which to open the document
     * @return true if the document was successfully opened, false otherwise
     */
    protected boolean open(URI uri) {
        try {
            setCurrentDocument(loadDocument(uri));
            return true;
        } catch (Exception e) {
            LOG.warn("error opening document", e);
            getApp().showErrorDialog(
                    i18n.format("fx.application.dialog.error.open.document.{0.name}", FxApplication.asText(uri)),
                    String.valueOf(e.getLocalizedMessage())
            );
            return false;
        }
    }

    /**
     * Retrieves the selected file extension filter to be used for the open file dialog.
     *
     * @return the selected {@link ExtensionFilter} for opening files
     */
    protected abstract ExtensionFilter selectedOpenFilter();

    /**
     * Retrieves the selected file extension filter to be used for the save file dialog.
     *
     * @return the selected {@link FileChooser.ExtensionFilter} for saving files
     */
    protected abstract ExtensionFilter selectedSaveFilter();

    /**
     * Saves the current document if it is available.
     * If the document's location is not set, delegates to the `saveAs()` method to prompt for a location.
     * Handles saving errors appropriately.
     *
     * @return true if the document was successfully saved, false otherwise
     */
    public boolean save() {
        D doc = getCurrentDocument().orElse(null);

        if (doc == null) {
            LOG.info("no document; not saving");
            return false;
        }

        if (!doc.hasLocation()) {
            LOG.debug("save: no URI set, delegating to saveAs()");
            return saveAs();
        }

        return saveDocumentAndHandleErrors(doc);
    }

    private boolean saveDocumentAndHandleErrors(D document) {
        return saveDocumentAndHandleErrors(document, document.getLocation());
    }

    /**
     * Retrieves a list of file extension filters to be used in an open file dialog.
     *
     * <p>This method returns a list that includes a single filter that allows all file types.
     * Applications should add their supported filters to the list.
     *
     * @return a list of {@link FileChooser.ExtensionFilter} with filters applied to the open file dialog
     */
    protected List<FileChooser.ExtensionFilter> openFilters() {
        List<FileChooser.ExtensionFilter> filters = new ArrayList<>();
        filters.add(getApp().getExtensionFilterAllFiles());
        return filters;
    }

    /**
     * Retrieves a list of file extension filters for saving files.
     *
     * <p>This method returns a list that includes a single filter that allows all file types.
     * Applications should add their supported filters to the list.
     *
     * @return a list of {@link FileChooser.ExtensionFilter} with filters applied to the save file dialog
     */
    protected List<FileChooser.ExtensionFilter> saveFilters() {
        List<FileChooser.ExtensionFilter> filters = new ArrayList<>();
        filters.add(getApp().getExtensionFilterAllFiles());
        return filters;
    }

    /**
     * Prompts the user with a "Save As" dialog to choose a location and filename to save the current document.
     * If the user confirms the operation and selects a file, the document is saved to the chosen location.
     *
     * @return true if the document was successfully saved, false otherwise
     */
    public boolean saveAs() {
        D document = getCurrentDocument().orElse(null);

        if (document == null) {
            LOG.info("no document; not saving as new document");
            return false;
        }

        Path initialDir = initialDir(document);

        Optional<Path> file = Dialogs
                .chooseFile(getApp().getStage())
                .initialDir(initialDir)
                .initialFileName("")
                .filter(saveFilters())
                .selectedFilter(selectedSaveFilter())
                .showSaveDialog();

        if (file.isEmpty()) {
            LOG.debug("saveAs(): no file was chosen");
            return false;
        }

        // save document content
        boolean rc = saveDocumentAndHandleErrors(document, file.get().toUri());

        if (rc) {
            setCurrentDocument(document);
        }

        return rc;
    }

    /**
     * Determine the parent folder to set for open/save dialogs.
     *
     * @param document the current document
     * @return the initial folder to set
     */
    private Path initialDir(@Nullable D document) {
        if (document == null) {
            getApp();
            return FxApplication.getUserHome();
        }

        Path parent = null;
        try {
            if (document.hasLocation()) {
                parent = document.getPath().getParent();
                LOG.debug("initialDir() - using parent folder of current document as parent: {}", parent);
            } else {
                String lastDocument = getApp().getPreference(PREF_DOCUMENT, "");
                if (lastDocument.isBlank()) {
                    getApp();
                    parent = FxApplication.getUserHome();
                    LOG.debug("initialDir() - last document location not set, using user home as parent: {}", parent);
                } else {
                    try {
                        Path path = Paths.get(URI.create(lastDocument));
                        parent = path.getParent();
                        LOG.debug("initialDir() - using last document location as parent: {}", parent);
                    } catch (IllegalArgumentException e) {
                        LOG.warn("could not retrieve last document location", e);
                        parent = FxApplication.getUserHome();
                    }
                }
            }
        } catch (IllegalStateException e) {
            // might for example be thrown by URI.create()
            LOG.warn("initialDir() - could not determine initial folder", e);
        }

        Path initialDir = parent;

        if (initialDir == null || !Files.isDirectory(initialDir)) {
            LOG.warn("initialDir() - initial directory invalid, using user home instead: {}", initialDir);
            getApp();
            initialDir = FxApplication.getUserHome();
        }

        return initialDir;
    }

    private boolean saveDocumentAndHandleErrors(D document, URI uri) {
        try {
            document.saveAs(uri);
            return true;
        } catch (Exception e) {
            LOG.warn("error saving document", e);
            getApp().showErrorDialog(
                    i18n.format("fx.application.dialog.error.save.{0.document}", FxApplication.asText(uri)),
                    e.getLocalizedMessage()
            );
            return false;
        }
    }

    /**
     * Creates a new document. This method is a placeholder and not yet implemented.
     *
     * <p>Subclasses should override this method to provide specific functionality
     * for creating a new document.
     *
     * @throws UnsupportedOperationException indicating that the method needs to be implemented.
     */
    @SuppressWarnings("static-method")
    protected void createDocument() {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * Loads a document from the specified URI.
     *
     * <p>Subclasses should override this method to provide specific functionality
     * for creating a new document.
     *
     * @param uri the URI from which to load the document
     * @return the document loaded from the specified URI
     * @throws IOException if an I/O error occurs while loading the document
     * @throws UnsupportedOperationException indicating that the method needs to be implemented.
     */
    @SuppressWarnings({"static-method", "unused", "RedundantThrows"})
    protected D loadDocument(URI uri) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * Sets the status text for the application.
     *
     * @param s the status text to be set
     */
    public void setStatusText(String s) {
        LOG.debug("status: {}", s);
    }

    /**
     * Retrieves the current directory based on the current document's location.
     * If the current document's location is available, it returns the parent directory of the document's path.
     * If not, it defaults to the user's home directory.
     *
     * @return the current directory if available, otherwise the user's home directory
     */
    public Path getCurrentDir() {
        FxDocument document = getCurrentDocument().orElse(null);
        if (document != null && document.hasLocation()) {
            try {
                Path parent = document.getPath().getParent();
                if (parent != null) {
                    return parent;
                }
            } catch (UnsupportedOperationException e) {
                LOG.warn("cannot get current directory", e);
            }
        }
        LOG.warn("using user home");
        return FxApplication.getUserHome();
    }

    /**
     * Test if document is set.
     *
     * @return true, if document is set
     */
    public boolean hasCurrentDocument() {
        return currentDocumentProperty.get() != null;
    }

    /**
     * Get the App instance.
     *
     * @return the App instance
     * @throws IllegalStateException if called before the App instance was set
     */
    public A getApp() {
        if (app == null) {
            throw new IllegalStateException("App instance was not yet set");
        }
        return app;
    }

    /**
     * Set application instance.
     * <p>
     * This method must be called exactly once!
     *
     * @param app the application instance
     */
    void setApp(A app) {
        LangUtil.check(this.app == null, "app instance was already set");
        this.app = app;
        init(app);
    }

    /**
     * Initializes the controller with the given application instance.
     * This method is protected and meant to be overridden by subclasses
     * to perform specific initialization tasks. The default implementation
     * does nothing.
     *
     * @param app the application instance to be associated with this controller
     */
    protected void init(A app) {
        // do nothing in the default implementation
    }

}
